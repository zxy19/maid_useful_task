package studio.fantasyit.maid_useful_task.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.item.AbstractEntityFromItem;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityBroom;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.fantasyit.maid_useful_task.vehicle.IVirtualControl;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleControlType;

@Mixin(EntityBroom.class)
abstract public class VehicleBroomMixin extends AbstractEntityFromItem implements IVirtualControl {
    public VehicleBroomMixin(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Unique
    public MaidVehicleControlType maid_useful_tasks$vc_type = MaidVehicleControlType.NONE;
    @Unique
    public float maid_useful_tasks$vc_xRot;
    @Unique
    public float maid_useful_tasks$vc_yRot;
    @Unique
    public float maid_useful_tasks$vc_speed;

    @Override
    public void maid_useful_tasks$setControlParam(float xRot, float yRot, float speed, MaidVehicleControlType type) {
        this.maid_useful_tasks$vc_xRot = xRot;
        this.maid_useful_tasks$vc_yRot = yRot;
        this.maid_useful_tasks$vc_speed = speed;
        this.maid_useful_tasks$vc_type = type;
    }

    @Override
    public CompoundTag maid_useful_tasks$getControlParam() {
        CompoundTag result = new CompoundTag();
        result.putFloat("xRot", maid_useful_tasks$vc_xRot);
        result.putFloat("yRot", maid_useful_tasks$vc_yRot);
        result.putFloat("speed", maid_useful_tasks$vc_speed);
        result.putString("type", maid_useful_tasks$vc_type.name());
        return result;
    }

    @Override
    public void maid_useful_tasks$setControlParam(CompoundTag target) {
        if (target.contains("type")) {
            this.maid_useful_tasks$vc_type = MaidVehicleControlType.valueOf(target.getString("type"));
        }
        if (target.contains("xRot")) {
            this.maid_useful_tasks$vc_xRot = target.getFloat("xRot");
        }
        if (target.contains("yRot")) {
            this.maid_useful_tasks$vc_yRot = target.getFloat("yRot");
        }
        if (target.contains("speed")) {
            this.maid_useful_tasks$vc_speed = target.getFloat("speed");
        }
    }

    @Override
    public void maid_useful_tasks$stopControl() {
        this.maid_useful_tasks$vc_type = MaidVehicleControlType.NONE;
    }

    @Inject(method = "tickRidden", at = @At(value = "INVOKE", target = "Lcom/github/tartaricacid/touhoulittlemaid/entity/item/AbstractEntityFromItem;tickRidden(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/Vec3;)V"))
    public void maid_useful_tasks$tickRidden(CallbackInfo ci) {
        if (maid_useful_tasks$vc_type == MaidVehicleControlType.NONE) {
            return;
        }
        this.setRot(maid_useful_tasks$vc_yRot, maid_useful_tasks$vc_xRot);
    }

    @ModifyVariable(method = "travel", at = @At(value = "STORE"), name = "strafe")
    float maid_useful_tasks$travel_s(float strafe) {
        if (maid_useful_tasks$vc_type != MaidVehicleControlType.FULL) {
            return strafe;
        }
        return 0;
    }

    @ModifyVariable(method = "travel", at = @At(value = "STORE"), name = "vertical")
    float maid_useful_tasks$travel_v(float vertical) {
        if (maid_useful_tasks$vc_type != MaidVehicleControlType.FULL) {
            return vertical;
        }
        return -(maid_useful_tasks$vc_xRot - 10.0F) / 22.5F;
    }

    @ModifyVariable(method = "travel", at = @At(value = "STORE"), name = "forward")
    float maid_useful_tasks$travel_f(float forward) {
        if (maid_useful_tasks$vc_type != MaidVehicleControlType.FULL) {
            return forward;
        }
        return maid_useful_tasks$vc_speed;
    }
}
