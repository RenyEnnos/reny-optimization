package dev.reny.optimization.devbridge;

import dev.reny.optimization.profiler.DurationSeriesSnapshot;
import dev.reny.optimization.profiler.InternalProfiler;
import dev.reny.optimization.profiler.ProfilerSnapshot;

/** Deterministic, read-only JSON projection; called only on an explicit bridge request. */
public final class RenyProfilerJson {

    private RenyProfilerJson() {}

    public static String snapshot(InternalProfiler profiler) {
        ProfilerSnapshot s = profiler.snapshot();
        StringBuilder b = new StringBuilder(1024);
        b.append("{\"enabled\":")
            .append(s.isEnabled())
            .append(",\"frameId\":")
            .append(s.getCurrentFrameId())
            .append(",\"tickId\":")
            .append(s.getCurrentTickId())
            .append(",\"frames\":")
            .append(series(s.getFrames()))
            .append(",\"ticks\":")
            .append(series(s.getTicks()))
            .append(",\"runtime\":");
        ProfilerSnapshot.RuntimeSnapshot r = s.getRuntime();
        b.append("{\"heapUsedBytes\":")
            .append(r.getHeapUsedBytes())
            .append(",\"heapCommittedBytes\":")
            .append(r.getHeapCommittedBytes())
            .append(",\"gcCount\":")
            .append(r.getGcCount())
            .append(",\"gcTimeMillis\":")
            .append(r.getGcTimeMillis())
            .append(",\"queuedTasks\":")
            .append(r.getQueuedTasks())
            .append(",\"activeTasks\":")
            .append(r.getActiveTasks())
            .append(",\"submittedTasks\":")
            .append(r.getSubmittedTasks())
            .append(",\"completedTasks\":")
            .append(r.getCompletedTasks())
            .append("},\"sections\":[");
        ProfilerSnapshot.SectionSnapshot[] sections = s.getSections();
        for (int i = 0; i < sections.length; i++) {
            if (i > 0) b.append(',');
            ProfilerSnapshot.SectionSnapshot x = sections[i];
            b.append("{\"name\":\"")
                .append(
                    x.getSection()
                        .name())
                .append("\",\"calls\":")
                .append(x.getCallCount())
                .append(",\"totalNanos\":")
                .append(x.getTotalNanos())
                .append(",\"maxNanos\":")
                .append(x.getMaxNanos())
                .append('}');
        }
        return b.append("]}")
            .toString();
    }

    private static String series(DurationSeriesSnapshot x) {
        return "{\"totalSamples\":" + x.getTotalSamples()
            + ",\"droppedSamples\":"
            + x.getDroppedSamples()
            + ",\"retainedSamples\":"
            + x.size()
            + "}";
    }
}
