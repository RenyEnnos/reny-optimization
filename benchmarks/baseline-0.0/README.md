# Performance Baseline 0.0

**Status: PARTIAL.** The runtime, instrumentation control surface, workload
manifests, pilot gate, and deterministic aggregator are in place. The formal
matrix is still in progress: 1 of 80 formal runs is currently valid, with the
remaining cells not yet measured.

## Scope

This is the execution of Reny Optimization issue #7:

| Dimension | Scope |
| --- | --- |
| Reny | `30d88b554addd4a610d6fe1efd319bda4b677635` |
| Minecraft / Forge | 1.7.10 / 10.13.4.1614 |
| Runtime | Temurin/OpenJDK 1.8.0_312 |
| Build JVM | Temurin JDK 25.0.4.1 |
| Formal matrix | 4 scenarios × 4 configurations × 5 runs = 80 |
| Timing | 60 s warmup + 120 s measurement |
| Primary display | 1280×720, render distance 8, VSync off, FPS cap 260 (1.7.10 maximum) |
| Optimization patches | None |

The four configurations are A: minimal/shaders off, B: minimal/shaders on,
C: The Reawakening/shaders off, and D: The Reawakening/shaders on. The
canonical scenarios are BENCH-01, BENCH-02, BENCH-03, and BENCH-06.

## Current evidence

The corrected A/BENCH-01 pilot passed the exporter gate. A separate formal
A/BENCH-01 run also passed after revalidation of a delayed export. These are
kept distinct: the pilot is not counted among the five formal runs.

No bottleneck is ranked yet. A ranking requires the completed formal matrix and
must be based on measured frame/tick evidence rather than expectation.

## Reproduction

From the repository checkout, aggregate terminal `VALID` events for the exact
commit with:

```text
python3 benchmarks/baseline-0.0/aggregate.py \
  --events /path/to/Reny\ Optimization/benchmark-work/campaign-runner.jsonl \
  --results-root /path/to/Reny\ Optimization/benchmarks/results \
  --commit 30d88b554addd4a610d6fe1efd319bda4b677635 \
  --output-dir benchmarks/baseline-0.0
```

Add `--strict` for the completion gate. The default event mode excludes
rejected attempts and the pilot unless a formal terminal `VALID` event exists.
The script concatenates measurement-only CSV samples for cell percentiles and
also reports run-level dispersion, threshold rates, MSPT, GC, and heap delta.

## Artifact locations

- `methodology.md` — scope and execution protocol.
- `environment-summary.json` — reproducible runtime and workload identity.
- `aggregate.py` — deterministic aggregation and validation.
- `aggregate.csv` / `aggregate.json` — generated only after the campaign has
  enough valid result exports.
- `known-limitations.md` — confirmed limits and unproven claims.
- `bottlenecks.md` — ranking status; no premature optimization target.

Large raw exports and logs stay in the local benchmark workspace and are not
committed to the repository.
