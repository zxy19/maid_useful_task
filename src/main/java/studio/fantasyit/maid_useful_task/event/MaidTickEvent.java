package studio.fantasyit.maid_useful_task.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.task.IMaidVehicleControlTask;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleManager;

@Mod.EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MaidTickEvent {
    @SubscribeEvent
    public static void onTick(com.github.tartaricacid.touhoulittlemaid.api.event.MaidTickEvent event) {
        if (event.getMaid().level() instanceof ServerLevel sl)
            if (event.getMaid().getTask() instanceof IMaidVehicleControlTask imvc && event.getMaid().getVehicle() != null) {
                imvc.tick(sl, event.getMaid());
                MaidVehicleManager.syncVehicleParameter(event.getMaid());
            }
    }
}
