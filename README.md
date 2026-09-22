# Reny Optimization

Reny Optimization is an experimental performance project for **Minecraft 1.7.10 + Forge** focused on reducing frame-time spikes, CPU overhead, memory pressure, chunk stalls, and rendering bottlenecks in heavily modded installations, including shader-heavy setups.

The project is intentionally measurement-driven: optimizations should be justified by profiling data and benchmarked before and after implementation.

## Long-term vision

Reny aims to become the **central optimization runtime for Minecraft Forge 1.7.10**: a system that can implement, coordinate, delegate, adapt, replace, or reject optimization capabilities according to profiling and compatibility evidence instead of blindly bundling or duplicating existing optimization mods.

See [Optimization Runtime Vision](docs/OPTIMIZATION_RUNTIME_VISION.md) for the capability-first model and long-term provider strategy.

## Related project: Reny Shaders

**Reny Shaders** is the proposed performance-first visual renderer/shaderpack for **The Reawakening**. Its governing objective is to maximize **perceived visual quality per millisecond of GPU time**, using approximation, low-resolution work, reconstruction, and pack-aware rendering where these techniques provide better visual return for the cost.

Its canonical pre-research direction — including the dark-fantasy art target, Lite/Default/Showcase profiles, project boundaries, agent rules, and open research questions — is documented in [Reny Shaders — Product and Rendering Vision](docs/RENY_SHADERS_VISION.md).

Reny Shaders is related to Reny Optimization but is a separate product: the optimization runtime may provide efficient rendering infrastructure, while the shaderpack owns the intended image and visual-performance tradeoffs.

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

The first milestone is `0.1 — Instrumented Core`: a stable bootstrap, compatibility/patch registry, internal profiler, benchmark protocol, and diagnostic tooling. Large invasive optimizations come only after the baseline dataset exists.

## Minecraft Dev Toolkit (development tooling only)

Reny uses the shared Minecraft Dev Toolkit only for local development and MCP tooling; it is not a runtime mod dependency. OpenCode and OMP are client configurations for the same shared `minecraft-dev` server and must not duplicate MCP/bridge implementation inside Reny.

### OpenCode

With sibling checkouts:

```bash
node ../minecraft-dev-toolkit/bootstrap/src/cli.js --consumer .
```

Or set `MINECRAFT_DEV_TOOLKIT_HOME` to an explicit Toolkit checkout before running the same command. The bootstrap is project-local and needs no global OpenCode configuration.

### OMP

OMP uses the project-local `.omp/mcp.json` definition and launches the same shared Toolkit entrypoint. The committed OMP-native config uses the canonical sibling checkout `../minecraft-dev-toolkit`; it does not rely on shell-style `${VAR:-default}` expansion in `args`. For a non-sibling local layout, use a non-versioned OMP user override rather than committing an absolute path.

OpenCode and OMP configurations are independent contracts. OMP autodiscovery of `opencode.json` is not relied upon.

From the repository root, reload and inspect the server with:

```text
/mcp reload
/mcp list
/mcp test minecraft-dev
```

No user-global OMP MCP registration is required.

The Forge bridge and `reny/profiler` extension are not part of this tooling-bootstrap slice; MCP configuration alone is not evidence of Forge runtime integration.
