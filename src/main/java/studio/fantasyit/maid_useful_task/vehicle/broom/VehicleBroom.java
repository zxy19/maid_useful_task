package studio.fantasyit.maid_useful_task.vehicle.broom;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityBroom;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.util.RotUtil;
import studio.fantasyit.maid_useful_task.vehicle.AbstractMaidControllableVehicle;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleControlType;

public class VehicleBroom extends AbstractMaidControllableVehicle {
    @Override
    public boolean isMaidOnThisVehicle(EntityMaid maid) {
        return maid.getVehicle() instanceof EntityBroom;
    }

    @Override
    public void maidControlVehicle(EntityMaid maid, MaidVehicleControlType type, BlockPos target) {
        if (maid.level().isClientSide) return;
        if (maid.getVehicle() instanceof EntityBroom vehicle) {
            if (type == MaidVehicleControlType.NONE) {
                BroomControlParamStore.removeControlParam(maid);
                return;
            }
            double xzDistance = maid.distanceToSqr(target.getX(), maid.getY(), target.getZ());
            double finalXRot = vehicle.getXRot();
            double finalYRot = vehicle.getYRot();
            double finalVertical = 0;
            double finalForward;
            if (vehicle.isInWater()) {
                finalXRot = 0;
                finalVertical = 0.1;
            } else if (xzDistance < Math.pow(maid.getY() - maid.level().getSeaLevel(), 2)) {
                finalXRot = 0;
                if (maid.getY() - maid.level().getSeaLevel() < 50 || vehicle.onGround()) {
                    finalXRot = 5;
                    finalVertical = -0.2;
                } else {
                    finalXRot = 50;
                    finalVertical = -0.35;
                }
            } else if (maid.getY() < 100) {
                finalXRot = -50;
                finalVertical = 0.2;
            } else if (maid.getY() > 160) {
                finalXRot = 0;
                finalVertical = -0.1;
            } else {
                finalXRot = 0;
                finalVertical = 0.05;
            }

            finalYRot = RotUtil.getYRot(maid.position(), target.getCenter());

            finalForward = type == MaidVehicleControlType.FULL ? 3.0 : 0;
            if (xzDistance < 5 * 5)
                finalForward = 0;

            BroomControlParamStore.setControlParam(maid, new BroomControlParamStore.BroomControlParam((float) finalXRot, (float) finalYRot, (float) finalVertical, (float) finalForward, type));
        }
    }

    @Override
    public void maidStopControlVehicle(EntityMaid maid) {
        BroomControlParamStore.removeControlParam(maid);
    }

    @Override
    public void syncVehicleParameter(EntityMaid maid, CompoundTag tag) {
        BroomControlParamStore.BroomControlParam broomControlParam = BroomControlParamStore.BroomControlParam.fromNbt(tag);
        BroomControlParamStore.setControlParam(maid, broomControlParam);
    }

    @Override
    public @Nullable CompoundTag getSyncVehicleParameter(EntityMaid maid) {
        return BroomControlParamStore.getControlParam(maid).toNbt();
    }
}
