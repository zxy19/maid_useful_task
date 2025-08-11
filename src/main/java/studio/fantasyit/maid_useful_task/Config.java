package studio.fantasyit.maid_useful_task;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLE_LOGGING = BUILDER
            .define("functions.logging", true);
    private static final ModConfigSpec.BooleanValue ENABLE_REVIVE = BUILDER
            .define("functions.revive", true);
    private static final ModConfigSpec.BooleanValue ENABLE_LOCATE = BUILDER
            .define("functions.locate", true);

    private static final ModConfigSpec.BooleanValue ENABLE_REVIVE_AGGRO = BUILDER
            .define("revive.aggro", false);
    private static final ModConfigSpec.BooleanValue ENABLE_REVIVE_TOTEM = BUILDER
            .define("revive.totem", true);

    private static final ModConfigSpec.BooleanValue LOGGING_DISABLE_BLOCKUP = BUILDER
            .define("logging.disable_blockup", false);

    private static final ModConfigSpec.BooleanValue ENABLE_VEHICLE_CONTROL_FULL = BUILDER
            .define("vehicle_control.full", true);
    private static final ModConfigSpec.BooleanValue ENABLE_VEHICLE_CONTROL_ROTATE = BUILDER
            .define("vehicle_control.rotate", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enableLoggingTask = false;
    public static boolean enableReviveTask = false;
    public static boolean enableLocateTask = false;

    public static boolean enableReviveAggro = false;
    public static boolean enableReviveTotem = false;

    public static boolean enableVehicleControlFull = false;
    public static boolean enableVehicleControlRotate = false;

    public static boolean disableLoggingBlockUp = false;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enableLoggingTask = ENABLE_LOGGING.get();
        enableReviveTask = ENABLE_REVIVE.get();
        enableLocateTask = ENABLE_LOCATE.get();
        enableReviveAggro = ENABLE_REVIVE_AGGRO.get();
        enableReviveTotem = ENABLE_REVIVE_TOTEM.get();
        enableVehicleControlFull = ENABLE_VEHICLE_CONTROL_FULL.get();
        enableVehicleControlRotate = ENABLE_VEHICLE_CONTROL_ROTATE.get();
        disableLoggingBlockUp = LOGGING_DISABLE_BLOCKUP.get();
    }
}
