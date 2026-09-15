# Optimization Runtime Vision

## 1. Long-term objective

Reny Optimization is intended to become the **central performance runtime for Minecraft Forge 1.7.10**.

The goal is not to bundle every existing optimization mod into one JAR, nor to blindly reimplement every optimization ever published. The goal is to systematically cover the useful optimization capabilities of the 1.7.10 ecosystem and relevant later Minecraft versions, then select the best safe implementation for the detected environment.

Reny should act as both:

- an optimization implementation;
- an optimization coordinator.

The runtime may implement a capability itself, delegate it to another installed project, adapt or extend an external implementation, replace an inferior implementation, or reject a technique that does not survive measurement or compatibility testing.

## 2. Capability-first model

Reny distinguishes **what must be optimized** from **how that optimization is implemented**.

A **capability** represents an optimization objective, for example:

- `render.entity.occlusion`;
- `render.chunk.occlusion`;
- `render.terrain.backend`;
- `render.mesh.build`;
- `render.shader.pipeline`;
- `tick.entity.idle_work`;
- `tick.tileentity.idle_work`;
- `world.lighting`;
- `chunk.scheduling`;
- `io.region`;
- `memory.nbt.allocations`;
- `memory.buffer.reuse`.

A **patch** is one concrete implementation or modification that may provide one or more capabilities.

This distinction prevents Reny from treating optimization as a flat list of tweaks. Multiple providers may exist for the same capability, but only a coherent compatible set should be active.

## 3. Provider states

For each capability, Reny should eventually classify the effective implementation using the following states:

### `NATIVE`
Reny provides the selected implementation.

### `DELEGATED`
Another installed project already provides an implementation that is preferable or sufficiently good, so Reny avoids duplicate work.

### `ADAPTED`
Reny cooperates with, configures, extends, schedules around, or otherwise improves an external implementation without replacing it.

### `REPLACED`
Reny intentionally supersedes an external or vanilla implementation because benchmark and compatibility evidence justify doing so.

### `REJECTED`
The technique or provider is not selected because it is redundant, incompatible, unsafe, ineffective, or regressive.

These states describe the selected provider strategy. They do not replace the Patch Registry's enable/disable, dependency, conflict, risk, or precondition semantics.

## 4. Target architecture

The long-term resolution flow should move toward:

```text
Environment Detection
        |
        v
Optimization Capability Catalog
        |
        v
Capability Resolver
        |
        v
Compatibility Manager
        |
        v
Patch Registry
        |
        v
Selected providers / implementations
```

The **Capability Catalog** describes optimization goals and known providers.

The **Capability Resolver** determines which provider should satisfy each capability in the current environment.

The **Compatibility Manager** supplies evidence about installed mods, versions, active backends, conflicts, and environment constraints.

The **Patch Registry** remains the authoritative policy layer for enabling concrete Reny patches, including dependencies, conflicts, risk ceilings, preconditions, diagnostics, and rollback behavior.

## 5. Relationship with existing optimization projects

Existing optimization mods are first treated as:

1. **prior art** — sources of algorithms, design ideas, failure modes, and benchmark hypotheses;
2. **compatibility targets** — projects Reny should detect and coexist with when practical;
3. **capability providers** — implementations Reny may deliberately delegate to;
4. **replacement candidates** — only when Reny can prove a superior implementation for the target environment.

Reny must not attempt to win transformer-order races or run duplicate implementations of the same exclusive subsystem merely to claim feature coverage.

Examples of relevant legacy projects include Angelica, ArchaicFix, FalseTweaks, FastCraft, FoamFix, Neodymium, Beddium, OptiFine, and LWJGL3ify. Their presence should be interpreted in terms of capabilities and compatibility rather than as a checklist of code to absorb.

## 6. Research beyond Minecraft 1.7.10

Reny research is not limited to mods originally written for 1.7.10.

Later projects such as Sodium, Lithium, FerriteCore, ModernFix, C2ME, EntityCulling, MoreCulling, Noisium, Krypton, ImmediatelyFast, and Distant Horizons may expose useful techniques or problem formulations.

The governing question is not:

> Can this mod be ported into Reny?

It is:

> Does Minecraft 1.7.10 suffer from the same underlying bottleneck, and can the technique be adapted safely and measurably to this runtime?

A modern optimization is relevant only when the underlying problem exists in the 1.7.10 stack and a Reny-specific implementation survives profiling, correctness, compatibility, memory, and latency evaluation.

## 7. Selection rule

Every candidate optimization follows an evidence pipeline:

```text
identify bottleneck
      |
      v
reproduce baseline
      |
      v
study prior art
      |
      v
prototype / integrate / delegate
      |
      v
benchmark A/B
      |
      v
correctness + compatibility checks
      |
      v
P95 / P99 / P99.9 + memory evaluation
      |
      v
select or reject
```

Feature coverage is not itself a reason to merge code.

A candidate is accepted only when it provides a measurable benefit for at least one defined workload without unacceptable semantic, compatibility, memory, or tail-latency regressions.

If another installed provider is already better, Reny should delegate instead of duplicating it.

## 8. Profiles and the long-term runtime

### `COMPATIBLE`

Reny acts primarily as a safe optimization coordinator and low-risk implementation layer.

Priorities:

- broad Forge/mod compatibility;
- safe native optimizations;
- capability detection;
- duplicate-work elimination;
- delegation to compatible external providers;
- deterministic conflict handling.

### `AGGRESSIVE`

Reny may take ownership of more subsystems when evidence supports doing so.

Examples may include:

- advanced culling;
- mesh scheduling;
- lighting changes;
- entity/TileEntity work elimination;
- chunk scheduling;
- I/O and memory structure changes.

Compatibility remains an explicit acceptance criterion.

### `NUCLEAR`

Reny may replace deep Minecraft/Forge implementation subsystems while preserving the compatibility contracts required by supported mods.

Potential ownership includes:

- renderer/backend;
- terrain pipeline;
- lighting engine;
- chunk scheduler;
- simulation scheduling;
- asynchronous world subsystems.

The long-term endpoint of `NUCLEAR` may therefore be a Minecraft 1.7.10 environment in which significant performance-critical implementation is provided by Reny while Forge and mod-facing semantics remain the compatibility boundary.

## 9. Non-goals

Reny is not intended to become:

- a bundle of unrelated optimization JARs;
- a compatibility layer that enables every combination at any cost;
- a collection of placebo JVM flags or undocumented tweaks;
- a reason to duplicate an already superior active implementation;
- a project that trades correctness or P99 stability for headline average FPS;
- a source-level museum of every optimization mod ever written.

## 10. Definition of success

The mature Reny runtime should be able to inspect an installation and produce a coherent optimization plan answering questions such as:

```text
Capability                         Selected provider
-----------------------------------------------------------
render.terrain.backend             Angelica / Reny / other
render.chunk.occlusion             selected compatible provider
render.entity.occlusion            Reny or delegated provider
world.lighting                     selected native/external provider
chunk.scheduling                   Reny
memory.nbt.allocations             Reny
shader.pipeline                    selected compatible provider
```

The user should not need to understand overlapping implementation details or manually resolve every optimization-mod conflict.

Reny should explain what is active, what is delegated, what was rejected, and why.

The final product is therefore not merely a high-performance mod. It is a **measurement-driven optimization runtime and coordination platform for Minecraft Forge 1.7.10**.
