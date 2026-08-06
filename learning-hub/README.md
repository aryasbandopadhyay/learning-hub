# Learning Hub 📚

A minimalist **Spring Boot** web app to browse and read your study material in one place:
**Spring Boot**, **LLD**, and **HLD** — with Markdown (including **Mermaid diagrams**) rendered
properly and source code shown with **syntax highlighting**.

It reads the existing folders that live **next to** this app (no copying, no database):

```
Projects/
├─ spring-crud-demo/          ← "Spring Boot" tab
├─ parking-lot/ … find-command/  (21 dirs) ← "LLD" tab
├─ system-design-hld/*.md     ← "HLD" tab
└─ learning-hub/              ← THIS app
```

---

## Quick start

```powershell
# from the learning-hub/ directory
$mvn = "$env:USERPROFILE\tools\apache-maven-3.9.9\bin\mvn.cmd"   # Maven not on PATH here
& $mvn spring-boot:run
```

Then open <http://localhost:8080>. Pick a tab, click a file on the left.

> Run tests with `& $mvn test` (11 tests). Requires internet in the **browser** (the Markdown /
> Mermaid / highlight.js renderers load from CDNs; the backend itself needs no internet).

---

## ✨ Extensibility — adding a new subject (e.g. DSA) is a *zero-code* change

The entire content model is **config-driven** in [`application.yml`](src/main/resources/application.yml).
The frontend builds its tabs dynamically from `GET /api/categories`, so a new category
automatically becomes a new tab — **no Java and no JavaScript changes**.

To add DSA tomorrow:

1. Put your material in a sibling folder, e.g. `../dsa/...`.
2. Add one block under `hub.categories`:
   ```yaml
   - id: dsa
     label: DSA
     description: "Data-structures & algorithms problems, notes and solutions."
     paths:
       - dsa
   ```
3. Restart. A **DSA** tab appears. Done.

`paths` accepts either a single file or a directory (walked recursively), and doubles as the
security allow-list. To add more items to an existing tab (say a new LLD problem), just append
its folder name to that category's `paths`.

---

## Architecture

```mermaid
graph LR
  Browser["Browser SPA<br/>(index.html + app.js)"] -->|"GET /api/categories"| C[ContentController]
  Browser -->|"GET /api/tree/{cat}"| C
  Browser -->|"GET /api/file?cat&path"| C
  C --> S[ContentService]
  S -->|"walk / read (safe)"| FS[("Sibling folders<br/>on disk")]
  subgraph Render in browser
    Browser -. marked .-> MD[Markdown → HTML]
    Browser -. mermaid .-> DIA[Diagrams]
    Browser -. highlight.js .-> CODE[Code highlight]
  end
```

### Backend (Spring Boot / Java 17)

| Component | Responsibility |
|---|---|
| `LearningHubApplication` | Boot entry point; enables `ContentProperties` binding. |
| `ContentProperties` (record) | Typed binding of `hub.*` config (root + categories). The extensibility hinge. |
| `ContentService` | Resolves the content root, builds the file tree, reads files. **All security lives here.** |
| `ContentController` | Thin REST layer: `/api/categories`, `/api/tree/{category}`, `/api/file`. |
| `TreeNode`, `FileContent`, `CategoryDto` | Immutable DTOs serialized to JSON by Jackson. |

**Security model.** A request can only read a file that is **both** inside the content root
**and** inside one of the requested category's configured `paths`. All paths are normalized and
checked with `startsWith`, so `../` traversal is rejected (returns `400`/`403`). Only an
allow-listed set of text extensions is served, build/VCS dirs (`target`, `.git`, `__pycache__`,
`node_modules`, …) are skipped, and files over 4 MB are refused.

### Frontend (vanilla JS, no build step)

`src/main/resources/static/` — served automatically by Spring Boot.

- **Tabs** are rendered from `/api/categories` (dynamic → new categories just appear).
- **Markdown** → [`marked`](https://marked.js.org) → HTML; ```mermaid blocks are turned into
  [`mermaid`](https://mermaid.js.org) diagrams; other code blocks are highlighted with
  [`highlight.js`](https://highlightjs.org).
- **Code files** → a single highlighted `<pre><code>` block (language inferred from extension).

---

## REST API

| Method & path | Description |
|---|---|
| `GET /api/categories` | `[{ id, label, description }]` — the tabs. |
| `GET /api/tree/{category}` | Nested `TreeNode` (dirs + files) for a category. |
| `GET /api/file?category={id}&path={relPath}` | `{ category, path, name, ext, markdown, content }`. |

Example:
```
GET /api/file?category=hld&path=system-design-hld/url-shortener.md
```

---

## Configuration reference (`hub.*`)

| Key | Meaning |
|---|---|
| `hub.root` | Base dir all `paths` resolve against. **Blank ⇒ parent of the working directory** (the surrounding `Projects` folder). Set an absolute path to override. |
| `hub.categories[].id` | URL-safe id used in API routes. |
| `hub.categories[].label` | Tab label. |
| `hub.categories[].description` | Optional blurb shown above the tree. |
| `hub.categories[].paths` | Files/dirs (relative to `root`) to expose **and** the security allow-list. |

---

## Testing

```powershell
& $mvn test
```

- `ContentServiceTest` (7) — tree building, extension/dir filtering, markdown vs code, unknown
  category, and **path-traversal rejection**, all against a hermetic `@TempDir`.
- `ContentControllerTest` (3) — `@WebMvcTest` slice with a mocked service verifying routing + JSON.
- `LearningHubApplicationTests` (1) — context loads / beans wire up.

---

## Notes & possible extensions

- **Offline use:** vendor `marked`, `mermaid`, and `highlight.js` locally under `static/` and
  swap the CDN `<script>`/`<link>` tags if you need to run without internet.
- Add full-text **search** across files, a **dark theme**, deep-linkable URLs (hash routing),
  raw-download / "open in editor" buttons, or a "copy code" button.
- Point `hub.root` elsewhere to reuse this viewer for a completely different set of folders.
