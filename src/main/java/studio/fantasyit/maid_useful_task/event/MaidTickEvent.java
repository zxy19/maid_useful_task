package studio.fantasyit.maid_useful_task.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
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
        EntityMaid maid = event.getMaid();
        if (!(maid.level() instanceof ServerLevel sl)) return;
        if (maid.getVehicle() == null) {
            MaidVehicleManager.onMaidWithoutVehicle(maid);
            return;
        }
        if (maid.getTask() instanceof IMaidVehicleControlTask imvc) {
            imvc.tick(sl, maid);
            MaidVehicleManager.syncVehicleParameter(maid);
        } else {
            MaidVehicleManager.stopControlling(maid);
        }
    }
}
