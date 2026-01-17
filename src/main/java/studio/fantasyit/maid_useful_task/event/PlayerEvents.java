package studio.fantasyit.maid_useful_task.event;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.data.MaidReviveGlobalData;

@Mod.EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
