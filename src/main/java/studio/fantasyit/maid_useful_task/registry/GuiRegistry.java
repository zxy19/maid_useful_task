package studio.fantasyit.maid_useful_task.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.menu.MaidLoggingConfigGui;
import studio.fantasyit.maid_useful_task.menu.MaidReviveConfigGui;

public class GuiRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MaidUsefulTask.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<MaidLoggingConfigGui.Container>> MAID_LOGGING_CONFIG_GUI = MENU_TYPES.register("maid_logging_config_gui",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new MaidLoggingConfigGui.Container(windowId, inv, data.readInt())));
    public static final DeferredHolder<MenuType<?>, MenuType<MaidReviveConfigGui.Container>> MAID_REVIVE_CONFIG_GUI = MENU_TYPES.register("maid_revive_config_gui",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new MaidReviveConfigGui.Container(windowId, inv, data.readInt())));

    public static void init(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}