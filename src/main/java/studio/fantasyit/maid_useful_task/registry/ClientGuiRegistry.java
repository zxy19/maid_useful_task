package studio.fantasyit.maid_useful_task.registry;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.menu.MaidLoggingConfigGui;
import studio.fantasyit.maid_useful_task.menu.MaidReviveConfigGui;

@Mod.EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientGuiRegistry {
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(GuiRegistry.MAID_LOGGING_CONFIG_GUI.get(), MaidLoggingConfigGui::new);
        });
        event.enqueueWork(() -> {
            MenuScreens.register(GuiRegistry.MAID_REVIVE_CONFIG_GUI.get(), MaidReviveConfigGui::new);
        });
    }
}
