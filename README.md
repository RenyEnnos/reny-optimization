# Reny Optimization

Reny Optimization is an experimental performance project for **Minecraft 1.7.10 + Forge** focused on reducing frame-time spikes, CPU overhead, memory pressure, chunk stalls, and rendering bottlenecks in heavily modded installations, including shader-heavy setups.

The project is intentionally measurement-driven: optimizations should be justified by profiling data and benchmarked before and after implementation.

## Project goals

- Improve frame-time consistency, especially P95/P99/P99.9 latency.
- Reduce main-thread and render-thread CPU cost.
- Reduce allocation rate and garbage-collection pressure.
- Improve chunk loading, meshing, lighting, and I/O behavior.
- Scale better with large numbers of entities and TileEntities.
- Support shader-heavy Minecraft 1.7.10 installations.
- Remain modular: every optimization should be independently measurable and disableable where practical.
- Preserve broad Forge/mod compatibility in the default profile while allowing opt-in aggressive and experimental optimizations.

## Target stack

- Minecraft 1.7.10
- Forge 10.13.4.1614
- Java 8 bytecode baseline
- RetroFuturaGradle
- UniMixins / Mixin as the preferred transformation layer
- ASM only when a transformation cannot reasonably be expressed with safer mechanisms

## Design principles

1. **Measure first.** No optimization is accepted only because it "feels faster".
2. **Frame time over headline FPS.** Smoothness and tail latency matter more than peak FPS.
3. **Modular patches.** Patches declare their dependencies, risk level, side, and known conflicts.
4. **Compatibility by default.** Aggressive rewrites are explicit opt-ins.
5. **No placebo tuning.** Every performance change needs a reproducible workload and measurable effect.
6. **Avoid duplicated work.** Existing 1.7.10 optimization projects are treated as prior art and compatibility targets, not blindly reimplemented.

## Planned subsystems

- bootstrap / patch registry
- diagnostics and benchmark harness
- memory and allocation optimizations
- tick / entity / TileEntity optimizations
- chunk lifecycle and I/O
- lighting
- rendering compatibility layer
- culling and mesh pipeline
- shader/render backend work
- experimental parallel simulation

## Status

**Pre-alpha / architecture bootstrap.**

## Integração minecraft-dev-toolkit

O bridge é opcional e server-safe. Defina `minecraft.dev.bridge` como propriedade
JVM (e, no Gradle, `-Pminecraft.dev.bridge`) para armá-lo; o token vem de
`minecraft.dev.bridge.token` ou de `MINECRAFT_DEV_BRIDGE_TOKEN`. As fontes são
resolvidas por `MINECRAFT_DEV_TOOLKIT_HOME` ou pelo irmão `minecraft-dev-toolkit`.
O adaptador local inicia com `node tools/minecraft-dev/launcher.js` e expõe
somente `reny_get_profiler_snapshot`, capability `profiler.read`, em
`GET /reny/profiler`, com snapshot read-only de heap/GC/tasks, IDs e seções.

Patches não são expostos pelo bridge: `PatchRegistry` não possui uma instância
runtime disponível para esse contrato.

The first milestone is `0.1 — Instrumented Core`: a stable bootstrap, compatibility/patch registry, internal profiler, benchmark protocol, and diagnostic tooling. Large invasive optimizations come only after the baseline dataset exists.
