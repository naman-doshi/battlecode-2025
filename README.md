# MIT Battlecode 2025 — 5th Place Overall

Competition bot for **MIT Battlecode 2025** (Java), placing **5th overall**.

## Architecture

The final tournament bot lives in [`java/src/caterpillow/`](java/src/caterpillow/):

- `robot/` — Per-unit strategy and micro controllers (soldiers, moppers, splashers, and paint/money/defense towers), including ruin/SRP scheduling, deadlock resolution, and tower-ratio management.
- `pathfinding/` — Bytecode-budgeted navigation and map traversal.
- `tracking/` & `world/` — Distributed map state tracking (`TowerTracker`, ruin/resource bookkeeping).
- `packet/` — Compact inter-unit messaging under Battlecode's per-turn communication limits.
- `scrimmer.py` — Automated local scrimmage runner for evaluating bot iterations.

## Running

See [`java/README.md`](java/README.md) for Gradle build and match-running commands.
