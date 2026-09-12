# Bottleneck ranking

**Status: UNPROVEN — ranking intentionally deferred.**

The single validated formal A/BENCH-01 run and the pilot are not enough to rank
the first Reny optimization target across the issue #7 workloads. No category
such as chunks, rendering, TileEntities, entities, lighting, GC, shader passes,
or GPU saturation is asserted here.

After the 80-run matrix is complete, this file must be generated from the
aggregated frame/tick/GC data and each ranked item must identify:

- affected scenario/configuration cells;
- the measured budget and tail-latency impact;
- CPU/GPU/JVM classification with non-overlapping evidence;
- confidence and compatibility risk;
- the proposed Reny milestone/module and acceptance benchmark.

The absence of active subsystem hooks and external GPU/CPU traces must remain
visible in the confidence level rather than being replaced by speculation.
