package studio.fantasyit.maid_useful_task.vehicle;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

abstract public class AbstractMaidControllableVehicle {
    abstract public boolean isMaidOnThisVehicle(EntityMaid maid);

    abstract public void maidStopControlVehicle(EntityMaid maid);

    abstract public void maidControlVehicle(EntityMaid maid, MaidVehicleControlType type, BlockPos target);

    public void syncVehicleParameter(EntityMaid maid, CompoundTag tag){
        if(maid.getVehicle() instanceof IVirtualControl vehicle){
            vehicle.maid_useful_tasks$setControlParam(tag);
        }
    }
    public @Nullable CompoundTag getSyncVehicleParameter(EntityMaid maid) {
        if (maid.getVehicle() instanceof IVirtualControl vehicle) {
            return vehicle.maid_useful_tasks$getControlParam();
        }
        return null;
    }
}
