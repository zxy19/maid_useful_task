package studio.fantasyit.maid_useful_task.vehicle;

import net.minecraft.nbt.CompoundTag;

public interface IVirtualControl {
    void maid_useful_tasks$setControlParam(float xRot, float yRot, float speed, MaidVehicleControlType type);

    void maid_useful_tasks$stopControl();

    CompoundTag maid_useful_tasks$getControlParam();

    void maid_useful_tasks$setControlParam(CompoundTag target);
}
