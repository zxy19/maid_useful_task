package studio.fantasyit.maid_useful_task.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.data.MaidReviveGlobalData;

@EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PlayerEvents {
    @SubscribeEvent
    public static void onPlayerEnter(PlayerEvent.PlayerLoggedInEvent event) {
        MaidReviveGlobalData.clearRescuingMaid(event.getEntity().getUUID());
    }
    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        MaidReviveGlobalData.clearRescuingMaid(event.getEntity().getUUID());
    }
}
