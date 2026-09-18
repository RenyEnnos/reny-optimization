package dev.reny.optimization.devbridge;

import dev.reny.minecraftdev.bridge.BridgeCore.SnapshotProvider;
import dev.reny.minecraftdev.bridge.MainThreadGate;

/** Generic server/player snapshots; profiler reads are separately gated. */
final class RenySnapshotProvider implements SnapshotProvider {

    private final MainThreadGate gate;

    RenySnapshotProvider(MainThreadGate gate) {
        this.gate = gate;
    }

    @Override
    public String runtime() {
        return "{\"side\":\"server\",\"bridge\":true,\"provider\":\"reny\"}";
    }

    @Override
    public String player() {
        try {
            return gate.call(new java.util.concurrent.Callable<String>() {

                @Override
                public String call() {
                    net.minecraft.server.MinecraftServer server = net.minecraft.server.MinecraftServer.getServer();
                    int count = server == null || server.getConfigurationManager() == null ? 0
                        : server.getConfigurationManager().playerEntityList.size();
                    return "{\"side\":\"server\",\"online\":" + count + "}";
                }
            });
        } catch (Exception e) {
            return "{\"side\":\"server\",\"available\":false}";
        }
    }
}
