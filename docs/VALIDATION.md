# Validation

Authoring date: November 6, 2026. Host: Windows. Target: Windows, macOS, Linux desktop.

## Local result

JDK unavailable on authoring host. CI compiles and runs schedule/scoring invariants; Swing interactions remain manual.

## Reproducible checks

The GitHub Actions workflow builds/runs the following on `ubuntu-latest`:

```sh
javac -d build src/Tournament.java
java -cp build Tournament --self-test
```

## Manual acceptance

Create odd/even team tournaments, record wins/draws, revise a score, restart, export text, and test duplicate/empty team names.

A workflow file is not evidence of a passing run. Observed CI run links and source commits are recorded after publication. Native GUI, sound, sleep/resume, and platform permissions require direct target-platform acceptance even when model tests pass.

## Observed CI result

Passed on November 6, 2026: [target-platform build/check](https://github.com/netcodermike/round-robin-tournament-studio/actions/runs/42198953221).

Verified source commit: `b09e417e52342e6a06873ed104472414ce9438ae`. This evidence identifies the exact tested source; a later documentation-only commit does not change that source.

The commands listed above passed. Native GUI/manual acceptance is not claimed by this CI result.
