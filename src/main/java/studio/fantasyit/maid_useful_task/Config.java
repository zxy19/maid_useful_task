package studio.fantasyit.maid_useful_task;

import com.github.tartaricacid.touhoulittlemaid.entity.task.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue SELF_RESCUE = BUILDER
            .define("misc.self_rescue", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_LOGGING = BUILDER
            .define("functions.logging", true);
    private static final ForgeConfigSpec.BooleanValue ENABLE_REVIVE = BUILDER
            .define("functions.revive", true);
    private static final ForgeConfigSpec.BooleanValue ENABLE_LOCATE = BUILDER
            .define("functions.locate", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_REVIVE_AGGRO = BUILDER
            .define("revive.aggro", false);
    private static final ForgeConfigSpec.BooleanValue ENABLE_REVIVE_TOTEM = BUILDER
            .define("revive.totem", true);
    private static final ForgeConfigSpec.BooleanValue ENABLE_REVIVE_PASSIVE = BUILDER
            .define("revive.passive", true);
    private static final ForgeConfigSpec.ConfigValue<List<?>> REVIVE_PASSIVE_PRIORITY_1 = BUILDER
            .defineList("revive.passive_priority.1", List.of(
                    TaskIdle.UID.toString()
            ), t -> t instanceof String);
    private static final ForgeConfigSpec.ConfigValue<List<?>> REVIVE_PASSIVE_PRIORITY_2 = BUILDER
            .defineList("revive.passive_priority.2", List.of(), t -> t instanceof String);
    private static final ForgeConfigSpec.ConfigValue<List<?>> REVIVE_PASSIVE_PRIORITY_4 = BUILDER
            .defineList("revive.passive_priority.4", List.of(
                    TaskAttack.UID.toString(),
                    TaskBowAttack.UID.toString(),
                    TaskCrossBowAttack.UID.toString(),
                    TaskDanmakuAttack.UID.toString(),
                    TaskTridentAttack.UID.toString()
            ), t -> t instanceof String);


    private static final ForgeConfigSpec.BooleanValue LOGGING_DISABLE_BLOCKUP = BUILDER
            .define("logging.disable_blockup", false);

    private static final ForgeConfigSpec.BooleanValue ENABLE_VEHICLE_CONTROL_FULL = BUILDER
            .define("vehicle_control.full", true);
    private static final ForgeConfigSpec.BooleanValue ENABLE_VEHICLE_CONTROL_ROTATE = BUILDER
            .define("vehicle_control.rotate", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean enableSelfRescue = false;

    public static boolean enableLoggingTask = false;
    public static boolean enableReviveTask = false;
    public static boolean enableLocateTask = false;

    public static boolean enableReviveAggro = false;
    public static boolean enableReviveTotem = false;
    public static boolean enableRevivePassive = false;

    public static boolean enableVehicleControlFull = false;
    public static boolean enableVehicleControlRotate = false;

    public static boolean disableLoggingBlockUp = false;

    /**
     * 配置加载发生在 MOD 事件总线（可能非服务端线程），而读取发生在服务端线程，
     * 因此使用并发容器；同时丢弃解析失败的非法资源名，避免写入 null 键。
     */
    public static Map<ResourceLocation, Integer> passiveReviveJobPriority = new ConcurrentHashMap<>();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enableSelfRescue = SELF_RESCUE.get();
        enableLoggingTask = ENABLE_LOGGING.get();
        enableReviveTask = ENABLE_REVIVE.get();
        enableLocateTask = ENABLE_LOCATE.get();
        enableReviveAggro = ENABLE_REVIVE_AGGRO.get();
        enableReviveTotem = ENABLE_REVIVE_TOTEM.get();
        enableRevivePassive = ENABLE_REVIVE_PASSIVE.get();
        enableVehicleControlFull = ENABLE_VEHICLE_CONTROL_FULL.get();
        enableVehicleControlRotate = ENABLE_VEHICLE_CONTROL_ROTATE.get();
        disableLoggingBlockUp = LOGGING_DISABLE_BLOCKUP.get();

        passiveReviveJobPriority.clear();
        setPriority(REVIVE_PASSIVE_PRIORITY_1.get(), 1);
        setPriority(REVIVE_PASSIVE_PRIORITY_2.get(), 2);
        setPriority(REVIVE_PASSIVE_PRIORITY_4.get(), 4);
    }

    private static void setPriority(List<?> list, int priority) {
        for (Object op1 : list) {
            if (op1 instanceof String sp1) {
                try {
                    ResourceLocation rl = ResourceLocation.tryParse(sp1);
                    if (rl == null) {
                        MaidUsefulTask.logger.error("Invalid resource location at level " + priority + ": " + sp1);
                        continue;
                    }
                    passiveReviveJobPriority.put(rl, priority);
                } catch (Exception e) {
                    MaidUsefulTask.logger.error("When parsing level " + priority + " rl: " + sp1);
                }
            } else {
                MaidUsefulTask.logger.error("not a string: " + op1.toString());
            }
        }
    }
}
