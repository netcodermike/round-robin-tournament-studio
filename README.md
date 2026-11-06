# Round-Robin Tournament Studio

Create round-robin fixtures, record scores, calculate standings, and export a tournament report.

[![Build and verify](https://github.com/netcodermike/round-robin-tournament-studio/actions/workflows/verify.yml/badge.svg)](https://github.com/netcodermike/round-robin-tournament-studio/actions/workflows/verify.yml)

**Language:** Java · **Platform:** Windows, macOS, Linux desktop · **App type:** Desktop sports/event organizer

First-release learning implementation, with explicit scope below. This is one independently runnable app, not a placeholder in a combined repository.

## Features

- Circle-method fixtures for 2–32 unique teams.
- Odd-team tournaments naturally include byes.
- Editable match scores and standings by points, goal difference, and goals for.
- Local persistence and text report export.

## Requirements

JDK 17+ with Swing and a graphical desktop. No Maven/Gradle or third-party libraries.

## Quick start

```sh
git clone https://github.com/netcodermike/round-robin-tournament-studio.git
cd round-robin-tournament-studio
javac -d build src/Tournament.java
java -cp build Tournament
# Optional separate store:
java -cp build Tournament weekend.properties
```

Commands run from the repository root. Lines marked for a particular operating system apply only there. Language tools are prerequisites, not installed by the app. Close desktop windows normally; terminal/server apps stop with Ctrl+C unless their usage notes say otherwise.

## Example workflow

Start with the four sample teams, select the first fixture, and record 2:1. Verify the winner gains three points. Create a five-team tournament and confirm every team faces each other once with one bye across rounds. Restart and verify scores persist.

## Architecture, configuration, and data

`src/Tournament.java` separates schedule generation, score/standing records, text rendering, Properties persistence, and the Swing Studio. Win/draw/loss scores award 3/1/0 points. Ties sort by goal difference, goals scored, then team name. A list rotation creates each unordered pairing once and prevents a team appearing twice in a round. The default store is tournament.properties. Tests cover sizes 2–9, total pair count, unique pairings, round uniqueness, and scoring. Saves use a same-directory temporary file and replacement; the exported report is plain text.

## Verification

Build with the quick-start command where needed, then run:

```sh
java -cp build Tournament --self-test
```

Manual acceptance: Create odd/even team tournaments, record wins/draws, revise a score, restart, export text, and test duplicate/empty team names.

See [validation record](docs/VALIDATION.md) for observed checks and their limits. CI results verify only what the workflow runs; they do not establish native GUI correctness or production readiness.

## Limitations and next steps

Single round-robin only: no playoffs, double round-robin, court/time assignments, multi-sport scoring, or head-to-head tie rules. Home/away is display order and is not optimized for balance. Names must be unique and comma-free through the create dialog. Only one writer should edit a store. A save failure is shown, but edited in-memory state may remain; export a report before closing if necessary. New tournament replaces the current saved tournament.

## Troubleshooting

Use a JDK for javac. Run from the project directory or provide a store path. --self-test works headlessly, while the UI needs a desktop. Corrupt data fails explicitly. Scores must be integers 0–999. Choose a different store path for independent events.

## Source map

- `src/Tournament.java`

## Development and license

Read [contributing](CONTRIBUTING.md) before changing the app, and [security/privacy](SECURITY.md) before supplying private data. Use synthetic fixtures for tests. No telemetry, paid API key, or account is required by the app itself. Dependencies, when present, are stated in Requirements.

[MIT license](LICENSE), copyright 2026 netcodermike.
