# Known limitations

The following limitations are explicit and must not be converted into stronger
claims in the final report:

- The formal matrix is incomplete. Until all completed cells have five valid
  runs, the baseline status is `PARTIAL`, not `COMPLETE`.
- BENCH-03 and BENCH-06 in the minimal profile are labeled proxies. The
  minimal world does not contain a constructed machine network or TileEntity
  farm, and it is not equivalent to the heavy industrial scene.
- The current runtime exports frame/tick series and JVM/GC deltas. Section rows
  exist, but active Forge/entity/TileEntity/chunk/lighting hooks are not yet
  enabled; subsystem attribution is therefore `UNPROVEN`.
- No external CPU profiler or GPU hardware-timer trace is claimed. The export
  records the available Intel/Mesa/OpenGL identity, but that is not a measured
  CPU/GPU bottleneck split.
- The exact Sildur pack loaded in the isolated heavy shader boot and entered a
  world, but legacy shader block mappings and missing texture warnings remain
  recorded compatibility noise.
- A delayed exporter write was observed once after the COMPLETE log. The runner
  now waits for all four export files before validation; the original rejected
  event is retained and the late-created export is revalidated separately.
- Forge/OptiFine update-check socket failures and the known nonfatal UniMixins
  crash-enhancer warning are environment noise, not proof of a performance
  bottleneck.
