package studio.fantasyit.maid_useful_task.vehicle.broom;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.nbt.CompoundTag;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleControlType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BroomControlParamStore {
    public record BroomControlParam(float xRot, float yRot, float vertical, float forward,
                                    MaidVehicleControlType type) {
        public static BroomControlParam fromNbt(CompoundTag tag) {
            MaidVehicleControlType type;
            try {
                type = MaidVehicleControlType.valueOf(tag.getString("type"));
            } catch (IllegalArgumentException e) {
                // 数据来自网络包，非法枚举值不应中断 tick
                type = MaidVehicleControlType.NONE;
            }
            return new BroomControlParam(
                    tag.getFloat("xRot"),
                    tag.getFloat("yRot"),
                    tag.getFloat("vertical"),
                    tag.getFloat("forward"),
                    type
            );
        }

        public CompoundTag toNbt() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("xRot", xRot);
            tag.putFloat("yRot", yRot);
            tag.putFloat("vertical", vertical);
            tag.putFloat("forward", forward);
            tag.putString("type", type.name());
            return tag;
        }
    }

    public static final BroomControlParam NONE = new BroomControlParam(0, 0, 0, 0, MaidVehicleControlType.NONE);

    /**
     * 该表同时被服务端线程与客户端网络线程读写（同步包处理、扫帚控制器），
     * 原实现使用 HashMap 存在并发损坏风险；且女仆消失后条目永不移除，长时间运行会持续累积。
     */
    private static final Map<UUID, BroomControlParam> store = new ConcurrentHashMap<>();

    /**
     * 容量上限，超出后整体清空兜底，避免极端情况下无限增长。
     */
    private static final int MAX_SIZE = 512;

    public static void setControlParam(EntityMaid maid, BroomControlParam param) {
        if (param == null || param.equals(NONE)) {
            removeControlParam(maid);
            return;
        }
        if (store.size() >= MAX_SIZE && !store.containsKey(maid.getUUID())) {
            store.clear();
        }
        store.put(maid.getUUID(), param);
    }

    public static BroomControlParam getControlParam(EntityMaid maid) {
        return store.getOrDefault(maid.getUUID(), NONE);
    }

    public static void removeControlParam(EntityMaid maid) {
        store.remove(maid.getUUID());
    }
}
