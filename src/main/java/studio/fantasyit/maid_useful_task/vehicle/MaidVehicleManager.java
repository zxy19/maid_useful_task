package studio.fantasyit.maid_useful_task.vehicle;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.network.PacketDistributor;
import studio.fantasyit.maid_useful_task.network.MaidSyncVehiclePacket;
import studio.fantasyit.maid_useful_task.network.Network;
import studio.fantasyit.maid_useful_task.vehicle.broom.VehicleBroom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

public class MaidVehicleManager {
    /**
     * 兜底停止控制的检查间隔（tick）。
     * 状态切换时（由控制任务切到非控制任务 / 女仆下挂载具）会立即停止，
     * 这里只是周期性兜底，用于覆盖直接改存档等未走正常流程的情况。
     */
    private static final int STOP_CHECK_INTERVAL_TICKS = 20;

    private static final List<AbstractMaidControllableVehicle> controllableVehicles = new ArrayList<>();

    /**
     * 上一次实际发送的参数，用于跳过内容完全相同的重复包。
     * 女仆骑乘但不操控时参数恒为 NONE，原实现会持续以 20 pkt/s 发送不变内容。
     * WeakHashMap：女仆被回收后条目自动消失。
     */
    private static final Map<EntityMaid, CompoundTag> LAST_SENT = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * 当前处于受控状态的女仆。用于在下一次非受控 tick 立即触发一次停止，
     * 避免节流导致扫帚残留控制参数继续飞行。
     */
    private static final Set<EntityMaid> CONTROLLING = Collections.newSetFromMap(
            Collections.synchronizedMap(new WeakHashMap<EntityMaid, Boolean>()));

    public static void register() {
        controllableVehicles.add(new VehicleBroom());
    }

    public static void addControllableVehicle(AbstractMaidControllableVehicle vehicle) {
        controllableVehicles.add(vehicle);
    }

    public static Optional<AbstractMaidControllableVehicle> getControllableVehicle(EntityMaid maid) {
        for (AbstractMaidControllableVehicle vehicle : controllableVehicles) {
            if (vehicle.isMaidOnThisVehicle(maid)) return Optional.of(vehicle);
        }
        return Optional.empty();
    }

    public static void syncVehicleParameter(EntityMaid maid) {
        CONTROLLING.add(maid);
        getControllableVehicle(maid).ifPresent(vehicle -> {
            CompoundTag syncVehicleParameter = vehicle.getSyncVehicleParameter(maid);
            if (syncVehicleParameter == null || syncVehicleParameter.isEmpty()) {
                return;
            }
            // 参数未变化则不重复发包（骑乘待机时参数恒为 NONE）
            CompoundTag last = LAST_SENT.get(maid);
            if (last != null && last.equals(syncVehicleParameter)) {
                return;
            }
            LAST_SENT.put(maid, syncVehicleParameter.copy());
            Network.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> maid),
                    new MaidSyncVehiclePacket(maid.getId(), syncVehicleParameter)
            );
        });
    }

    public static void stopControlling(EntityMaid maid) {
        boolean wasControlling = CONTROLLING.remove(maid);
        if (!wasControlling && Math.floorMod(maid.level().getGameTime() + maid.getId(), STOP_CHECK_INTERVAL_TICKS) != 0) {
            return;
        }
        getControllableVehicle(maid).ifPresent(vehicle -> vehicle.maidStopControlVehicle(maid));
    }

    /**
     * 女仆已不在任何载具上时调用：立即结束控制并清理残留缓存。
     */
    public static void onMaidWithoutVehicle(EntityMaid maid) {
        boolean wasControlling = CONTROLLING.remove(maid);
        LAST_SENT.remove(maid);
        if (wasControlling) {
            for (AbstractMaidControllableVehicle vehicle : controllableVehicles) {
                vehicle.maidStopControlVehicle(maid);
            }
        }
    }
}
