/* ============================================================================================
   features.js — optional "power-user" layer for the Learning Hub.
   ============================================================================================
   Loaded AFTER app.js, so every top-level binding in app.js (state, el, getJSON, postJSON,
   escapeHtml, openProblemByPath, showToast, currentTheme, categoryForPath, difficultyPill, …)
   is already in the shared global scope and reused here — no duplication.

   app.js calls three optional hooks on window.HubFeatures:
     • onSolvePanel({path, panel, meta, getCode, setCode, cm}) — after a Solve panel mounts.
     • onRunResult(path, mode, res, panel)                     — after every Run / Estimate.
     • onDashboard(catId, container)                           — after the problems dashboard renders.

   Everything here FAILS OPEN: if a backend endpoint is missing or errors, the core app is
   unaffected. This keeps the base experience intact even if the feature backend is down.

   Features implemented (Tiers 1–3 of the roadmap):
     1  Spaced-repetition review queue (SM-2)      8  Custom test input + reveal failing cases
     2  Submission history + diff                  9  Complexity self-report vs. measured
     3  Time-to-solve + attempt counter           10  Topic-level mastery dashboard
     4  Command palette + keyboard shortcuts      11  Export / import progress
     5  Per-problem markdown notes                12  Progressive hint reveal (naive→better→optimal)
     6  Random / interview-simulation mode        13  Print-friendly cheat-sheet view
     7  Streaks + activity heatmap                14  Theme picker + persistence (4 themes)
   ============================================================================================ */
(function () {
  "use strict";

  // ---- tiny helpers -------------------------------------------------------------------------
  const h = (html) => { const t = document.createElement("template"); t.innerHTML = html.trim(); return t.content.firstElementChild; };
  const esc = (s) => (typeof escapeHtml === "function" ? escapeHtml(s) : String(s ?? ""));
  const toast = (m) => (typeof showToast === "function" ? showToast(m) : console.log(m));
  async function safeGet(url) { try { return await getJSON(url); } catch (_) { return null; } }
  async function safePost(url, body) { try { return await postJSON(url, body); } catch (_) { return null; } }

  const COMPLEXITY_OPTS = ["O(1)", "O(log n)", "O(n)", "O(n log n)", "O(n^2)", "O(n^3)", "O(2^n)", "O(n!)"];
  const REVEAL_AFTER = 3; // failed runs before offering to reveal a hidden test

  // The currently-open Solve panel context (only one is ever mounted at a time).
  let ctx = null;

  /* ==========================================================================================
     Hook 1 — Solve panel augmentation
     ========================================================================================== */
  function onSolvePanel(c) {
    ctx = Object.assign({}, c, { openedAt: Date.now(), failed: 0, attempts: 0, solvedThisSession: false, self: "" });
    const body = c.panel.querySelector(".judge-body");
    const actions = c.panel.querySelector(".judge-actions");
    if (!body || !actions) return;

    // A toolbar of extra actions + a live "attempts / best time" chip + a self-report select.
    const bar = h(`
      <div class="feat-bar">
        <button class="feat-btn" data-feat="notes" type="button">📝 Notes</button>
        <button class="feat-btn" data-feat="custom" type="button">⚗ Custom test</button>
        <button class="feat-btn" data-feat="history" type="button">🕘 History</button>
        <button class="feat-btn" data-feat="hints" type="button">💡 Hints</button>
        <button class="feat-btn" data-feat="print" type="button">🖨 Print</button>
        <label class="cx-self" title="Guess the time complexity, then Estimate to compare">
          <span>My Big-O:</span>
          <select class="cx-self-sel">
            <option value="">—</option>
            ${COMPLEXITY_OPTS.map((o) => `<option value="${o}">${o}</option>`).join("")}
          </select>
        </label>
        <span class="feat-stat" hidden></span>
      </div>
      `);
    actions.after(bar);

    // A stack of lazily-created collapsible panels lives right below the toolbar.
    const stack = h(`<div class="feat-stack"></div>`);
    bar.after(stack);
    ctx.stack = stack;
    ctx.statEl = bar.querySelector(".feat-stat");
    ctx.selfSel = bar.querySelector(".cx-self-sel");
    ctx.selfSel.onchange = () => { ctx.self = ctx.selfSel.value; };

    bar.querySelectorAll(".feat-btn").forEach((b) => {
      b.onclick = () => togglePanel(b.dataset.feat, b);
    });

    // Preload stats (attempts / best time) and notes existence in the background.
    hydrateStats(c.path);
  }

  function togglePanel(name, btn) {
    const stack = ctx.stack;
    let p = stack.querySelector(`.feat-panel[data-p="${name}"]`);
    if (p) { const open = p.hidden; stack.querySelectorAll(".feat-panel").forEach((x) => (x.hidden = true)); p.hidden = !open; if (!p.hidden) focusPanel(name, p); return; }
    stack.querySelectorAll(".feat-panel").forEach((x) => (x.hidden = true));
    p = h(`<div class="feat-panel" data-p="${name}"></div>`);
    stack.appendChild(p);
    buildPanel(name, p);
  }

  function focusPanel(name, p) {
    if (name === "notes") p.querySelector(".feat-notes")?.focus();
    if (name === "custom") p.querySelector(".ct-input")?.focus();
  }

  function buildPanel(name, p) {
    if (name === "notes") return buildNotes(p);
    if (name === "custom") return buildCustom(p);
    if (name === "history") return buildHistory(p);
    if (name === "hints") return buildHints(p);
    if (name === "print") { p.hidden = true; printProblem(); }
  }

  /* ---- 5. Notes --------------------------------------------------------------------------- */
  async function buildNotes(p) {
    p.innerHTML = `<div class="feat-h">📝 Your notes <span class="feat-sub">(markdown, saved automatically)</span></div>
      <textarea class="feat-notes" placeholder="Jot down the key idea, edge cases, recurrence, why the optimal works…"></textarea>
      <div class="feat-notes-status"></div>`;
    const ta = p.querySelector(".feat-notes");
    const status = p.querySelector(".feat-notes-status");
    const data = await safeGet(`/api/notes?path=${encodeURIComponent(ctx.path)}`);
    ta.value = (data && data.text) || "";
    let timer = null;
    ta.oninput = () => {
      status.textContent = "editing…";
      clearTimeout(timer);
      timer = setTimeout(async () => {
        const r = await safePost("/api/notes", { path: ctx.path, text: ta.value });
        status.textContent = r ? "✓ saved" : "⚠ not saved";
        setTimeout(() => (status.textContent = ""), 1500);
      }, 700);
    };
    ta.focus();
  }

  /* ---- 8. Custom test input --------------------------------------------------------------- */
  function buildCustom(p) {
    p.innerHTML = `<div class="feat-h">⚗ Run your own input <span class="feat-sub">(JSON array of the arguments, e.g. <code>[[2,7,11,15], 9]</code>)</span></div>
      <textarea class="ct-input" placeholder="[[2,7,11,15], 9]" spellcheck="false"></textarea>
      <div class="feat-row"><button class="feat-run-btn ct-run" type="button">▶ Run custom</button></div>
      <div class="ct-out"></div>`;
    const input = p.querySelector(".ct-input");
    const out = p.querySelector(".ct-out");
    p.querySelector(".ct-run").onclick = async () => {
      out.innerHTML = `<p class="hint">Running…</p>`;
      let parsed;
      try { parsed = JSON.parse(input.value); } catch (e) { out.innerHTML = `<p class="error">Invalid JSON: ${esc(e.message)}</p>`; return; }
      if (!Array.isArray(parsed)) { out.innerHTML = `<p class="error">Input must be a JSON array of arguments.</p>`; return; }
      const res = await safePost("/api/judge/custom", { path: ctx.path, code: ctx.getCode(), input: input.value });
      if (!res) { out.innerHTML = `<p class="error">Custom run failed (endpoint unavailable).</p>`; return; }
      if (res.ok === false || res.error) {
        out.innerHTML = `<p class="error">Error:</p><pre class="judge-stderr">${esc(res.error || res.message || "unknown")}</pre>`
          + (res.stdout ? `<div class="judge-console"><div class="jc-h">🖨 console</div><pre>${esc(res.stdout)}</pre></div>` : "");
        return;
      }
      out.innerHTML = `<div class="ct-result"><span class="jc-h">↩ returned</span><pre><code>${esc(JSON.stringify(res.result))}</code></pre></div>`
        + (res.stdout ? `<div class="judge-console"><div class="jc-h">🖨 console</div><pre>${esc(res.stdout)}</pre></div>` : "");
    };
    input.focus();
  }

  /* ---- 2. Submission history + diff ------------------------------------------------------- */
  async function buildHistory(p) {
    p.innerHTML = `<div class="feat-h">🕘 Submission history <span class="feat-sub">(your accepted submissions, newest first)</span></div>
      <div class="sh-list"><p class="hint">Loading…</p></div>
      <div class="sh-diff" hidden></div>`;
    const list = p.querySelector(".sh-list");
    const diff = p.querySelector(".sh-diff");
    const data = await safeGet(`/api/judge/history?path=${encodeURIComponent(ctx.path)}`);
    const items = (data && data.history) || [];
    if (!items.length) { list.innerHTML = `<p class="hint">No accepted submissions yet. Pass all tests to build history.</p>`; return; }
    list.innerHTML = items.map((it, i) =>
      `<button class="sh-item" data-i="${i}" type="button"><span class="sh-when">${esc(fmtWhen(it.updatedAt))}</span><span class="sh-tag">${it.passed === false ? "attempt" : "accepted"}</span></button>`
    ).join("");
    list.querySelectorAll(".sh-item").forEach((b) => {
      b.onclick = () => {
        list.querySelectorAll(".sh-item").forEach((x) => x.classList.remove("active"));
        b.classList.add("active");
        const it = items[+b.dataset.i];
        diff.hidden = false;
        diff.innerHTML = `<div class="feat-sub">Diff: this submission (left/−) vs. your current editor (right/+)</div>${renderDiff(it.code || "", ctx.getCode())}`;
      };
    });
  }

  /* ---- 12. Progressive hint reveal -------------------------------------------------------- */
  function buildHints(p) {
    const sols = (ctx.meta && ctx.meta.solutions) || {};
    const order = ["naive", "better", "optimal"].filter((k) => sols[k]);
    if (!order.length) { p.innerHTML = `<div class="feat-h">💡 Hints</div><p class="hint">No reference solutions available for this problem.</p>`; return; }
    const labels = { naive: "① Naive idea", better: "② Better", optimal: "③ Optimal" };
    p.innerHTML = `<div class="feat-h">💡 Progressive hints <span class="feat-sub">(reveal one level at a time — try before you peek)</span></div>
      <div class="hint-cards">${order.map((k) =>
        `<div class="hint-card" data-k="${k}">
           <div class="hint-card-h">${labels[k]}</div>
           <pre class="hint-code blurred"><code class="language-python">${esc(sols[k])}</code></pre>
           <button class="hint-reveal" type="button">Reveal ${k}</button>
         </div>`).join("")}</div>`;
    p.querySelectorAll(".hint-card").forEach((card) => {
      const pre = card.querySelector(".hint-code");
      const btn = card.querySelector(".hint-reveal");
      btn.onclick = () => {
        pre.classList.toggle("blurred");
        const shown = !pre.classList.contains("blurred");
        btn.textContent = shown ? "Hide" : `Reveal ${card.dataset.k}`;
        if (shown && window.hljs) card.querySelectorAll("pre code").forEach((c) => hljs.highlightElement(c));
      };
    });
  }

  /* ==========================================================================================
     Hook 2 — after every Run / Estimate
     ========================================================================================== */
  function onRunResult(path, mode, res, panel) {
    if (!ctx || ctx.path !== path) return;
    const section = state.activeCategory;

    if (mode === "complexity") {
      const measured = res && (res.timeComplexity || res.timeGuess);
      if (ctx.self && measured) {
        safePost("/api/stats/selfreport", { path, self: ctx.self, measured });
        const norm = (x) => String(x || "").replace(/\s+/g, "").toLowerCase();
        const hit = norm(ctx.self) === norm(measured);
        const line = h(`<p class="cx-compare ${hit ? "ok" : "miss"}">${hit ? "✓" : "✗"} You guessed <b>${esc(ctx.self)}</b>, measured <b>${esc(measured)}</b>${hit ? " — nice!" : "."}</p>`);
        panel.querySelector(".judge-results")?.appendChild(line);
      }
      return;
    }

    // mode === "run"
    ctx.attempts++;
    const allPassed = res && res.summary && res.summary.allPassed;
    if (allPassed) {
      const elapsed = ctx.solvedThisSession ? 0 : Date.now() - ctx.openedAt;
      safePost("/api/stats/attempt", { path, section, solved: true, elapsedMs: elapsed }).then((r) => paintStat(r, elapsed));
      if (!ctx.solvedThisSession) { ctx.solvedThisSession = true; showReviewPrompt(path, section, panel); }
    } else {
      ctx.failed++;
      safePost("/api/stats/attempt", { path, section, solved: false, elapsedMs: 0 }).then((r) => paintStat(r, 0));
      if (ctx.failed >= REVEAL_AFTER) offerReveal(path, panel);
    }
  }

  async function hydrateStats(path) {
    const s = await safeGet(`/api/stats?path=${encodeURIComponent(path)}`);
    if (s && ctx && ctx.path === path) paintStat({ attempts: s.attempts, bestTimeMs: s.bestTimeMs }, 0, s.firstSolvedAt);
  }

  function paintStat(r, elapsed, firstSolvedAt) {
    if (!ctx || !ctx.statEl || !r) return;
    const a = r.attempts != null ? r.attempts : ctx.attempts;
    const best = r.bestTimeMs || (elapsed > 0 ? elapsed : 0);
    const parts = [];
    if (a) parts.push(`${a} attempt${a === 1 ? "" : "s"}`);
    if (best) parts.push(`best ${fmtDuration(best)}`);
    if (parts.length) { ctx.statEl.hidden = false; ctx.statEl.textContent = parts.join(" · "); }
  }

  /* ---- 1. Review prompt (SM-2) after solving ---------------------------------------------- */
  function showReviewPrompt(path, section, panel) {
    const results = panel.querySelector(".judge-results");
    if (!results || results.querySelector(".review-prompt")) return;
    const map = { Again: 2, Hard: 3, Good: 4, Easy: 5 };
    const box = h(`<div class="review-prompt">
      <span class="rp-label">🗓 Schedule a review:</span>
      ${Object.keys(map).map((k) => `<button class="rev-btn" data-q="${map[k]}" type="button">${k}</button>`).join("")}
    </div>`);
    box.querySelectorAll(".rev-btn").forEach((b) => {
      b.onclick = async () => {
        const r = await safePost("/api/reviews/grade", { path, section, quality: +b.dataset.q });
        if (r && r.dueDate) toast(`🗓 Review scheduled for ${r.dueDate} (in ${r.intervalDays}d)`);
        box.innerHTML = `<span class="rp-label">✓ Review scheduled${r && r.dueDate ? ` for ${esc(r.dueDate)}` : ""}.</span>`;
      };
    });
    results.appendChild(box);
  }

  /* ---- 8. Reveal a hidden failing test ---------------------------------------------------- */
  async function offerReveal(path, panel) {
    const results = panel.querySelector(".judge-results");
    if (!results || results.querySelector(".reveal-box")) return;
    const box = h(`<div class="reveal-box"><button class="feat-btn reveal-btn" type="button">🔍 Reveal a hidden test (${ctx.failed} tries)</button><div class="reveal-out"></div></div>`);
    results.appendChild(box);
    box.querySelector(".reveal-btn").onclick = async () => {
      const data = await safeGet(`/api/judge/reveal?path=${encodeURIComponent(path)}&n=2`);
      const out = box.querySelector(".reveal-out");
      const tests = (data && data.tests) || [];
      if (!tests.length) { out.innerHTML = `<p class="hint">No hidden tests to reveal.</p>`; return; }
      out.innerHTML = tests.map((t) =>
        `<div class="reveal-tc"><div class="jc-h">${esc(t.id || t.kind || "test")}</div>
          <div class="judge-diff"><span>input: <code>${esc(JSON.stringify(t.input))}</code></span><span>expected: <code>${esc(JSON.stringify(t.expected))}</code></span></div></div>`
      ).join("");
    };
  }

  /* ==========================================================================================
     Hook 3 — dashboard widgets
     ========================================================================================== */
  async function onDashboard(catId, container) {
    if (!container) return;
    const widgets = h(`<div class="dash-widgets"></div>`);
    const head = container.querySelector(".dash-head");
    if (head) head.after(widgets); else container.prepend(widgets);

    renderModeBar(widgets, catId);      // 6. random / interview mode
    renderReviewQueue(widgets);         // 1. due reviews
    renderHeatmap(widgets);             // 7. streaks + activity heatmap
    renderMastery(widgets, catId);      // 10. topic mastery
  }

  /* ---- 6. Random / interview-simulation mode ---------------------------------------------- */
  function renderModeBar(root, catId) {
    const bar = h(`<div class="widget mode-bar">
      <button class="feat-btn" data-m="random" type="button">🎲 Random unsolved</button>
      <button class="feat-btn" data-m="interview" type="button">🧑‍💼 Interview set (E·M·H)</button>
      <button class="feat-btn" data-m="cheat" type="button">🖨 Cheat sheet</button>
      <span class="mode-note"></span>
    </div>`);
    root.appendChild(bar);
    const note = bar.querySelector(".mode-note");
    bar.querySelector('[data-m="random"]').onclick = () => {
      const pick = randomUnsolved();
      if (pick) openProblemByPath(pick); else note.textContent = "🎉 All solved in this section!";
    };
    bar.querySelector('[data-m="interview"]').onclick = () => startInterview(note);
    bar.querySelector('[data-m="cheat"]').onclick = () => cheatSheet(catId);
  }

  function unsolvedByDiff(diff) {
    const out = [];
    state.problemIndex.forEach((m, path) => {
      if (state.completed.has(path)) return;
      if (diff && (m.difficulty || "").toLowerCase() !== diff) return;
      out.push(path);
    });
    return out;
  }
  function randomUnsolved(diff) {
    const pool = unsolvedByDiff(diff);
    return pool.length ? pool[Math.floor(Math.random() * pool.length)] : null;
  }
  function startInterview(note) {
    const set = ["easy", "medium", "hard"].map((d) => randomUnsolved(d)).filter(Boolean);
    if (!set.length) { note.textContent = "No unsolved problems left."; return; }
    sessionStorage.setItem("hub-interview", JSON.stringify(set));
    toast(`🧑‍💼 Interview set: ${set.length} problems queued`);
    openProblemByPath(set[0]);
  }

  /* ---- 1. Due-for-review queue ------------------------------------------------------------ */
  async function renderReviewQueue(root) {
    const data = await safeGet("/api/reviews/due");
    const due = (data && data.due) || [];
    if (!due.length) return;
    const rows = due.slice(0, 12).map((d) => {
      const meta = state.problemIndex.get(d.path);
      const title = meta ? meta.title : d.path.split("/").pop().replace(/\.md$/, "");
      return `<button class="rq-item" data-path="${esc(d.path)}" type="button"><span class="rq-title">${esc(title)}</span><span class="rq-due">due ${esc(d.dueDate)}</span></button>`;
    }).join("");
    const w = h(`<div class="widget rev-queue"><div class="widget-h">🗓 Due for review <span class="widget-badge">${due.length}</span></div><div class="rq-list">${rows}</div></div>`);
    root.appendChild(w);
    w.querySelectorAll(".rq-item").forEach((b) => (b.onclick = () => openProblemByPath(b.dataset.path)));
  }

  /* ---- 7. Streak + activity heatmap ------------------------------------------------------- */
  async function renderHeatmap(root) {
    const data = await safeGet("/api/stats/activity");
    if (!data) return;
    const days = data.days || {};
    const cells = [];
    const today = new Date();
    const WEEKS = 18;
    // Start on the Sunday WEEKS weeks ago so columns align to calendar weeks.
    const start = new Date(today);
    start.setDate(start.getDate() - (WEEKS * 7 - 1));
    start.setDate(start.getDate() - start.getDay());
    for (let d = new Date(start); d <= today; d.setDate(d.getDate() + 1)) {
      const key = d.toISOString().slice(0, 10);
      const n = days[key] || 0;
      const lvl = n === 0 ? 0 : n < 2 ? 1 : n < 4 ? 2 : n < 6 ? 3 : 4;
      cells.push(`<div class="hm-cell l${lvl}" title="${key}: ${n} solved"></div>`);
    }
    const w = h(`<div class="widget heat">
      <div class="widget-h">🔥 Activity
        <span class="streak-chip">Streak ${data.currentStreak || 0}d</span>
        <span class="streak-chip alt">Longest ${data.longestStreak || 0}d</span>
        <span class="streak-chip alt">Total ${data.totalSolved || 0}</span>
      </div>
      <div class="heatmap">${cells.join("")}</div>
      <div class="hm-legend"><span>less</span><div class="hm-cell l0"></div><div class="hm-cell l1"></div><div class="hm-cell l2"></div><div class="hm-cell l3"></div><div class="hm-cell l4"></div><span>more</span></div>
    </div>`);
    root.appendChild(w);
  }

  /* ---- 10. Topic-level mastery ------------------------------------------------------------ */
  function renderMastery(root, catId) {
    const topics = new Map(); // topic -> {done, total}
    state.problemIndex.forEach((m, path) => {
      const topic = (m.topic || "misc").replace(/^(google|faang)\//, "");
      const t = topics.get(topic) || { done: 0, total: 0 };
      t.total++;
      if (state.completed.has(path)) t.done++;
      topics.set(topic, t);
    });
    if (!topics.size) return;
    const arr = [...topics.entries()].map(([topic, t]) => ({ topic, ...t, pct: t.total ? t.done / t.total : 0 }));
    arr.sort((a, b) => a.pct - b.pct || b.total - a.total);
    const weakest = new Set(arr.filter((x) => x.done < x.total).slice(0, 3).map((x) => x.topic));
    const rows = arr.slice().sort((a, b) => a.topic.localeCompare(b.topic)).map((x) => {
      const pct = Math.round(x.pct * 100);
      const weak = weakest.has(x.topic) ? " weak" : "";
      return `<div class="mastery-row${weak}"><span class="m-topic">${esc(x.topic)}${weak ? ' <span class="weak-tag">weak</span>' : ""}</span>
        <div class="m-track"><div class="m-fill" style="width:${pct}%"></div></div>
        <span class="m-count">${x.done}/${x.total}</span></div>`;
    }).join("");
    const w = h(`<div class="widget mastery"><div class="widget-h">📊 Topic mastery <span class="widget-sub">(weakest highlighted)</span></div><div class="mastery-list">${rows}</div></div>`);
    root.appendChild(w);
  }

  /* ==========================================================================================
     13. Print-friendly views
     ========================================================================================== */
  function printProblem() {
    document.body.classList.add("printing-problem");
    const done = () => { document.body.classList.remove("printing-problem"); window.removeEventListener("afterprint", done); };
    window.addEventListener("afterprint", done);
    window.print();
  }

  function cheatSheet(catId) {
    const cat = state.categories.find((c) => c.id === catId);
    const label = cat ? cat.label : catId;
    const topics = new Map();
    state.problemIndex.forEach((m, path) => {
      const topic = (m.topic || "misc").replace(/^(google|faang)\//, "");
      if (!topics.has(topic)) topics.set(topic, []);
      topics.get(topic).push({ path, ...m, solved: state.completed.has(path) });
    });
    const sections = [...topics.entries()].sort((a, b) => a[0].localeCompare(b[0])).map(([topic, items]) => {
      const li = items.map((it) => `<li>${it.solved ? "✔" : "☐"} ${esc(it.title || it.path.split("/").pop())} <em>${esc(it.difficulty || "")}</em></li>`).join("");
      return `<section><h2>${esc(topic)} <small>(${items.filter((i) => i.solved).length}/${items.length})</small></h2><ul>${li}</ul></section>`;
    }).join("");
    const overlay = h(`<div class="cheat-overlay">
      <div class="cheat-bar no-print"><strong>🖨 ${esc(label)} — cheat sheet</strong>
        <span><button class="feat-btn cheat-print" type="button">Print</button>
        <button class="feat-btn cheat-close" type="button">Close</button></span></div>
      <div class="cheat-doc"><h1>${esc(label)} — Cheat Sheet</h1>${sections}</div>
    </div>`);
    document.body.appendChild(overlay);
    overlay.querySelector(".cheat-close").onclick = () => overlay.remove();
    overlay.querySelector(".cheat-print").onclick = () => { document.body.classList.add("printing-cheat"); const done = () => { document.body.classList.remove("printing-cheat"); window.removeEventListener("afterprint", done); }; window.addEventListener("afterprint", done); window.print(); };
  }

  /* ==========================================================================================
     4. Command palette + keyboard shortcuts
     ========================================================================================== */
  let paletteEl = null;

  function actionCommands() {
    return [
      { icon: "🎲", label: "Random unsolved problem", run: () => { const p = randomUnsolved(); if (p) openProblemByPath(p); else toast("All solved here!"); } },
      { icon: "🖨", label: "Print current problem", run: () => printProblem() },
      { icon: "🎨", label: "Theme: Dark", run: () => setTheme("dark") },
      { icon: "🎨", label: "Theme: Light", run: () => setTheme("light") },
      { icon: "🎨", label: "Theme: Midnight", run: () => setTheme("midnight") },
      { icon: "🎨", label: "Theme: Solarized", run: () => setTheme("solarized") },
      { icon: "⬇", label: "Export my progress (JSON)", run: () => exportData() },
      { icon: "⬆", label: "Import progress (JSON)", run: () => importData() },
      { icon: "⌨", label: "Keyboard shortcuts", run: () => showShortcuts() },
    ];
  }

  function openPalette() {
    if (paletteEl) return;
    const items = [];
    state.problemIndex.forEach((m, path) => items.push({ icon: "📄", label: m.title || path.split("/").pop(), sub: (m.topic || "").replace(/^(google|faang)\//, ""), run: () => openProblemByPath(path) }));
    const commands = actionCommands().map((c) => Object.assign({ sub: "action" }, c));
    const all = commands.concat(items);

    paletteEl = h(`<div class="cmdk"><div class="cmdk-box">
      <input class="cmdk-input" type="text" placeholder="Jump to a problem or run a command…" autocomplete="off" spellcheck="false" />
      <div class="cmdk-list"></div>
      <div class="cmdk-foot"><kbd>↑</kbd><kbd>↓</kbd> navigate · <kbd>↵</kbd> open · <kbd>esc</kbd> close</div>
    </div></div>`);
    document.body.appendChild(paletteEl);
    const input = paletteEl.querySelector(".cmdk-input");
    const list = paletteEl.querySelector(".cmdk-list");
    let active = 0, shown = [];

    const paint = () => {
      const q = input.value.trim().toLowerCase();
      shown = (q ? all.map((c) => ({ c, s: fuzzy(q, (c.label + " " + (c.sub || "")).toLowerCase()) })).filter((x) => x.s > -Infinity).sort((a, b) => b.s - a.s).map((x) => x.c) : all).slice(0, 60);
      active = 0;
      list.innerHTML = shown.map((c, i) =>
        `<div class="cmdk-item${i === 0 ? " active" : ""}" data-i="${i}"><span class="ck-ic">${c.icon || "•"}</span><span class="ck-label">${esc(c.label)}</span><span class="ck-sub">${esc(c.sub || "")}</span></div>`
      ).join("") || `<div class="cmdk-empty">No matches</div>`;
      list.querySelectorAll(".cmdk-item").forEach((it) => {
        it.onclick = () => choose(+it.dataset.i);
        it.onmousemove = () => setActive(+it.dataset.i);
      });
    };
    const setActive = (i) => { active = i; list.querySelectorAll(".cmdk-item").forEach((it, j) => it.classList.toggle("active", j === i)); };
    const choose = (i) => { const c = shown[i]; closePalette(); if (c) c.run(); };

    input.oninput = paint;
    input.onkeydown = (e) => {
      if (e.key === "ArrowDown") { e.preventDefault(); setActive(Math.min(active + 1, shown.length - 1)); ensureVisible(list); }
      else if (e.key === "ArrowUp") { e.preventDefault(); setActive(Math.max(active - 1, 0)); ensureVisible(list); }
      else if (e.key === "Enter") { e.preventDefault(); choose(active); }
      else if (e.key === "Escape") { e.preventDefault(); closePalette(); }
    };
    paletteEl.onclick = (e) => { if (e.target === paletteEl) closePalette(); };
    paint();
    input.focus();
  }
  function ensureVisible(list) { const a = list.querySelector(".cmdk-item.active"); if (a) a.scrollIntoView({ block: "nearest" }); }
  function closePalette() { if (paletteEl) { paletteEl.remove(); paletteEl = null; } }

  // Subsequence fuzzy match with a light contiguity bonus. Returns -Infinity on no match.
  function fuzzy(q, text) {
    let ti = 0, score = 0, streak = 0;
    for (let qi = 0; qi < q.length; qi++) {
      const ch = q[qi];
      let found = -1;
      for (let j = ti; j < text.length; j++) { if (text[j] === ch) { found = j; break; } }
      if (found === -1) return -Infinity;
      streak = found === ti ? streak + 1 : 0;
      score += 1 + streak - (found - ti) * 0.05;
      ti = found + 1;
    }
    return score;
  }

  function showShortcuts() {
    const overlay = h(`<div class="cmdk"><div class="cmdk-box shortcuts">
      <div class="feat-h">⌨ Keyboard shortcuts</div>
      <table class="sc-table">
        <tr><td><kbd>Ctrl</kbd>+<kbd>K</kbd></td><td>Command palette / jump to problem</td></tr>
        <tr><td><kbd>Ctrl</kbd>+<kbd>Enter</kbd></td><td>Run tests (in editor)</td></tr>
        <tr><td><kbd>Ctrl</kbd>+<kbd>/</kbd></td><td>Toggle comment (in editor)</td></tr>
        <tr><td><kbd>Ctrl</kbd>+<kbd>B</kbd></td><td>Toggle sidebar</td></tr>
        <tr><td><kbd>g</kbd> then <kbd>r</kbd></td><td>Go to a random unsolved problem</td></tr>
        <tr><td><kbd>?</kbd></td><td>Show this help</td></tr>
        <tr><td><kbd>Esc</kbd></td><td>Close overlays</td></tr>
      </table>
      <div class="cmdk-foot">Press <kbd>esc</kbd> to close</div>
    </div></div>`);
    document.body.appendChild(overlay);
    const close = () => overlay.remove();
    overlay.onclick = (e) => { if (e.target === overlay) close(); };
    overlay.tabIndex = -1; overlay.focus();
    overlay.onkeydown = (e) => { if (e.key === "Escape") close(); };
  }

  function inEditableTarget(e) {
    const t = e.target;
    return t && (t.tagName === "INPUT" || t.tagName === "TEXTAREA" || t.isContentEditable || t.closest(".CodeMirror"));
  }

  let gPending = false;
  function wireShortcuts() {
    document.addEventListener("keydown", (e) => {
      if ((e.ctrlKey || e.metaKey) && (e.key === "k" || e.key === "K")) { e.preventDefault(); paletteEl ? closePalette() : openPalette(); return; }
      if ((e.ctrlKey || e.metaKey) && (e.key === "b" || e.key === "B") && !inEditableTarget(e)) { e.preventDefault(); if (typeof toggleDrawer === "function") toggleDrawer(); return; }
      if (e.key === "Escape") { closePalette(); document.querySelectorAll(".cheat-overlay").forEach((o) => o.remove()); }
      if (inEditableTarget(e) || e.ctrlKey || e.metaKey || e.altKey) return;
      if (e.key === "?") { e.preventDefault(); showShortcuts(); return; }
      if (e.key === "g") { gPending = true; setTimeout(() => (gPending = false), 800); return; }
      if (e.key === "r" && gPending) { gPending = false; const p = randomUnsolved(); if (p) openProblemByPath(p); }
    });
  }

  /* ==========================================================================================
     11. Export / import progress
     ========================================================================================== */
  async function exportData() {
    const data = await safeGet("/api/data/export");
    if (!data) { toast("⚠ Export failed"); return; }
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: "application/json" });
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = `learning-hub-progress-${new Date().toISOString().slice(0, 10)}.json`;
    document.body.appendChild(a); a.click(); a.remove();
    URL.revokeObjectURL(a.href);
    toast("⬇ Progress exported");
  }
  function importData() {
    const inp = document.createElement("input");
    inp.type = "file"; inp.accept = "application/json,.json";
    inp.onchange = async () => {
      const file = inp.files && inp.files[0];
      if (!file) return;
      let doc;
      try { doc = JSON.parse(await file.text()); } catch (e) { toast("⚠ Invalid JSON"); return; }
      const r = await safePost("/api/data/import", doc);
      if (!r) { toast("⚠ Import failed"); return; }
      toast("⬆ Import complete — reloading");
      setTimeout(() => location.reload(), 900);
    };
    inp.click();
  }

  /* ==========================================================================================
     14. Theme picker + persistence (extends app.js's dark/light toggle to 4 themes)
     ========================================================================================== */
  const THEMES = { dark: "dark", light: "light", midnight: "dark", solarized: "light" }; // -> hljs family
  function setTheme(name) {
    if (!THEMES[name]) name = "dark";
    document.documentElement.setAttribute("data-theme", name);
    localStorage.setItem("hub-theme", name);
    applyThemeSideEffects(name);
    toast(`🎨 ${name[0].toUpperCase()}${name.slice(1)} theme`);
  }
  function applyThemeSideEffects(name) {
    const family = THEMES[name] || "dark";
    const dark = document.getElementById("hljs-dark");
    const light = document.getElementById("hljs-light");
    if (dark) dark.disabled = family !== "dark";
    if (light) light.disabled = family !== "light";
    if (state && state.cm) { try { state.cm.setOption("theme", family === "light" ? "neo" : "material-darker"); } catch (_) {} }
    if (window.mermaid) { try { window.mermaid.initialize({ startOnLoad: false, theme: family === "light" ? "default" : "dark", securityLevel: "loose" }); } catch (_) {} }
    const btn = document.getElementById("theme-toggle");
    if (btn) btn.textContent = family === "dark" ? "☀️" : "🌙";
  }

  /* ==========================================================================================
     small formatters + a minimal line diff
     ========================================================================================== */
  function fmtDuration(ms) {
    if (!ms) return "—";
    if (ms < 1000) return `${Math.round(ms)} ms`;
    const s = Math.round(ms / 1000);
    if (s < 60) return `${s}s`;
    const m = Math.floor(s / 60);
    return `${m}m ${s % 60}s`;
  }
  function fmtWhen(iso) {
    if (!iso) return "";
    const d = new Date(iso);
    return isNaN(d) ? String(iso) : d.toLocaleString();
  }

  // LCS-based unified line diff (inputs are tiny submissions).
  function renderDiff(a, b) {
    const A = a.split("\n"), B = b.split("\n");
    const n = A.length, m = B.length;
    const dp = Array.from({ length: n + 1 }, () => new Int32Array(m + 1));
    for (let i = n - 1; i >= 0; i--)
      for (let j = m - 1; j >= 0; j--)
        dp[i][j] = A[i] === B[j] ? dp[i + 1][j + 1] + 1 : Math.max(dp[i + 1][j], dp[i][j + 1]);
    const rows = [];
    let i = 0, j = 0;
    while (i < n && j < m) {
      if (A[i] === B[j]) { rows.push(["ctx", A[i]]); i++; j++; }
      else if (dp[i + 1][j] >= dp[i][j + 1]) { rows.push(["del", A[i]]); i++; }
      else { rows.push(["add", B[j]]); j++; }
    }
    while (i < n) rows.push(["del", A[i++]]);
    while (j < m) rows.push(["add", B[j++]]);
    const sym = { ctx: " ", del: "−", add: "+" };
    return `<pre class="diff-view">${rows.map(([k, line]) => `<span class="d-${k}">${sym[k]} ${esc(line)}</span>`).join("\n")}</pre>`;
  }

  /* ==========================================================================================
     init
     ========================================================================================== */
  function init() {
    // Correct hljs/CM/mermaid for custom themes app.js's dark/light-only applyTheme can't handle.
    const t = localStorage.getItem("hub-theme") || "dark";
    if (t === "midnight" || t === "solarized") applyThemeSideEffects(t);
    wireShortcuts();
  }

  window.HubFeatures = { onSolvePanel, onRunResult, onDashboard, setTheme, openPalette, init };
  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", init);
  else init();
})();
