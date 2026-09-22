# Reny Shaders — Product and Rendering Vision

Status: **CANONICAL DIRECTION / PRE-RESEARCH**  
Working name: **Reny Shaders**  
Primary visual target: **The Reawakening**  
Platform target: **Minecraft 1.7.10 shader-capable client stack**

## 1. Purpose

This document records the agreed product direction, visual identity, performance philosophy, project boundaries, and research questions for the proposed Reny Shaders project.

It exists so agents do not turn an intentionally specialized performance-first shader into a generic "light shader", a reduced clone of a modern shaderpack, or an unbounded collection of expensive effects.

This is a **vision and constraint document**, not an implementation specification.

Statements are classified as:

- **DECIDED** — current project direction; preserve it unless the maintainer explicitly changes it.
- **RESEARCH** — promising direction that must be validated against the actual Minecraft 1.7.10 / OptiFine-era pipeline and target hardware.
- **NON-GOAL** — behavior that should not become a default design target.

## 2. Project identity

### DECIDED — Reny is a family of performance-oriented technology

The intended relationship is:

- **Reny Optimization** reduces the cost of Minecraft/Forge/modpack execution and coordinates optimization capabilities.
- **Reny Shaders** maximizes visual quality under a strict rendering budget.
- **The Reawakening** is the integrated gameplay experience that can use both.

Reny Shaders should be capable of becoming the **official visual renderer/shaderpack of The Reawakening**, while remaining conceptually separate from Reny Optimization.

The current working name is **Reny Shaders**. A pack-specific label such as **Reny Shaders — Reawakening Edition** may be useful later, but branding is not the architectural priority at this stage.

### DECIDED — project boundary

Reny Optimization must not become dependent on the shaderpack.

Reny Shaders may eventually exploit optional capabilities exposed by Reny Optimization or a future Reny renderer/backend, but the two projects must keep their responsibilities clear.

- Reny Optimization optimizes and coordinates rendering infrastructure.
- Reny Shaders defines how the scene should look within an explicit performance budget.
- The Reawakening supplies the primary artistic and real-world workload context.

## 3. Governing metric

### DECIDED — optimize visual quality per unit of GPU time

The central design objective is:

> **Maximize perceived visual quality per millisecond of GPU time.**

Reny Shaders is not a project for maximum physical correctness.

If two techniques produce effectively equivalent perceived quality during normal gameplay and one is substantially cheaper, the cheaper approximation is preferred even when it is less physically correct.

Valid tools include, when they survive measurement and visual inspection:

- fake or approximate lighting;
- low-resolution intermediate buffers;
- reconstruction;
- dithering;
- lookup tables;
- analytic approximations;
- distance-based degradation;
- interleaved or checkerboard sampling;
- temporal reuse;
- packed data;
- reuse of vanilla information;
- intentionally short effect ranges;
- visual masking through fog and atmosphere;
- any other technique that reduces real cost without visibly breaking the intended image.

"Correct but expensive" is not automatically better than "convincing and cheap".

## 4. Visual identity

### DECIDED — dark fantasy, not generic cinematic realism

The target identity is **dark fantasy with strong atmosphere and readable gameplay**.

Reny Shaders should not look like a generic modern shaderpack with effects enabled because they are fashionable. It should reinforce the tone of The Reawakening.

### Day

Target qualities:

- natural but slightly severe atmosphere;
- restrained saturation rather than oversaturated foliage;
- warm directional sunlight;
- slightly cooler shadow language where useful;
- depth expressed through atmosphere and fog;
- clear terrain readability;
- strong but not noisy contrast.

### Sunset and transitional light

Sunrise and sunset may become substantially more dramatic than midday.

This is a high-value place for:

- warm directional light;
- stronger atmospheric color separation;
- silhouettes;
- fog depth;
- selective bloom and emission contrast.

The goal is mood, not expensive physical simulation.

### Night

Night should feel meaningfully dark.

However, darkness must not destroy navigation or combat readability.

The target is:

> **dark world, readable silhouettes.**

The player should feel that the environment is dangerous and visually transformed at night without needing to defeat the shader by raising gamma.

### Supernatural and magical elements

The natural world should be comparatively restrained so supernatural content has visual authority.

Magic, lava, portals, special ores, Tensura-related effects, rituals, energy systems, unusual entities, and other supernatural elements may use:

- strong emissive contrast;
- distinctive color identity;
- selective bloom;
- local atmosphere;
- intentionally heightened visual language.

This creates a deliberate hierarchy:

> **restrained world -> visually powerful supernatural phenomena**

### NON-GOAL — cinematic clutter by default

The default experience should not depend on:

- constant depth of field;
- motion blur;
- aggressive lens flare;
- chromatic aberration;
- excessive bloom;
- effects that reduce combat or navigation readability;
- post-processing included only because other shaderpacks have it.

Such effects may exist experimentally or in a showcase configuration only if they have a clear purpose.

## 5. Rendering philosophy

### DECIDED — spend GPU time only where the player perceives value

High-value systems currently include:

- tone mapping;
- color grading;
- sky;
- fog and atmosphere;
- directional lighting;
- nearby shadows;
- water treatment;
- emissive treatment;
- selective low-cost bloom;
- subtle vegetation movement where cheap.

Candidate expensive systems are **not** entitled to exist by default.

### Initial priority model

| System | Initial direction |
| --- | --- |
| Tone mapping | core, cheap |
| Color grading | core, cheap |
| Sky | analytic and cheap where possible |
| Fog / atmosphere | high visual priority |
| Vegetation movement | vertex-oriented, low cost |
| Water | cheap normals + Fresnel + approximation before expensive reflection |
| Bloom | low-resolution and selective |
| Emissives | high priority |
| Shadows | local and limited, then faded before long-distance brute force |
| AO | reuse or cheap approximation before expensive screen-space work |
| God rays | optional and reduced-resolution if retained |
| SSR | not a default requirement |
| GI | not a default requirement |
| Volumetric clouds | not a default requirement |
| Full PBR | not a default requirement |
| Parallax | not a default requirement |

This table is architectural direction, not proof that every listed technique is feasible in the final 1.7.10 pipeline.

## 6. The full-resolution rule

### DECIDED

> **No effect earns a dedicated full-resolution pass without evidence that the visual return justifies the cost.**

Every candidate effect should first be evaluated for:

- lower-resolution execution;
- shared passes;
- shared samples;
- data reuse;
- packed channels;
- cheaper analytic substitutes;
- vertex-stage alternatives;
- distance-limited execution;
- lower update frequency;
- reconstruction;
- temporal or interleaved sampling when available.

Integrated GPUs and older hardware are first-class targets. Bandwidth and memory traffic matter, not only shader arithmetic.

## 7. Shadows: spend detail near the player

### DECIDED

Reny Shaders should prefer:

> **high-value nearby shadowing -> simplified or faded mid-range -> atmosphere and fog instead of brute-force distant shadows**

A distant tree shadow does not need to be perfectly simulated if the final scene can hide that loss through distance, light balance, fog, and atmosphere.

Exact shadow-map resolutions, ranges, filtering methods, and cascade or ring strategies are **RESEARCH** questions and must be selected from benchmark evidence rather than intuition.

## 8. Resolution should not be uniform

### DECIDED

Effects do not need to operate at display resolution merely because the final image does.

Candidate examples:

- bloom at reduced resolution;
- god rays at reduced resolution;
- atmosphere and fog reconstruction;
- cheap AO at reduced or interleaved resolution;
- selective effects only near the camera or only on important material categories.

The implementation must prefer the cheapest resolution and update rate that preserves the intended perception.

## 9. Reuse information before reconstructing it

### DECIDED

The shader should aggressively investigate reuse of information already produced by Minecraft, the active shader pipeline, or other passes.

Potential sources include:

- vanilla lightmap information;
- depth;
- normals when already available;
- material or block identity;
- existing color and alpha channels;
- existing framebuffer data;
- previous-pass outputs.

Do not rebuild expensive information merely to obtain a theoretically cleaner representation.

Buffer formats and channel layouts should be treated as a performance resource.

## 10. Modpack awareness is a feature, not a failure of generality

### DECIDED

Reny Shaders does **not** need to pretend that it knows nothing about The Reawakening.

A generic public shaderpack must guess about arbitrary installations. Reny Shaders can exploit the fact that its primary target has a known mod set, known dimensions, known liquids, known materials, known emissive content, known supernatural systems, and known visual stressors.

This specialization may provide both better visuals and lower cost.

Candidate semantic classes include:

- foliage;
- ordinary terrain;
- water;
- lava;
- metal;
- emissive;
- magic and supernatural;
- Tensura-specific visual categories;
- dimension-specific categories.

### RESEARCH — generated material mapping

If the 1.7.10 shader pipeline exposes enough stable block or material identity, investigate generating shader include or config data directly from the curated The Reawakening configuration rather than implementing expensive generic runtime material inference.

The principle is:

> **If the pack already knows what a material is, do not spend GPU time guessing.**

The exact mechanism depends on the real OptiFine or shader API available in the chosen 1.7.10 stack.

## 11. Dimension-specific art direction

### DECIDED conceptually / RESEARCH technically

Different dimensions may use distinct art-direction parameters while sharing one underlying renderer and shader architecture.

The goal is not multiple unrelated shaders. It is one coherent rendering language with dimension-specific atmosphere.

Examples may include:

- Overworld: grounded dark fantasy;
- Nether: more hostile heat and emission language;
- Twilight Forest: its own atmospheric identity if and when adopted;
- Tensura or other custom dimensions: unique parameters without abandoning the Reny visual grammar.

The exact technical support for dimension-specific shader behavior in the selected 1.7.10 pipeline must be verified before implementation.

## 12. Temporal and interleaved rendering

### RESEARCH — high priority

Temporal reuse is potentially one of the highest-leverage optimization directions.

Instead of paying for all samples every frame, a technique may distribute work across frames or pixels and reconstruct a stable result.

Candidate ideas:

- rotated or interleaved sample patterns;
- checkerboard work;
- temporal accumulation;
- low-sample effects reconstructed over time;
- selectively refreshed expensive information.

This is **not yet an implementation requirement**.

The first task is to determine what history buffers, previous-frame data, motion information, and pipeline hooks are actually available or can be introduced safely in the target 1.7.10 stack.

Ghosting, instability, camera movement, entities, vegetation, particles, and dimension transitions must be treated as first-class failure cases.

## 13. Profiles

### DECIDED — three initial experience classes

### Reny Lite

Purpose:

- lowest practical GPU budget;
- integrated and old GPUs as explicit targets;
- aggressive approximations;
- very limited expensive passes;
- same art direction as higher profiles.

Lite must not become "Reny with the beauty removed". It should expose more of the project's tricks while preserving its identity.

### Reny Default

Purpose:

- **official intended appearance of The Reawakening**;
- primary engineering and art target;
- best quality and performance balance;
- performance budget treated as a hard product constraint.

Most design decisions should be optimized around Default first.

### Reny Showcase

Purpose:

- screenshots;
- stronger hardware;
- experimentation with higher-cost versions of existing effects.

Showcase may spend more, but it should still look like Reny rather than become a different shader.

## 14. Performance acceptance philosophy

### DECIDED

No shader feature is accepted because it "looks cool" in isolation.

A meaningful feature proposal should eventually answer:

1. What visual problem does it solve?
2. What scene demonstrates the improvement?
3. What is the GPU or frame-time cost?
4. What happens to frame-time consistency?
5. What lower-cost approximation was attempted?
6. Can it run at lower resolution or lower frequency?
7. Can existing data be reused?
8. What happens on integrated or old GPUs?
9. Does the feature preserve gameplay readability?
10. Does it preserve the Reny dark-fantasy visual grammar?

When objective GPU timers are available, use them. When they are not, use reproducible scene-level frame-time comparisons and isolate variables as much as the platform permits.

## 15. Relationship with Reny Optimization

### DECIDED

Reny Optimization's existing **0.9 — Shader pipeline** milestone is **not Reny Shaders**.

That milestone concerns infrastructure such as:

- shader-path profiling;
- framebuffer lifecycle;
- state and uniform overhead;
- shadow-pass scaling;
- redundant rendering work;
- shader compatibility;
- possible future renderer or backend support.

Reny Shaders is the visual product that may later benefit from that infrastructure.

Long-term interaction:

    Reny Optimization / renderer capabilities
                   |
                   v
            efficient render path
                   |
                   v
              Reny Shaders
                   |
                   v
        The Reawakening visual identity

Neither project should be made artificially dependent on unfinished future infrastructure.

## 16. Research program before implementation lock-in

Agents should investigate the following branches independently and return evidence rather than guesses.

### R1 — Minecraft 1.7.10 / shader-pipeline archaeology

Determine the **exact** capabilities and constraints of the selected 1.7.10 client stack:

- exact OptiFine and shader implementation version;
- GLSL and OpenGL level;
- available shader stages;
- uniforms and attributes;
- framebuffer count and formats;
- depth, normal, and material access;
- shadow pipeline;
- profile and config support;
- buffer scaling;
- persistent or history-buffer possibilities;
- block, entity, and material identifiers;
- dimension behavior;
- driver-specific restrictions.

Do not assume that a feature documented in a modern OptiFine version exists in the selected 1.7.10 version.

### R2 — efficient shader prior art

Study unusually efficient Minecraft shaders and relevant real-time rendering techniques.

Extract:

- algorithms;
- approximations;
- failure modes;
- bandwidth-saving techniques;
- sample-reduction techniques;
- useful profiling methodology.

Do not copy code without verifying its license and compatibility.

### R3 — The Reawakening render audit

Inventory the pack's actual visual workload:

- mods and render hooks;
- dimensions;
- liquids;
- vegetation;
- emissive blocks;
- particles;
- TileEntity renderers;
- unusual entities;
- portals;
- sky systems;
- magic systems;
- post-processing conflicts;
- shader-hostile content.

Optimize for the real pack, not an imagined generic installation.

### R4 — target-hardware matrix

Integrated GPUs and older or modest hardware are first-class test platforms.

Research and benchmarks should include hardware that exposes:

- memory-bandwidth limits;
- weak shader throughput;
- driver limitations;
- poor framebuffer or pass scaling;
- CPU and GPU contention.

A technique that is cheap on a modern dedicated GPU may still be the wrong technique for Reny.

### R5 — effect microbenchmarks

Measure candidate costs independently where possible:

- shadow resolution, range, and filtering;
- AO;
- bloom;
- water;
- fog;
- god rays;
- normals and noise;
- extra framebuffer passes;
- half and quarter-resolution variants;
- temporal and interleaved variants.

### R6 — prototype the cheats

Prioritize prototypes of:

- half and quarter-resolution effects;
- checkerboard and interleaved sampling;
- dithering;
- rotated samples;
- LUT-driven approximations;
- material packing;
- shadow fading;
- vertex-based movement;
- fake reflection;
- analytic sky and fog;
- temporal reconstruction if technically possible.

## 17. Agent operating rules

Agents working on Reny Shaders research or implementation must follow these rules:

1. **Preserve the dark-fantasy direction.**
2. **Optimize perceived quality per GPU millisecond, not feature count.**
3. **Do not assume modern OptiFine features exist on 1.7.10. Verify them.**
4. **Separate facts, measured results, hypotheses, and aesthetic choices.**
5. **Benchmark before claiming performance wins.**
6. **Prefer a cheap approximation before proposing an expensive simulation.**
7. **Do not add a full-resolution pass without justification.**
8. **Treat integrated and older GPUs as target hardware, not edge cases.**
9. **Exploit curated The Reawakening knowledge when it removes generic runtime cost.**
10. **Do not hardcode The Reawakening gameplay logic into Reny Optimization core.**
11. **Do not copy shader code without license review.**
12. **Do not silently convert research ideas into requirements.**
13. **Keep Lite, Default, and Showcase visually related.**
14. **Default should remain the canonical The Reawakening appearance.**
15. **When a visually equivalent cheaper technique exists, prefer the cheaper technique.**

## 18. Non-goals

Reny Shaders is not intended to become:

- a benchmark for maximum physical realism;
- a clone of BSL, Complementary, SEUS, or another shaderpack;
- a checklist of every popular shader effect;
- a screenshot-only renderer at the expense of gameplay;
- a shader that hides poor performance behind high average FPS while frame time stutters;
- a universal shader whose generality prevents useful The Reawakening specialization;
- a reason to duplicate expensive information already produced elsewhere;
- a requirement that Reny Optimization must ship before shader research can proceed.

## 19. Definition of success

The project succeeds when:

1. The Reawakening has a distinct, recognizable dark-fantasy visual identity.
2. The default preset remains practical on modest hardware rather than requiring a modern dedicated GPU.
3. Expensive effects exist only when their perceived visual return survives measurement.
4. Lite still looks intentionally like Reny.
5. Showcase remains stylistically Reny rather than becoming a separate renderer.
6. The shader can exploit known modpack semantics without making Reny Optimization pack-dependent.
7. Agents can explain why each major rendering cost exists.
8. Benchmark data can distinguish genuine improvements from placebo visual and performance tuning.
9. The shader and Reny Optimization can evolve independently while benefiting from each other when optional integration becomes available.

## 20. Current decision boundary

### Frozen enough to guide research

- Working identity: **Reny Shaders**.
- Primary visual home: **The Reawakening**.
- Art direction: **dark fantasy, atmospheric, readable, restrained natural world, powerful supernatural contrast**.
- Engineering objective: **maximum perceived visual quality per GPU millisecond**.
- Approximation and "cheats" are explicitly allowed.
- Full-resolution work must earn its cost.
- Integrated and older GPUs are first-class targets.
- Three-profile model: **Lite / Default / Showcase**.
- The shaderpack remains distinct from Reny Optimization's shader-pipeline and backend work.

### Intentionally not frozen

- exact OptiFine build;
- exact GLSL and OpenGL feature baseline;
- exact shadow algorithm, resolution, and range;
- exact AO implementation;
- exact water reflection strategy;
- exact framebuffer layout;
- exact material-ID mechanism;
- exact dimension override mechanism;
- whether temporal accumulation is viable;
- exact performance budgets and hardware floor.

Those questions belong to the research phase and must be answered with evidence.
