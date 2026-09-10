package studio.fantasyit.maid_useful_task;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import studio.fantasyit.maid_useful_task.registry.GuiRegistry;
import studio.fantasyit.maid_useful_task.registry.MemoryModuleRegistry;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleManager;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MaidUsefulTask.MODID)
public class MaidUsefulTask {
    public static final Logger logger = LogUtils.getLogger();
    public static final String MODID = "maid_useful_task";

    @SuppressWarnings("removal")
    public MaidUsefulTask() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        MemoryModuleRegistry.register(modEventBus);
        GuiRegistry.init(modEventBus);
        MaidVehicleManager.register();
    }
}
