# Reference workload: The Reawakening

Status: **PROPOSED reference-workload policy**  
Date: 2026-09-12

## Purpose

The Reawakening is a large Minecraft 1.7.10 modpack under active development. It is a strong candidate for Reny Optimization's primary real-world heavy-modpack reference workload because it combines industrial automation, magic, survival systems, world generation, entity pressure, and shader-heavy rendering in one environment.

This document defines how Reny may use The Reawakening without becoming pack-specific.

## Core rule

The Reawakening is a **benchmark target, not a dependency**.

Reny must continue to:

- build without The Reawakening;
- run without The Reawakening;
- expose generic patch metadata and compatibility rules;
- avoid importing The Reawakening classes or assets;
- avoid encoding pack progression, recipes, lore, or balance into Reny core;
- avoid special-casing a bottleneck when the underlying Minecraft/Forge/mod interaction can be generalized.

The Reawakening should provide evidence and workload diversity, not architectural ownership over Reny.

## Why this workload is useful

The pack combines or is expected to combine stressors from multiple independent domains:

- GregTech machine and TileEntity load;
- AE2 networks/autocrafting;
- BuildCraft and Railcraft logistics;
- Forestry and Magic Bees automation/genetics;
- Thaumcraft systems, golems, aura/flux-related behavior;
- Botania flowers, spreaders, pools, bursts, and automation;
- Blood Magic and Witchery ritual systems;
- EnviroMine environmental processing;
- Zombie Awareness and Techguns entity/AI pressure;
- Chocolate Quest, Roguelike Dungeons, Ruins, Mowzie's Mobs, and HEE exploration load;
- Biomes O' Plenty and other chunk-generation work;
- OptiFine/shader-heavy client rendering;
- future Tensura RPG workloads after the port reaches a stable release.

A patch that survives this environment with measurable gains is likely to have value beyond one pack, provided the implementation remains generic.

## Relationship to Performance Baseline 0.0

For the existing baseline campaign, The Reawakening should be the preferred `heavy modpack` configuration where practical.

Recommended mapping:

1. minimal/vanilla-like, shaders off;
2. minimal/vanilla-like, shaders on;
3. The Reawakening, shaders off;
4. The Reawakening, shaders on.

The existing canonical scenarios remain authoritative:

- `BENCH-01` Stationary render;
- `BENCH-02` Chunk traversal;
- `BENCH-03` Industrial base;
- `BENCH-06` Shader torture.

The pack relationship does not change benchmark schema or methodology.

## Reproducibility requirements

A formal The Reawakening benchmark result should record at minimum:

- exact Reny commit;
- exact The Reawakening commit or release identifier;
- config hash;
- active mod list/versions;
- Java/JVM details;
- CPU/GPU/driver/OS metadata where available;
- shader name/version/preset;
- display resolution, render distance, VSync/FPS cap;
- world seed/descriptor;
- camera route or workload procedure;
- warmup and measurement duration.

Personal save files may be useful during diagnosis, but canonical baseline claims should use curated reproducible worlds/routes or clearly versioned scenario descriptors.

## Preferred development loop

```text
The Reawakening exposes a real bottleneck
        ↓
Reny reproduces and measures it
        ↓
subsystem/root cause is isolated
        ↓
generic patch or compatibility policy is designed
        ↓
A/B benchmark under identical workload
        ↓
regression and compatibility checks
        ↓
promote, revise, or reject patch
```

The pack should not be modified to manufacture a benchmark win for Reny. Reny should improve the observed workload while preserving intended gameplay semantics.

## Compatibility priorities before invasive patches

The Reawakening currently uses optimization/fix components including FastCraft, FoamFix, and OptiFine.

OptiFine already has explicit handling in Reny's compatibility model. Before Reny begins implementing patches that could overlap their domains, FastCraft and FoamFix should also receive explicit compatibility treatment.

Desired behavior is fail-closed and deterministic:

- equivalent feature already owned elsewhere -> `REPLACED`;
- coexistence possible with changed assumptions -> `PARTIAL`;
- unsafe overlap -> `CONFLICT`;
- insufficient evidence -> `UNKNOWN`;
- safe coexistence -> `COMPATIBLE`.

Reny should not rely on transformer-order races to override an existing optimizer.

## Optimization profiles for reference-pack testing

Suggested convention:

| Workload context | Profile |
| --- | --- |
| representative public/release behavior | `COMPATIBLE` |
| development/QA experiments | `AGGRESSIVE` |
| controlled research only | `NUCLEAR` |

`NUCLEAR` is a research envelope, not a default high-performance preset.

## Pack-specific instrumentation

Reny may add generic instrumentation needed to understand workloads seen in The Reawakening, for example:

- TileEntity class/mod attribution;
- entity class/mod attribution;
- chunk-generation stage attribution;
- Forge event timing;
- scheduled tick pressure;
- render pass/TileEntity renderer timing;
- allocation hot spots;
- I/O queue depth;
- task scheduler counters.

However, instrumentation should describe generic classes, mods, or subsystems. It should not encode pack progression concepts such as `GT tier`, `magic school`, `Tensura skill rank`, or recipe ownership into Reny's profiler core.

Pack-specific interpretation belongs in analysis/reporting outside the core runtime.

## Tensura boundary

The future Tensura 1.7.10 port is not part of the current Reny reference workload until a stable release exists and The Reawakening adopts it.

After adoption, it may add useful workloads involving:

- combat/entity spikes;
- skill processing;
- persistent player capability/state;
- networking;
- particles/rendering;
- mob AI;
- worldgen or structure content.

Those workloads may motivate generic Reny optimization work, but Reny must not become dependent on Tensura APIs unless an optional compatibility adapter is explicitly justified later.

## Success condition

The relationship is successful when:

1. The Reawakening becomes larger while maintaining measurable performance budgets.
2. Reny's optimization priorities are chosen from real profiling evidence.
3. Improvements proven on the pack remain generic enough to benefit other 1.7.10 installations.
4. Either project can evolve or be released independently.
5. Removing Reny from The Reawakening changes performance/observability, not gameplay correctness.
