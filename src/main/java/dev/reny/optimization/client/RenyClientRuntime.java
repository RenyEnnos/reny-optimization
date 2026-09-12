package dev.reny.optimization.client;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.ClientCommandHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.reny.optimization.benchmark.BenchmarkConfigHasher;
import dev.reny.optimization.benchmark.BenchmarkContext;
import dev.reny.optimization.benchmark.BenchmarkContextProvider;
import dev.reny.optimization.benchmark.BenchmarkContexts;
import dev.reny.optimization.benchmark.BenchmarkController;
import dev.reny.optimization.benchmark.BenchmarkScenario;
import dev.reny.optimization.command.RenyBenchmarkCommand;

/** Client-only command bridge that captures render settings and camera metadata. */
@SideOnly(Side.CLIENT)
public final class RenyClientRuntime {

    private static boolean initialized;

    private RenyClientRuntime() {}

    public static void initialize(BenchmarkController controller) {
        if (initialized) {
            return;
        }
        ClientCommandHandler.instance
            .registerCommand(new RenyBenchmarkCommand(controller, new BenchmarkContextProvider() {

                @Override
                public BenchmarkContext create(ICommandSender sender, BenchmarkScenario scenario) {
                    return createClientContext(scenario);
                }
            }));
        initialized = true;
    }

    private static BenchmarkContext createClientContext(BenchmarkScenario scenario) {
        Minecraft minecraft = Minecraft.getMinecraft();
        BenchmarkContext.Builder builder = BenchmarkContexts.builderFor(scenario)
            .extra("context_source", "client-command")
            .extra("shader_metadata_source", "reny.benchmark.shader.* system properties")
            .configHash(
                BenchmarkContexts.property(
                    "reny.benchmark.config.hash",
                    BenchmarkConfigHasher.hashProfile(minecraft == null ? null : minecraft.mcDataDir)));
        if (minecraft == null) {
            return builder.world("unknown", "no-client", "unknown", "unknown")
                .build();
        }
        if (minecraft.gameSettings != null) {
            builder
                .display(
                    minecraft.displayWidth,
                    minecraft.displayHeight,
                    minecraft.gameSettings.renderDistanceChunks,
                    minecraft.gameSettings.enableVsync,
                    minecraft.gameSettings.limitFramerate)
                .extra("fps_cap_semantics", isUncapped(minecraft.gameSettings.limitFramerate) ? "uncapped" : "capped");
        }
        World world = minecraft.theWorld;
        if (world == null) {
            return builder.world("unknown", "no-world", "unknown", "unknown")
                .build();
        }
        WorldInfo info = world.getWorldInfo();
        String worldName = info == null ? "unknown" : info.getWorldName();
        String weather = BenchmarkContexts.weather(world);
        String route = playerRoute(minecraft);
        builder
            .world(
                String.valueOf(world.getSeed()),
                worldName + ",dimension=" + world.provider.dimensionId,
                route,
                weather)
            .extra("world_time_ticks", String.valueOf(world.getWorldTime()))
            .extra("weather", weather)
            .extra("primary_run_settings_valid", primarySettingsValid(minecraft));
        return builder.build();
    }

    private static String playerRoute(Minecraft minecraft) {
        if (minecraft.thePlayer == null) {
            return "unknown";
        }
        return String.format(
            Locale.ROOT,
            "x=%.3f,y=%.3f,z=%.3f,yaw=%.3f,pitch=%.3f",
            Double.valueOf(minecraft.thePlayer.posX),
            Double.valueOf(minecraft.thePlayer.posY),
            Double.valueOf(minecraft.thePlayer.posZ),
            Float.valueOf(minecraft.thePlayer.rotationYaw),
            Float.valueOf(minecraft.thePlayer.rotationPitch));
    }

    private static String primarySettingsValid(Minecraft minecraft) {
        if (minecraft.gameSettings == null) {
            return "unknown";
        }
        return !minecraft.gameSettings.enableVsync && isUncapped(minecraft.gameSettings.limitFramerate) ? "true"
            : "false";
    }

    /** Minecraft 1.7.10 uses the slider maximum (260) as its uncapped sentinel. */
    private static boolean isUncapped(int limitFramerate) {
        return limitFramerate >= (int) GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }
}
