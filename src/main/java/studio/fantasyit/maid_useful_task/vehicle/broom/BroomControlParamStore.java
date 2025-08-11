package studio.fantasyit.maid_useful_task.vehicle.broom;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.nbt.CompoundTag;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleControlType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BroomControlParamStore {
    public record BroomControlParam(float xRot, float yRot, float vertical, float forward,
                                    MaidVehicleControlType type) {
        public static BroomControlParam fromNbt(CompoundTag tag) {
            return new BroomControlParam(
                    tag.getFloat("xRot"),
                    tag.getFloat("yRot"),
                    tag.getFloat("vertical"),
                    tag.getFloat("forward"),
                    MaidVehicleControlType.valueOf(tag.getString("type"))
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

    private static final BroomControlParam NONE = new BroomControlParam(0, 0, 0, 0, MaidVehicleControlType.NONE);

    private static final Map<UUID, BroomControlParam> store = new HashMap<>();

    public static void setControlParam(EntityMaid maid, BroomControlParam param) {
        store.put(maid.getUUID(), param);
    }

    public static BroomControlParam getControlParam(EntityMaid maid) {
        if (!store.containsKey(maid.getUUID()))
            return NONE;
        return store.get(maid.getUUID());
    }

    public static void removeControlParam(EntityMaid maid) {
        store.remove(maid.getUUID());
    }
}
