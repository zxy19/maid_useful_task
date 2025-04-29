package studio.fantasyit.maid_useful_task.vehicle;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityBroom;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.util.RotUtil;

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
                ((IVirtualControl) vehicle).maid_useful_tasks$setControlParam(0, 0, 0, type);
                return;
            }
            double xzDistance = maid.distanceToSqr(target.getX(), maid.getY(), target.getZ());
            double finalXRot = vehicle.getXRot();
            double finalYRot = vehicle.getYRot();
            double finalSpeed = 0;
            if (vehicle.isInWater()) {
                finalXRot = -10;
            } else if (xzDistance < Math.pow(maid.getY() - maid.level().getSeaLevel(), 2)) {
                if (maid.getY() - maid.level().getSeaLevel() < 50 || vehicle.onGround())
                    finalXRot = 15;
                else
                    finalXRot = 60;
            } else if (maid.getY() < 100) {
                finalXRot = -50;
            } else if (maid.getY() > 160) {
                finalXRot = 15;
            } else {
                finalXRot = 0;
            }

            finalYRot = RotUtil.getYRot(maid.position(), target.getCenter());

            finalSpeed = type == MaidVehicleControlType.FULL ? 3.0 : 0;
            if (xzDistance < 5 * 5 && (vehicle.onGround() || vehicle.isInWater()))
                finalSpeed = 0;

            ((IVirtualControl) vehicle).maid_useful_tasks$setControlParam((float) finalXRot, (float) finalYRot, (float) finalSpeed, type);
        }
    }

    @Override
    public void maidStopControlVehicle(EntityMaid maid) {
        EntityBroom vehicle = (EntityBroom) maid.getVehicle();
        if (vehicle instanceof IVirtualControl ivc)
            ivc.maid_useful_tasks$stopControl();
    }
}
