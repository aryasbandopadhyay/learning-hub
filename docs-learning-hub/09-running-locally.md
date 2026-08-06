# Running Locally

Learning Hub runs entirely on your machine with **no cloud account** — progress and auth fall back
to in-memory stores when no Azure connection string is set.

---

## Prerequisites

- **JDK 17+**
- **Maven** (or the wrapper)
- **Python 3** on `PATH` (only needed to use the judge)

---

## Quick start

```bash
cd learning-hub
mvn spring-boot:run
# open http://localhost:8080
```

That's it for reading content. The site serves from `src/main/resources/static/`, and the content
root is the **parent** of `learning-hub/` (i.e. the `Projects/` folder), so all sibling note folders
show up as tabs.

---

## Enabling the judge locally

The judge shells out to Python. Point the app at your interpreter if it isn't named `python`:

```bash
# Windows PowerShell
$env:HUB_JUDGE_PYTHON_EXE = "python"
# Linux/macOS
export HUB_JUDGE_PYTHON_EXE=python3
```

Then open a DSA/Google/FAANG problem and use the **Solve** panel.

---

## Enabling admin login locally

Admin is disabled unless **both** the email and password are provided (they are never hardcoded):

```bash
# PowerShell
$env:HUB_AUTH_ADMIN_EMAIL    = "you@example.com"
$env:HUB_AUTH_ADMIN_PASSWORD = "choose-a-password"
```

Guests need no password — the admin adds their email to the allow-list from the in-app admin
dashboard. With no Azure connection string, the allow-list lives in memory (resets on restart).

---

## Optional: real Azure storage locally

To persist progress/allow-list against a real table:

```bash
$env:HUB_PROGRESS_CONNECTION_STRING = "<azure storage connection string>"
```

Leave it unset for a pure in-memory run.

---

## Useful config knobs (`application.yml` / env)

| Key | Env var | Default | Meaning |
|---|---|---|---|
| `hub.root` | `HUB_ROOT` | parent of working dir | content root |
| `hub.judge.python-exe` | `HUB_JUDGE_PYTHON_EXE` | `python` | judge interpreter |
| `hub.auth.enabled` | `HUB_AUTH_ENABLED` | `true` | master auth switch |
| `hub.auth.admin-email` | `HUB_AUTH_ADMIN_EMAIL` | *(blank)* | admin login email |
| `hub.auth.admin-password` | `HUB_AUTH_ADMIN_PASSWORD` | *(blank)* | admin login password |
| `hub.progress.connection-string` | `HUB_PROGRESS_CONNECTION_STRING` | *(blank)* | Azure Table conn string (blank = in-memory) |

---

## Running the tests

```bash
# backend
cd learning-hub && mvn test

# judge
cd learning-hub/judge && pytest test_runner.py
```

Both use in-memory fallbacks, so no cloud is needed. See `08-testing-and-ci` for the CI pipeline
that runs these on every push.

---

## Building the container (optional)

You don't need Docker locally to deploy (`az containerapp up --source .` builds in the cloud), but to
build the image yourself:

```bash
docker build -t learning-hub .
docker run -p 8080:8080 -e HUB_JUDGE_PYTHON_EXE=python3 learning-hub
```
