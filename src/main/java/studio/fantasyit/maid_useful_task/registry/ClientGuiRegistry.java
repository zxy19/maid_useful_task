package studio.fantasyit.maid_useful_task.registry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.menu.MaidLoggingConfigGui;
import studio.fantasyit.maid_useful_task.menu.MaidReviveConfigGui;

@EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientGuiRegistry {
    @SubscribeEvent
    public static void init(RegisterMenuScreensEvent event) {
        event.register(GuiRegistry.MAID_LOGGING_CONFIG_GUI.get(), MaidLoggingConfigGui::new);
        event.register(GuiRegistry.MAID_REVIVE_CONFIG_GUI.get(), MaidReviveConfigGui::new);
    }
}
