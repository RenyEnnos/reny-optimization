package dev.reny.optimization.devbridge;

import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import dev.reny.minecraftdev.bridge.BridgeCore;
import dev.reny.minecraftdev.bridge.DevBridge;
import dev.reny.minecraftdev.bridge.MainThreadGate;
import dev.reny.optimization.profiler.InternalProfiler;

/** Server-only, opt-in adapter for the generic minecraft-dev-toolkit bridge. */
public final class RenyDevBridgeBootstrap {

    private static volatile boolean initialized;

    private RenyDevBridgeBootstrap() {}

    public static synchronized void initialize() {
        if (initialized || !Boolean.parseBoolean(System.getProperty("minecraft.dev.bridge"))) return;
        final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<Runnable>();
        MainThreadGate gate = new MainThreadGate(new Executor() {

            @Override
            public void execute(Runnable command) {
                queue.offer(command);
            }
        }, 8, 2000L);
        DevBridge.configure(new RenySnapshotProvider(gate), token());
        DevBridge.registerExtension(new BridgeCore.Extension() {

            @Override
            public String id() {
                return "reny";
            }

            @Override
            public java.util.List<String> capabilities() {
                return Collections.singletonList("profiler.read");
            }

            @Override
            public String path() {
                return "/reny/profiler";
            }

            @Override
            public String read() throws Exception {
                return gate.call(new java.util.concurrent.Callable<String>() {

                    @Override
                    public String call() {
                        return RenyProfilerJson.snapshot(InternalProfiler.get());
                    }
                });
            }
        });
        FMLCommonHandler.instance()
            .bus()
            .register(new Object() {

                @cpw.mods.fml.common.eventhandler.SubscribeEvent
                public void tick(ServerTickEvent event) {
                    for (int i = 0; i < 64; i++) {
                        Runnable task = queue.poll();
                        if (task == null) break;
                        task.run();
                    }
                }
            });
        DevBridge.maybeArm();
        initialized = true;
    }

    private static String token() {
        String value = System.getProperty("minecraft.dev.bridge.token");
        if (value == null || value.length() == 0) value = System.getenv("MINECRAFT_DEV_BRIDGE_TOKEN");
        return value;
    }
}
