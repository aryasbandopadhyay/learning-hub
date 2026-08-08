/* ============================================================================================
   Learning Hub — frontend logic (vanilla JS, no build step).
   Flow:
     1. GET /api/categories  -> render the tab bar (so new categories auto-appear as tabs).
     2. On tab click         -> GET /api/tree/{id} and render the file tree.
     3. On file click        -> GET /api/file?category=&path= and render:
          - Markdown files -> marked -> HTML, then Mermaid diagrams + code highlighting.
          - Code files     -> a single highlighted <pre><code> block.
   ============================================================================================ */

const state = {
  categories: [],
  activeCategory: null,
  activeFileEl: null,
  activePath: null,
  // Progress tracking (DSA/Google/FAANG only):
  completed: new Set(),   // completed problem paths for the active category
  problemPaths: new Set(),// all gradable/problem .md paths in the active category (denominator)
  fileEls: new Map(),     // path -> { el, isProblem } for live DOM updates
  progressOn: false,      // is the active category progress-tracked?
  problemIndex: new Map(),// path -> { title, difficulty, topic } for gradable problems (dashboard/pills)
  showingDashboard: false,// is the viewer currently showing the problems dashboard?
};

const el = {
  tabs: document.getElementById("tabs"),
  tree: document.getElementById("tree"),
  desc: document.getElementById("category-desc"),
  viewer: document.getElementById("viewer"),
  breadcrumb: document.getElementById("breadcrumb"),
  sidebar: document.getElementById("sidebar"),
  scrim: document.getElementById("scrim"),
  menuToggle: document.getElementById("menu-toggle"),
  themeToggle: document.getElementById("theme-toggle"),
  adminBtn: document.getElementById("admin-btn"),
  userChip: document.getElementById("user-chip"),
  userEmail: document.getElementById("user-email"),
  logoutBtn: document.getElementById("logout-btn"),
  treeSearch: document.getElementById("tree-search"),
  treeSearchWrap: document.getElementById("tree-search-wrap"),
};

// Categories whose markdown problems are judge-gradable AND progress-tracked.
const JUDGE_CATEGORIES = new Set(["dsa", "google", "faang"]);

// Map file extensions to highlight.js language names (best-effort; hljs also auto-detects).
const LANG = {
  java: "java", py: "python", kt: "kotlin", scala: "scala", groovy: "groovy",
  js: "javascript", ts: "typescript", jsx: "javascript", tsx: "typescript",
  go: "go", rs: "rust", rb: "ruby", php: "php", swift: "swift", cs: "csharp",
  c: "c", cpp: "cpp", cc: "cpp", h: "c", hpp: "cpp",
  xml: "xml", html: "xml", yml: "yaml", yaml: "yaml", json: "json",
  properties: "properties", toml: "ini", ini: "ini",
  sql: "sql", sh: "bash", bat: "dos", ps1: "powershell", gradle: "gradle",
  css: "css", txt: "plaintext",
};

async function getJSON(url) {
  const res = await fetch(url);
  if (!res.ok) {
    const msg = await res.text().catch(() => res.statusText);
    throw new Error(`${res.status}: ${msg}`);
  }
  return res.json();
}

/* ---- Tabs -------------------------------------------------------------------------------- */
async function init() {
  try {
    state.categories = await getJSON("/api/categories");
  } catch (e) {
    el.tree.innerHTML = `<p class="error">Failed to load categories: ${e.message}</p>`;
    return;
  }
  el.tabs.innerHTML = "";
  state.categories.forEach((c, i) => {
    const btn = document.createElement("button");
    btn.className = "tab";
    btn.textContent = c.label;
    btn.dataset.id = c.id;
    btn.onclick = () => selectCategory(c.id);
    el.tabs.appendChild(btn);
    if (i === 0) btn.classList.add("active");
  });
  if (state.categories.length) {
    selectCategory(state.categories[0].id);
  } else {
    el.tree.innerHTML = `<p class="hint">No categories configured.</p>`;
  }
}

async function selectCategory(id) {
  state.activeCategory = id;
  state.progressOn = JUDGE_CATEGORIES.has(id);
  state.completed = new Set();
  state.problemPaths = new Set();
  state.fileEls = new Map();
  state.problemIndex = new Map();
  state.showingDashboard = false;
  [...el.tabs.children].forEach((b) => b.classList.toggle("active", b.dataset.id === id));

  const cat = state.categories.find((c) => c.id === id);
  el.desc.textContent = cat && cat.description ? cat.description : "";

  removeProgressCard();
  // Toggle the sidebar problem filter (only meaningful for judge categories).
  if (el.treeSearchWrap) el.treeSearchWrap.hidden = !state.progressOn;
  if (el.treeSearch) el.treeSearch.value = "";
  el.tree.innerHTML = `<p class="hint">Loading…</p>`;

  // For judge categories, fetch the problem index first so the tree can show difficulty pills
  // and the dashboard has titles/difficulty/topic. (Runs in parallel with the tree fetch.)
  const treeP = getJSON(`/api/tree/${encodeURIComponent(id)}`);
  const indexP = state.progressOn
    ? getJSON(`/api/judge/index?section=${encodeURIComponent(id)}`).catch(() => null)
    : Promise.resolve(null);

  let root;
  try {
    root = await treeP;
  } catch (e) {
    el.tree.innerHTML = `<p class="error">Failed to load tree: ${e.message}</p>`;
    return;
  }
  const idx = await indexP;
  if (state.activeCategory !== id) return; // user switched tabs mid-flight
  if (idx && idx.problems) {
    idx.problems.forEach((p) => {
      state.problemIndex.set(p.path, { title: p.title, difficulty: p.difficulty, topic: p.topic });
    });
  }

  el.tree.innerHTML = "";
  const ul = document.createElement("ul");
  (root.children || []).forEach((child) => ul.appendChild(renderNode(child, true)));
  el.tree.appendChild(ul);

  // Progress bar (DSA/Google/FAANG): fetch completed set, then render card + tree badges.
  if (state.progressOn && state.problemPaths.size) {
    mountProgressCard(id);
    await loadProgress(id);
  }

  if (state.progressOn && state.problemIndex.size) {
    // Judge categories land on the problems dashboard (LeetCode/NeetCode-style overview).
    renderDashboard(id);
  } else {
    // Doc categories: auto-open the first README (or first file) so the pane isn't empty.
    const first = findFirstFile(root, true) || findFirstFile(root, false);
    if (first) openFile(first.path, first.name, first.ext);
    else resetViewer();
  }
}

/* ---- Difficulty helpers ------------------------------------------------------------------ */
function difficultyOf(path) {
  const meta = state.problemIndex.get(path);
  return meta ? meta.difficulty || "" : "";
}
function difficultyPill(diff) {
  if (!diff) return "";
  const cls = diff.toLowerCase(); // easy | medium | hard
  return `<span class="pill pill-${cls}">${escapeHtml(diff)}</span>`;
}

/* ---- Tree rendering ---------------------------------------------------------------------- */
function renderNode(node, topLevel) {
  const li = document.createElement("li");
  if (node.type === "dir") {
    const details = document.createElement("details");
    if (topLevel) details.open = true; // expand the top-level entries by default
    const summary = document.createElement("summary");
    summary.innerHTML = `<span class="ic">📁</span>${node.name}`;
    details.appendChild(summary);
    const ul = document.createElement("ul");
    (node.children || []).forEach((child) => ul.appendChild(renderNode(child, false)));
    details.appendChild(ul);
    li.appendChild(details);
  } else {
    const a = document.createElement("a");
    a.className = "file";
    a.href = "javascript:void(0)";
    // A "problem" is a non-README markdown file in a progress-tracked category.
    const isProblem =
      state.progressOn && node.ext === "md" && !node.name.toLowerCase().startsWith("readme");
    const badge = isProblem ? `<span class="done-mark" title="Toggle complete">✓</span>` : "";
    const diff = isProblem ? difficultyOf(node.path) : "";
    const dot = diff ? `<span class="diff-dot diff-${diff.toLowerCase()}" title="${escapeHtml(diff)}"></span>` : "";
    a.innerHTML = `${badge}<span class="ic">${iconFor(node.ext)}</span><span class="fname">${node.name}</span>${dot}`;
    a.dataset.name = (node.name || "").toLowerCase();
    a.onclick = () => {
      if (state.activeFileEl) state.activeFileEl.classList.remove("active");
      a.classList.add("active");
      state.activeFileEl = a;
      openFile(node.path, node.name, node.ext);
    };
    if (isProblem) {
      state.problemPaths.add(node.path);
      state.fileEls.set(node.path, { el: a, isProblem: true });
      // Clicking the badge toggles completion without opening the file.
      a.querySelector(".done-mark").onclick = (ev) => {
        ev.stopPropagation();
        toggleComplete(node.path);
      };
    }
    li.appendChild(a);
  }
  return li;
}

function iconFor(ext) {
  if (ext === "md" || ext === "markdown") return "📄";
  if (ext === "java") return "☕";
  if (ext === "py") return "🐍";
  if (ext === "xml" || ext === "yml" || ext === "yaml" || ext === "properties") return "⚙️";
  return "📃";
}

function findFirstFile(node, preferReadme) {
  if (node.type === "file") {
    if (!preferReadme) return node;
    return node.name.toLowerCase().startsWith("readme") ? node : null;
  }
  for (const child of node.children || []) {
    const found = findFirstFile(child, preferReadme);
    if (found) return found;
  }
  return null;
}

/* ---- Sidebar tree filter ----------------------------------------------------------------- */
function filterTree(term) {
  term = (term || "").trim().toLowerCase();
  const files = el.tree.querySelectorAll("a.file");
  files.forEach((a) => {
    const name = a.dataset.name || a.textContent.toLowerCase();
    const li = a.closest("li");
    if (li) li.style.display = !term || name.includes(term) ? "" : "none";
  });
  // Hide directory groups that have no visible files; open matching ones for visibility.
  el.tree.querySelectorAll("details").forEach((d) => {
    const anyVisible = [...d.querySelectorAll("li")].some(
      (li) => li.querySelector("a.file") && li.style.display !== "none"
    );
    const wrapLi = d.closest("li");
    if (wrapLi) wrapLi.style.display = !term || anyVisible ? "" : "none";
    if (term && anyVisible) d.open = true;
  });
}

/* ============================================================================================
   Problems dashboard — LeetCode/NeetCode-style overview for judge categories.
   A searchable, filterable table of every gradable problem (status · title · difficulty · topic).
   ============================================================================================ */
const dashState = { search: "", difficulty: "all", status: "all" };

function renderDashboard(catId) {
  state.showingDashboard = true;
  state.activePath = null;
  if (state.activeFileEl) { state.activeFileEl.classList.remove("active"); state.activeFileEl = null; }
  dashState.search = ""; dashState.difficulty = "all"; dashState.status = "all";
  el.breadcrumb.textContent = `${catId} · all problems`;

  const cat = state.categories.find((c) => c.id === catId);
  const label = cat ? cat.label : catId;
  const counts = { Easy: 0, Medium: 0, Hard: 0 };
  state.problemIndex.forEach((m) => { if (counts[m.difficulty] != null) counts[m.difficulty]++; });

  el.viewer.className = "viewer";
  el.viewer.innerHTML = `
    <div class="dashboard">
      <div class="dash-head">
        <div>
          <h1 class="dash-title">${escapeHtml(label)}</h1>
          <div class="dash-stats">
            <span class="pill pill-easy">Easy ${counts.Easy}</span>
            <span class="pill pill-medium">Medium ${counts.Medium}</span>
            <span class="pill pill-hard">Hard ${counts.Hard}</span>
            <span class="dash-total">${state.problemIndex.size} problems</span>
          </div>
        </div>
      </div>
      <div class="dash-controls">
        <input class="dash-search" type="search" placeholder="Search problems…" autocomplete="off" />
        <div class="dash-filters">
          <div class="chip-group" data-group="difficulty">
            <button class="chip active" data-val="all">All</button>
            <button class="chip" data-val="Easy">Easy</button>
            <button class="chip" data-val="Medium">Medium</button>
            <button class="chip" data-val="Hard">Hard</button>
          </div>
          <div class="chip-group" data-group="status">
            <button class="chip active" data-val="all">Any</button>
            <button class="chip" data-val="solved">Solved</button>
            <button class="chip" data-val="unsolved">Unsolved</button>
          </div>
        </div>
      </div>
      <div class="dash-table-wrap">
        <table class="dash-table">
          <thead><tr><th class="c-status">Status</th><th class="c-title">Title</th><th class="c-diff">Difficulty</th><th class="c-topic">Topic</th></tr></thead>
          <tbody class="dash-rows"></tbody>
        </table>
      </div>
      <p class="dash-empty hint" hidden>No problems match your filters.</p>
    </div>`;

  const searchInput = el.viewer.querySelector(".dash-search");
  searchInput.oninput = () => { dashState.search = searchInput.value.toLowerCase(); paintDashboardRows(); };
  el.viewer.querySelectorAll(".chip-group").forEach((grp) => {
    const group = grp.dataset.group;
    grp.querySelectorAll(".chip").forEach((chip) => {
      chip.onclick = () => {
        grp.querySelectorAll(".chip").forEach((c) => c.classList.remove("active"));
        chip.classList.add("active");
        dashState[group] = chip.dataset.val;
        paintDashboardRows();
      };
    });
  });
  paintDashboardRows();
}

function paintDashboardRows() {
  const tbody = el.viewer.querySelector(".dash-rows");
  const empty = el.viewer.querySelector(".dash-empty");
  if (!tbody) return;
  // Build a sorted list of [path, meta]; keep topic grouping (index is already topic-sorted).
  const rows = [];
  state.problemIndex.forEach((meta, path) => {
    if (dashState.difficulty !== "all" && meta.difficulty !== dashState.difficulty) return;
    const solved = state.completed.has(path);
    if (dashState.status === "solved" && !solved) return;
    if (dashState.status === "unsolved" && solved) return;
    const hay = `${meta.title} ${meta.topic}`.toLowerCase();
    if (dashState.search && !hay.includes(dashState.search)) return;
    rows.push([path, meta, solved]);
  });
  tbody.innerHTML = rows.map(([path, meta, solved]) => {
    const title = meta.title || path.split("/").pop().replace(/\.md$/, "");
    const topic = (meta.topic || "").replace(/^(google|faang)\//, "");
    return `<tr class="dash-row${solved ? " solved" : ""}" data-path="${escapeHtml(path)}">
      <td class="c-status">${solved ? '<span class="dash-check">✓</span>' : '<span class="dash-dash">○</span>'}</td>
      <td class="c-title">${escapeHtml(title)}</td>
      <td class="c-diff">${difficultyPill(meta.difficulty)}</td>
      <td class="c-topic"><span class="topic-tag">${escapeHtml(topic)}</span></td>
    </tr>`;
  }).join("");
  if (empty) empty.hidden = rows.length > 0;
  tbody.querySelectorAll(".dash-row").forEach((tr) => {
    tr.onclick = () => {
      const path = tr.dataset.path;
      const name = path.split("/").pop();
      const ref = state.fileEls.get(path);
      if (ref && ref.el) {
        if (state.activeFileEl) state.activeFileEl.classList.remove("active");
        ref.el.classList.add("active");
        state.activeFileEl = ref.el;
      }
      openFile(path, name, "md");
    };
  });
}

/* ---- File viewing ------------------------------------------------------------------------ */
async function openFile(path, name, ext) {
  el.breadcrumb.textContent = path;
  state.activePath = path;
  state.showingDashboard = false;
  closeDrawer(); // collapse the mobile sidebar once a file is chosen
  el.viewer.innerHTML = `<p class="hint">Loading ${name}…</p>`;
  let file;
  try {
    file = await getJSON(
      `/api/file?category=${encodeURIComponent(state.activeCategory)}&path=${encodeURIComponent(path)}`
    );
  } catch (e) {
    el.viewer.innerHTML = `<p class="error">Failed to load file: ${e.message}</p>`;
    return;
  }
  if (file.markdown) renderMarkdown(file.content);
  else renderCode(file.content, ext);
  el.content?.scrollTo?.(0, 0);
  document.querySelector(".content").scrollTop = 0;

  // DSA-style markdown files may have a local judge — offer a "Solve" panel if a manifest exists.
  if (file.markdown && JUDGE_CATEGORIES.has(state.activeCategory)) {
    maybeMountJudge(path);
  }
}

function renderMarkdown(md) {
  el.viewer.className = "viewer markdown-body";
  el.viewer.innerHTML = marked.parse(md);

  // 1) Convert ```mermaid code blocks into <div class="mermaid"> and render them.
  const mermaidBlocks = el.viewer.querySelectorAll("code.language-mermaid");
  const mermaidNodes = [];
  mermaidBlocks.forEach((code) => {
    const div = document.createElement("div");
    div.className = "mermaid";
    div.textContent = code.textContent; // already-decoded diagram source
    const pre = code.closest("pre");
    (pre || code).replaceWith(div);
    mermaidNodes.push(div);
  });
  if (mermaidNodes.length && window.mermaid) {
    try {
      window.mermaid.run({ nodes: mermaidNodes });
    } catch (e) {
      console.error("mermaid render failed", e);
    }
  }

  // 2) Highlight the remaining (non-mermaid) code blocks.
  el.viewer.querySelectorAll("pre code").forEach((code) => {
    if (!code.classList.contains("language-mermaid")) hljs.highlightElement(code);
  });

  // 3) Salesforce section: its DSA index links straight to main-bank problem paths. Since that
  //    section isn't itself progress-tracked, decorate each referenced path with a live
  //    "solved" badge sourced from the user's global (cross-section) progress.
  if (state.activeCategory === "salesforce") applySolvedBadges();
}

/** Append a "✔ solved" badge to inline-code problem paths already completed by the user. */
async function applySolvedBadges() {
  let done;
  try {
    done = new Set((await getJSON("/api/progress")).completed || []);
  } catch (e) {
    return; // progress disabled/unreachable — no badges
  }
  el.viewer.querySelectorAll("code").forEach((code) => {
    if (code.closest("pre")) return; // skip fenced code blocks
    const path = code.textContent.trim();
    if (!path.endsWith(".md") || !done.has(path)) return;
    if (code.nextElementSibling && code.nextElementSibling.classList.contains("sf-solved")) return;
    const badge = document.createElement("span");
    badge.className = "sf-solved";
    badge.textContent = "✔ solved";
    code.after(badge);
  });
}

function renderCode(content, ext) {
  el.viewer.className = "viewer code-view";
  const pre = document.createElement("pre");
  const code = document.createElement("code");
  const lang = LANG[ext] || "plaintext";
  code.className = `language-${lang}`;
  code.textContent = content; // textContent => safe (no HTML injection)
  pre.appendChild(code);
  el.viewer.innerHTML = "";
  el.viewer.appendChild(pre);
  hljs.highlightElement(code);
}

function resetViewer() {
  el.breadcrumb.textContent = "";
  el.viewer.className = "viewer markdown-body";
  el.viewer.innerHTML = `<p class="hint">Select a file on the left to view it.</p>`;
}

/* ============================================================================================
   Local DSA Online Judge
   For a DSA markdown problem, GET /api/judge/problem?path=… ; if a manifest exists we mount a
   "Solve" panel: a code editor prefilled with a function stub (the user writes ONLY the body;
   the runner auto-drives the call), Run + Estimate Complexity buttons, per-test results, and a
   side-by-side compare with the naive/better/optimal reference solutions.
   ============================================================================================ */
const escapeHtml = (s) =>
  String(s ?? "")
    .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;").replace(/'/g, "&#39;");

async function maybeMountJudge(path) {
  let meta;
  try {
    meta = await getJSON(`/api/judge/problem?path=${encodeURIComponent(path)}`);
  } catch (e) {
    return; // judge disabled or errored — silently skip
  }
  if (!meta || !meta.available) return;
  if (state.activePath !== path) return; // user navigated away while loading

  const idxMeta = state.problemIndex.get(path) || {};
  const title = idxMeta.title || path.split("/").pop().replace(/\.md$/, "");
  const difficulty = meta.difficulty || idxMeta.difficulty || "";
  const solved = state.completed.has(path);

  const savedSolution = await loadSavedSolution(path);
  if (state.activePath !== path) return; // user navigated away while loading the saved solution
  const panel = document.createElement("section");
  panel.className = "judge";
  panel.innerHTML = `
    <div class="judge-head">
      <span class="judge-title">🧪 Solve</span>
      <span class="judge-sub">${escapeHtml(meta.shape || "")}</span>
    </div>
    <div class="judge-body">
      <p class="judge-hint">Fill in the function body — the driver is auto-generated, so your method is called directly. <kbd>Ctrl</kbd>+<kbd>Enter</kbd> runs, <kbd>Ctrl</kbd>+<kbd>/</kbd> toggles a comment.</p>
      <div class="judge-saved${savedSolution.saved ? " on" : ""}">✓ Loaded your saved solution</div>
      <div class="judge-editor-host"></div>
      <div class="judge-actions">
        <button class="btn-run" type="button">▶ Run tests</button>
        <button class="btn-cx" type="button" ${meta.complexitySupported ? "" : "disabled title='Not available for this problem'"}>Σ Estimate complexity</button>
        <button class="btn-reset" type="button">↺ Reset</button>
        <button class="btn-compare" type="button">⇄ Compare solutions</button>
      </div>
      <div class="judge-results"></div>
      <div class="judge-compare" hidden></div>
    </div>`;

  // Sticky problem header (title · difficulty · solved badge · back-to-list).
  const header = document.createElement("div");
  header.className = "problem-topbar";
  header.innerHTML = `
    <button class="back-to-list" type="button" title="Back to all problems">← All problems</button>
    <h1 class="pt-title">${escapeHtml(title)}</h1>
    ${difficultyPill(difficulty)}
    <span class="pt-solved${solved ? " on" : ""}">✓ Solved</span>`;
  header.querySelector(".back-to-list").onclick = () => renderDashboard(state.activeCategory);

  // Reflow the viewer into two columns: problem statement (left) + Solve panel (right, sticky),
  // separated by a draggable divider. Reference solutions live further down the markdown, so the
  // user solves first, then reads.
  const layout = document.createElement("div");
  layout.className = "solve-layout";
  const problemCol = document.createElement("div");
  problemCol.className = "problem-col markdown-body";
  while (el.viewer.firstChild) problemCol.appendChild(el.viewer.firstChild);
  const divider = document.createElement("div");
  divider.className = "split-divider";
  divider.title = "Drag to resize";
  const solveCol = document.createElement("div");
  solveCol.className = "solve-col";
  solveCol.appendChild(panel);
  layout.appendChild(problemCol);
  layout.appendChild(divider);
  layout.appendChild(solveCol);
  el.viewer.className = "viewer has-solve";
  el.viewer.appendChild(header);
  el.viewer.appendChild(layout);

  wireDivider(divider, problemCol, solveCol, layout);

  // Mount CodeMirror into the editor host.
  const host = panel.querySelector(".judge-editor-host");
  const starter = meta.starterCode || "class Solution:\n    def solve(self):\n        pass\n";
  const cm = CodeMirror(host, {
    value: savedSolution.saved ? savedSolution.code : starter,
    mode: "python",
    theme: cmTheme(),
    lineNumbers: true,
    indentUnit: 4,
    tabSize: 4,
    indentWithTabs: false,
    matchBrackets: true,
    autoCloseBrackets: true,
    styleActiveLine: true,
    extraKeys: {
      "Ctrl-Enter": () => runJudge(path, cm.getValue(), "run", panel),
      "Cmd-Enter": () => runJudge(path, cm.getValue(), "run", panel),
      "Ctrl-/": (c) => c.toggleComment(),
      "Cmd-/": (c) => c.toggleComment(),
      Tab: (c) => c.execCommand("insertSoftTab"),
    },
  });
  state.cm = cm;
  setTimeout(() => cm.refresh(), 30); // ensure correct sizing after insertion

  const body = panel.querySelector(".judge-body");
  panel.querySelector(".btn-reset").onclick = () => { cm.setValue(meta.starterCode || ""); cm.focus(); };
  panel.querySelector(".btn-run").onclick = () => runJudge(path, cm.getValue(), "run", panel);
  panel.querySelector(".btn-cx").onclick = () => runJudge(path, cm.getValue(), "complexity", panel);
  panel.querySelector(".btn-compare").onclick = () => toggleCompare(panel, meta);
}

/** Load a previously accepted solution. Fail open so the starter stub still appears offline. */
async function loadSavedSolution(path) {
  try {
    const data = await getJSON(`/api/judge/solution?path=${encodeURIComponent(path)}`);
    return data && data.saved ? data : { saved: false };
  } catch (e) {
    return { saved: false };
  }
}

/** CodeMirror theme name matching the active app theme. */
function cmTheme() {
  return currentTheme() === "light" ? "neo" : "material-darker";
}

/** Draggable split divider: adjust the problem column's flex-basis on drag. */
function wireDivider(divider, problemCol, solveCol, layout) {
  let dragging = false;
  divider.addEventListener("mousedown", (e) => {
    // The split is horizontal only on wide screens (the layout stacks below 1100px).
    if (window.innerWidth <= 1100) return;
    dragging = true;
    document.body.style.cursor = "col-resize";
    document.body.style.userSelect = "none";
    e.preventDefault();
  });
  window.addEventListener("mousemove", (e) => {
    if (!dragging) return;
    const rect = layout.getBoundingClientRect();
    let pct = ((e.clientX - rect.left) / rect.width) * 100;
    pct = Math.max(30, Math.min(70, pct));
    problemCol.style.flex = `0 0 ${pct}%`;
    solveCol.style.flex = `1 1 auto`;
    if (state.cm) state.cm.refresh();
  });
  window.addEventListener("mouseup", () => {
    if (!dragging) return;
    dragging = false;
    document.body.style.cursor = "";
    document.body.style.userSelect = "";
  });
}

async function runJudge(path, code, mode, panel) {
  const out = panel.querySelector(".judge-results");
  out.innerHTML = `<p class="hint">${mode === "complexity" ? "Measuring…" : "Running…"}</p>`;
  let res;
  try {
    res = await postJSON("/api/judge/run", { path, code, mode });
  } catch (e) {
    out.innerHTML = `<p class="error">Judge error: ${escapeHtml(e.message)}</p>`;
    return;
  }
  out.innerHTML = mode === "complexity" ? renderComplexity(res) : renderRun(res);

  // Auto-mark the problem complete the first time every test passes.
  if (mode === "run" && res && res.summary && res.summary.allPassed) {
    const saved = panel.querySelector(".judge-saved");
    if (saved) {
      saved.textContent = "✓ Solution saved";
      saved.classList.add("on");
    }
    showToast("✓ Solution saved");
    if (state.progressOn && !state.completed.has(path)) {
      setComplete(path, true, /*silent=*/ false, "🎉 Solved! Marked complete");
    }
  }
}

function renderRun(res) {
  if (res.timeout) return `<p class="error">⏱ ${escapeHtml(res.message || "Timed out")}</p>`;
  if (res.compileError) return `<p class="error">Compile error:\n<pre>${escapeHtml(res.compileError)}</pre></p>`;
  if (!res.ok && res.message) {
    return `<p class="error">${escapeHtml(res.message)}</p>` +
      (res.stderr ? `<pre class="judge-stderr">${escapeHtml(res.stderr)}</pre>` : "");
  }
  const s = res.summary || {};
  const badge = s.allPassed
    ? `<span class="verdict pass">✔ All ${s.total} tests passed</span>`
    : `<span class="verdict fail">✗ ${s.passed}/${s.total} passed</span>`;
  const rows = (res.results || []).map((r) => {
    const ok = r.passed ? "pass" : "fail";
    const icon = r.passed ? "✔" : "✗";
    let detail = "";
    if (!r.passed) {
      if (r.error) detail = `<pre class="judge-stderr">${escapeHtml(r.error)}</pre>`;
      else detail = `<div class="judge-diff"><span>expected: <code>${escapeHtml(JSON.stringify(r.expected))}</code></span><span>got: <code>${escapeHtml(JSON.stringify(r.got))}</code></span></div>`;
    }
    const t = r.timeMs != null ? `<span class="t">${r.timeMs} ms</span>` : "";
    return `<div class="tc ${ok}"><div class="tc-h">${icon} ${escapeHtml(r.id || "test")} <span class="kind">${escapeHtml(r.kind || "")}</span> ${t}</div>${detail}</div>`;
  }).join("");
  const total = s.totalTimeMs != null ? `<div class="judge-total">total ${s.totalTimeMs} ms</div>` : "";
  return `<div class="judge-summary">${badge}${total}</div>${rows}`;
}

function renderComplexity(res) {
  if (!res.supported) return `<p class="hint">Complexity estimation is not available for this problem.</p>`;
  if (res.error) return `<p class="error">Could not measure:\n<pre>${escapeHtml(res.error)}</pre></p>`;
  const rows = (res.samples || []).map(
    (x) => `<tr><td>${x.n}</td><td>${x.timeMs} ms</td><td>${(x.peakBytes / 1024).toFixed(1)} KB</td></tr>`
  ).join("");
  return `
    <div class="judge-summary">
      <span class="verdict est">⏱ time ≈ ${escapeHtml(res.timeComplexity)} <em>(conf ${res.timeConfidence})</em></span>
      <span class="verdict est">💾 space ≈ ${escapeHtml(res.spaceComplexity)} <em>(conf ${res.spaceConfidence})</em></span>
    </div>
    <table class="judge-table"><thead><tr><th>n</th><th>time</th><th>peak mem</th></tr></thead><tbody>${rows}</tbody></table>
    <p class="judge-note">${escapeHtml(res.note || "")}</p>`;
}

function toggleCompare(panel, meta) {
  const box = panel.querySelector(".judge-compare");
  if (!box.hidden) {
    box.hidden = true;
    return;
  }
  const sols = meta.solutions || {};
  const order = ["naive", "better", "optimal"].filter((k) => sols[k]);
  box.innerHTML = `<div class="cmp-grid">${order
    .map(
      (k) =>
        `<div class="cmp-col"><h4>${k}</h4><pre><code class="language-python">${escapeHtml(sols[k])}</code></pre></div>`
    )
    .join("")}</div>`;
  box.querySelectorAll("pre code").forEach((c) => hljs.highlightElement(c));
  box.hidden = false;
}

async function postJSON(url, payload) {
  const res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
  if (!res.ok) {
    const msg = await res.text().catch(() => res.statusText);
    throw new Error(`${res.status}: ${msg}`);
  }
  return res.json();
}

/* ============================================================================================
   Progress tracking — per-section progress bar, completion badges, auto-mark, reset.
   Backed by GET/POST /api/progress (Azure Table Storage in the cloud; in-memory locally).
   ============================================================================================ */
let progressCardEl = null;

function mountProgressCard(catId) {
  removeProgressCard();
  const card = document.createElement("div");
  card.className = "progress-card";
  card.innerHTML = `
    <div class="progress-top">
      <span class="progress-label">Progress</span>
      <span class="progress-count">0 / ${state.problemPaths.size}</span>
    </div>
    <div class="progress-track"><div class="progress-fill"></div></div>
    <div class="progress-actions">
      <button class="progress-reset" type="button">↺ Reset progress</button>
    </div>`;
  card.querySelector(".progress-reset").onclick = () => resetProgress(catId);
  // Insert above the file tree inside the sidebar.
  el.tree.parentNode.insertBefore(card, el.tree);
  progressCardEl = card;
}

function removeProgressCard() {
  if (progressCardEl && progressCardEl.parentNode) progressCardEl.parentNode.removeChild(progressCardEl);
  progressCardEl = null;
}

async function loadProgress(catId) {
  let data;
  try {
    data = await getJSON(`/api/progress?section=${encodeURIComponent(catId)}`);
  } catch (e) {
    return; // progress disabled/unreachable — leave the bar at 0
  }
  if (state.activeCategory !== catId) return; // user switched tabs mid-flight
  state.completed = new Set(data.completed || []);
  updateProgressUI();
}

function updateProgressUI() {
  // Count only completed paths that are actual problems in this section.
  let done = 0;
  state.problemPaths.forEach((p) => {
    if (state.completed.has(p)) done++;
  });
  const total = state.problemPaths.size || 1;
  const pct = Math.round((done / total) * 100);
  if (progressCardEl) {
    progressCardEl.querySelector(".progress-count").textContent =
      `${done} / ${state.problemPaths.size} · ${pct}%`;
    progressCardEl.querySelector(".progress-fill").style.width = `${pct}%`;
  }
  // Reflect completion on each tree file badge.
  state.fileEls.forEach(({ el: fileEl }, path) => {
    fileEl.classList.toggle("done", state.completed.has(path));
  });
  // Reflect completion on the sticky problem header (if a problem is open).
  const ptSolved = document.querySelector(".problem-topbar .pt-solved");
  if (ptSolved && state.activePath) {
    ptSolved.classList.toggle("on", state.completed.has(state.activePath));
  }
}

/** Toggle completion for a problem (manual badge click). */
function toggleComplete(path) {
  const next = !state.completed.has(path);
  setComplete(path, next, /*silent=*/ false, next ? "Marked complete" : "Marked incomplete");
}

/** Persist completion for a problem and update the UI. */
async function setComplete(path, completed, silent, toastMsg) {
  // Optimistic UI update.
  if (completed) state.completed.add(path);
  else state.completed.delete(path);
  updateProgressUI();
  try {
    await postJSON("/api/progress", { path, section: state.activeCategory, completed });
    if (!silent && toastMsg) showToast(toastMsg);
  } catch (e) {
    // Revert on failure.
    if (completed) state.completed.delete(path);
    else state.completed.add(path);
    updateProgressUI();
    showToast("⚠ Could not save progress");
  }
}

async function resetProgress(catId) {
  if (!confirm("Reset progress for this section? This clears all completed marks here.")) return;
  try {
    await postJSON("/api/progress/reset", { section: catId });
  } catch (e) {
    showToast("⚠ Reset failed");
    return;
  }
  state.completed = new Set();
  updateProgressUI();
  showToast("Progress reset");
}

/* ---- Toast --------------------------------------------------------------------------------- */
let toastTimer = null;
function showToast(msg) {
  let t = document.querySelector(".toast");
  if (!t) {
    t = document.createElement("div");
    t.className = "toast";
    document.body.appendChild(t);
  }
  t.innerHTML = `<span class="t-ic"></span>${escapeHtml(msg)}`;
  requestAnimationFrame(() => t.classList.add("show"));
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => t.classList.remove("show"), 2200);
}

/* ============================================================================================
   Theme toggle (dark default <-> light) + mobile drawer sidebar.
   ============================================================================================ */
function currentTheme() {
  return document.documentElement.getAttribute("data-theme") || "dark";
}

function applyTheme(theme) {
  document.documentElement.setAttribute("data-theme", theme);
  localStorage.setItem("hub-theme", theme);
  // Enable the matching highlight.js stylesheet, disable the other.
  const dark = document.getElementById("hljs-dark");
  const light = document.getElementById("hljs-light");
  if (dark) dark.disabled = theme !== "dark";
  if (light) light.disabled = theme !== "light";
  // Update the toggle icon (show the theme you'd switch TO).
  if (el.themeToggle) el.themeToggle.textContent = theme === "dark" ? "☀️" : "🌙";
  // Keep an open CodeMirror editor in sync with the app theme.
  if (state.cm) {
    try { state.cm.setOption("theme", theme === "light" ? "neo" : "material-darker"); } catch (_) {}
  }
  // Future Mermaid renders should match; already-drawn diagrams keep their theme.
  if (window.mermaid) {
    try {
      window.mermaid.initialize({
        startOnLoad: false,
        theme: theme === "light" ? "default" : "dark",
        securityLevel: "loose",
      });
    } catch (_) {}
  }
}

function toggleTheme() {
  applyTheme(currentTheme() === "dark" ? "light" : "dark");
}

function openDrawer() {
  el.sidebar.classList.add("open");
  el.scrim.hidden = false;
  requestAnimationFrame(() => el.scrim.classList.add("show"));
}
function closeDrawer() {
  el.sidebar.classList.remove("open");
  el.scrim.classList.remove("show");
  setTimeout(() => {
    if (!el.sidebar.classList.contains("open")) el.scrim.hidden = true;
  }, 280);
}
function toggleDrawer() {
  if (el.sidebar.classList.contains("open")) closeDrawer();
  else openDrawer();
}

function wireChrome() {
  applyTheme(currentTheme()); // sync hljs sheets + icon on load
  if (el.themeToggle) el.themeToggle.onclick = toggleTheme;
  if (el.menuToggle) el.menuToggle.onclick = toggleDrawer;
  if (el.scrim) el.scrim.onclick = closeDrawer;
  if (el.treeSearch) el.treeSearch.oninput = () => filterTree(el.treeSearch.value);
}

/* ============================================================================================
   Auth — identity chip, logout, and the admin dashboard (allow-list management).
   The app is gated server-side; if the SPA loads we're authenticated. We still fetch /me to
   show who's signed in and to reveal the admin controls for the admin account.
   ============================================================================================ */
const auth = { email: null, role: null, admin: false };

async function bootstrapAuth() {
  try {
    const me = await getJSON("/api/auth/me");
    if (!me.authenticated) {
      window.location.href = "/login.html";
      return false;
    }
    auth.email = me.email;
    auth.role = me.role;
    auth.admin = !!me.admin;
  } catch (e) {
    window.location.href = "/login.html";
    return false;
  }
  // Identity chip + logout.
  if (el.userEmail) el.userEmail.textContent = auth.email;
  if (el.userChip) el.userChip.hidden = false;
  if (el.logoutBtn) el.logoutBtn.onclick = logout;
  // Admin button (only for the admin account).
  if (auth.admin && el.adminBtn) {
    el.adminBtn.hidden = false;
    el.adminBtn.onclick = openAdmin;
  }
  return true;
}

async function logout() {
  try {
    await fetch("/api/auth/logout", { method: "POST" });
  } catch (_) {}
  window.location.href = "/login.html";
}

/** Render the admin dashboard in the content pane. */
async function openAdmin() {
  // Deselect any active tab/file; the admin view takes over the content pane.
  [...el.tabs.children].forEach((b) => b.classList.remove("active"));
  if (state.activeFileEl) { state.activeFileEl.classList.remove("active"); state.activeFileEl = null; }
  el.breadcrumb.textContent = "admin";
  closeDrawer();
  el.viewer.className = "viewer";
  el.viewer.innerHTML = `
    <div class="admin-view">
      <h2>⚙️ Admin · Access control</h2>
      <p class="hint">Add an email to let that person sign in (email only, no password). Remove to revoke access.</p>
      <div class="admin-add">
        <input id="admin-email-input" type="email" placeholder="name@example.com" autocomplete="off" />
        <button id="admin-add-btn" type="button">＋ Add user</button>
      </div>
      <div class="admin-msg" id="admin-msg"></div>
      <div id="admin-list"><p class="hint">Loading users…</p></div>
    </div>`;
  const input = document.getElementById("admin-email-input");
  const addBtn = document.getElementById("admin-add-btn");
  addBtn.onclick = () => adminAdd(input.value);
  input.onkeydown = (e) => { if (e.key === "Enter") adminAdd(input.value); };
  loadAdminUsers();
}

async function loadAdminUsers() {
  const box = document.getElementById("admin-list");
  if (!box) return;
  let data;
  try {
    data = await getJSON("/api/admin/users");
  } catch (e) {
    box.innerHTML = `<p class="error">Failed to load users: ${escapeHtml(e.message)}</p>`;
    return;
  }
  const users = data.users || [];
  if (!users.length) {
    box.innerHTML = `<p class="admin-empty">No approved guests yet. Add an email above.</p>`;
    return;
  }
  const rows = users.map((u) => `
    <tr>
      <td>${escapeHtml(u.email)}</td>
      <td>${escapeHtml(u.addedAt ? String(u.addedAt).slice(0, 10) : "")}</td>
      <td style="text-align:right"><button class="admin-remove" data-email="${escapeHtml(u.email)}">Remove</button></td>
    </tr>`).join("");
  box.innerHTML = `
    <table class="admin-table">
      <thead><tr><th>Email</th><th>Added</th><th></th></tr></thead>
      <tbody>${rows}</tbody>
    </table>`;
  box.querySelectorAll(".admin-remove").forEach((b) => {
    b.onclick = () => adminRemove(b.dataset.email);
  });
}

async function adminAdd(email) {
  const msg = document.getElementById("admin-msg");
  email = (email || "").trim();
  if (!email || !email.includes("@")) {
    if (msg) { msg.style.color = "var(--danger)"; msg.textContent = "Enter a valid email."; }
    return;
  }
  try {
    const r = await postJSON("/api/admin/users", { email });
    if (!r.ok) throw new Error(r.error || "Add failed");
    if (msg) { msg.style.color = "var(--success)"; msg.textContent = `Added ${r.email}`; }
    document.getElementById("admin-email-input").value = "";
    loadAdminUsers();
  } catch (e) {
    if (msg) { msg.style.color = "var(--danger)"; msg.textContent = escapeHtml(e.message); }
  }
}

async function adminRemove(email) {
  if (!confirm(`Revoke access for ${email}?`)) return;
  try {
    await fetch(`/api/admin/users?email=${encodeURIComponent(email)}`, { method: "DELETE" });
    loadAdminUsers();
  } catch (e) {
    const msg = document.getElementById("admin-msg");
    if (msg) { msg.style.color = "var(--danger)"; msg.textContent = "Remove failed."; }
  }
}

wireChrome();
bootstrapAuth().then((ok) => { if (ok) init(); });
