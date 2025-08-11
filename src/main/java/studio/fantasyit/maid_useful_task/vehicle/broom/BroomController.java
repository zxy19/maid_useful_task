package studio.fantasyit.maid_useful_task.vehicle.broom;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IBroomControl;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityBroom;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleControlType;

public class BroomController implements IBroomControl {
    private final EntityBroom broom;

    public BroomController(EntityBroom broom) {
        this.broom = broom;
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public boolean inControl(Player player, EntityMaid entityMaid) {
        return BroomControlParamStore.getControlParam(entityMaid).type() != MaidVehicleControlType.NONE;
    }

    @Override
    public void travel(Player player, EntityMaid entityMaid) {
        BroomControlParamStore.BroomControlParam param = BroomControlParamStore.getControlParam(entityMaid);

        float forward = 0;
        float strafe = 0;
        float vertical = param.vertical();
        if (param.type() == MaidVehicleControlType.FULL) {
            forward = param.forward() / 15.0f;
        } else {
            boolean keyForward = player.zza > 0;
            boolean keyBack = player.zza < 0;
            boolean keyLeft = player.xxa > 0;
            boolean keyRight = player.xxa < 0;

            if (keyForward || keyBack || keyLeft || keyRight) {
                strafe = keyLeft ? 0.2f : (keyRight ? -0.2f : 0);
                forward = keyForward ? 0.375f : (keyBack ? -0.2f : 0);
            } else {
                vertical = 0;
            }
        }
        //来自PlayerBroomControl
        double playerSpeed = player.getAttributeValue(Attributes.MOVEMENT_SPEED);
        double speed = Math.max(playerSpeed - 0.1, 0) * 2.5 + 0.1;
        Vec3 targetMotion = new Vec3(strafe, vertical, forward).scale(speed * 20);
        targetMotion = targetMotion.yRot((float) (-broom.getYRot() * Math.PI / 180.0));

        // 插值到目标速度，而不是直接累加
        Vec3 currentMotion = broom.getDeltaMovement();
        Vec3 newMotion = currentMotion.lerp(targetMotion, 0.25f);
        broom.setDeltaMovement(newMotion);
    }


    @Override
    public void tickRot(Player player, EntityMaid entityMaid) {
        BroomControlParamStore.BroomControlParam param = BroomControlParamStore.getControlParam(entityMaid);

        broom.yRotO = broom.yBodyRot = broom.yHeadRot = broom.getYRot();
        broom.setRot(param.yRot(), param.xRot());
    }
}
