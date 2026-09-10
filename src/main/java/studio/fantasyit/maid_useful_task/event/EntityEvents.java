package studio.fantasyit.maid_useful_task.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.task.MaidLocateTask;
import studio.fantasyit.maid_useful_task.util.WrappedMaidFakePlayer;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleManager;
import studio.fantasyit.maid_useful_task.vehicle.broom.BroomControlParamStore;

/**
 * 女仆实体离开世界（死亡、卸载、跨维度、被移除）时统一释放静态缓存，
 * 避免 FakePlayer、扫帚控制参数、同步缓存等在长时间运行的服务器上越积越多。
 */
@Mod.EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {
    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof EntityMaid maid)) return;
        MaidVehicleManager.onMaidWithoutVehicle(maid);
        BroomControlParamStore.removeControlParam(maid);
        WrappedMaidFakePlayer.invalidate(maid);
        MaidLocateTask.invalidateCache(maid);
    }
}
