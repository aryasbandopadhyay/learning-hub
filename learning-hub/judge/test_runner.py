"""
Tests for the local DSA judge runner (judge/runner.py).

Covers:
  * mode_run grades a correct submission as all-passed and a wrong one as failed.
  * The PEP-578 sandbox blocks network / subprocess access while allowing pure computation.

The sandbox installs a process-wide, non-removable audit hook, so the "blocked" and "allowed"
cases are exercised in fresh subprocesses to avoid contaminating the pytest process itself.

Run:  python -m pytest judge/test_runner.py -q      (from the learning-hub module dir)
"""
import os
import subprocess
import sys

import runner  # judge/ is the working dir / on sys.path when pytest is invoked from here


TWO_SUM_MANIFEST = {
    "id": "arrays-hashing/two-sum",
    "title": "Two Sum",
    "shape": "function",
    "entry": "twoSum",
    "className": "Solution",
    "compareMode": "exact",
    "tests": [
        {"id": "example-1", "kind": "example", "args": [[2, 7, 11, 15], 9], "expected": [0, 1]},
        {"id": "example-2", "kind": "example", "args": [[3, 2, 4], 6], "expected": [1, 2]},
    ],
}

GOOD_SOLUTION = (
    "class Solution:\n"
    "    def twoSum(self, nums, target):\n"
    "        seen = {}\n"
    "        for i, n in enumerate(nums):\n"
    "            if target - n in seen:\n"
    "                return [seen[target - n], i]\n"
    "            seen[n] = i\n"
    "        return []\n"
)

WRONG_SOLUTION = (
    "class Solution:\n"
    "    def twoSum(self, nums, target):\n"
    "        return [0, 0]\n"
)


def test_mode_run_grades_correct_submission():
    out = runner.mode_run(TWO_SUM_MANIFEST, GOOD_SOLUTION)
    assert out["ok"] is True
    assert out["summary"]["allPassed"] is True
    assert out["summary"]["passed"] == out["summary"]["total"] == 2


def test_mode_run_rejects_wrong_submission():
    out = runner.mode_run(TWO_SUM_MANIFEST, WRONG_SOLUTION)
    assert out["summary"]["allPassed"] is False
    assert out["summary"]["passed"] < out["summary"]["total"]


def test_mode_run_reports_compile_error():
    out = runner.mode_run(TWO_SUM_MANIFEST, "class Solution:\n    def twoSum(  # syntax error\n")
    assert out["ok"] is False
    assert "compileError" in out


def _run_in_subprocess(snippet: str) -> subprocess.CompletedProcess:
    """Execute a snippet in a fresh interpreter with judge/ importable, capturing the result."""
    here = os.path.dirname(os.path.abspath(__file__))
    return subprocess.run(
        [sys.executable, "-c", snippet],
        cwd=here,
        capture_output=True,
        text=True,
        timeout=30,
    )


def test_sandbox_blocks_socket_creation():
    snippet = (
        "import runner, socket, sys\n"
        "runner._install_sandbox()\n"
        "try:\n"
        "    socket.socket()\n"
        "    print('NOT_BLOCKED')\n"
        "except PermissionError as e:\n"
        "    sys.stderr.write('BLOCKED:' + str(e))\n"
        "    sys.exit(7)\n"
    )
    res = _run_in_subprocess(snippet)
    assert res.returncode == 7, f"expected socket to be blocked; stdout={res.stdout!r} stderr={res.stderr!r}"
    assert "not permitted" in res.stderr


def test_sandbox_blocks_subprocess_spawn():
    snippet = (
        "import runner, subprocess, sys\n"
        "runner._install_sandbox()\n"
        "try:\n"
        "    subprocess.run(['echo', 'hi'])\n"
        "    print('NOT_BLOCKED')\n"
        "except PermissionError:\n"
        "    sys.exit(7)\n"
    )
    res = _run_in_subprocess(snippet)
    assert res.returncode == 7, f"expected subprocess spawn to be blocked; stderr={res.stderr!r}"


def test_sandbox_allows_pure_computation():
    # Imports/maths a normal algorithm needs must still work under the sandbox.
    snippet = (
        "import runner\n"
        "runner._install_sandbox()\n"
        "import heapq, math, collections\n"
        "h = []\n"
        "for x in (5, 1, 3):\n"
        "    heapq.heappush(h, x)\n"
        "assert heapq.heappop(h) == 1\n"
        "assert math.gcd(12, 8) == 4\n"
        "assert collections.Counter('aab')['a'] == 2\n"
        "print('OK')\n"
    )
    res = _run_in_subprocess(snippet)
    assert res.returncode == 0, f"pure computation should be allowed; stderr={res.stderr!r}"
    assert "OK" in res.stdout
