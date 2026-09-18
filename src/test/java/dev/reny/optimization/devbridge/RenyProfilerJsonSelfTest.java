package dev.reny.optimization.devbridge;

import dev.reny.optimization.profiler.InternalProfiler;

/** Dependency-free executable check for the bridge projection. */
public final class RenyProfilerJsonSelfTest {

    private RenyProfilerJsonSelfTest() {}

    public static void main(String[] args) {
        InternalProfiler profiler = new InternalProfiler(4, 4);
        String json = RenyProfilerJson.snapshot(profiler);
        require(json.indexOf("\"enabled\":") >= 0, "enabled");
        require(json.indexOf("\"frameId\":") >= 0, "frameId");
        require(json.indexOf("\"runtime\":") >= 0, "runtime");
        require(json.indexOf("\"sections\":[") >= 0, "sections");
        if (json.indexOf('\n') >= 0 || json.indexOf('\r') >= 0)
            throw new AssertionError("non-deterministic whitespace");
        System.out.println("Reny profiler JSON self-test passed");
    }

    private static void require(boolean value, String name) {
        if (!value) throw new AssertionError(name);
    }
}
