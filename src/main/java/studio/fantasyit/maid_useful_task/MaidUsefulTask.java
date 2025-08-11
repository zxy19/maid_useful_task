package studio.fantasyit.maid_useful_task;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import studio.fantasyit.maid_useful_task.registry.GuiRegistry;
import studio.fantasyit.maid_useful_task.registry.MemoryModuleRegistry;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleManager;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MaidUsefulTask.MODID)
public class MaidUsefulTask {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "maid_useful_task";

    public MaidUsefulTask() {
        IEventBus modEventBus = ModLoadingContext.get().getActiveContainer().getEventBus();
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        MemoryModuleRegistry.register(modEventBus);
        GuiRegistry.init(modEventBus);
        MaidVehicleManager.register();
    }
}
