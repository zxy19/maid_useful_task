package studio.fantasyit.maid_useful_task.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.control.MaidMoveControl;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

@Mixin(MaidMoveControl.class)
abstract public class MaidMoveControlMixin {
    @Shadow(remap = false)
    @Final
    private EntityMaid maid;

    @Inject(method = "tick", at = @At("HEAD"), remap = false, cancellable = true)
    public void tick(CallbackInfo ci) {
        if (MemoryUtil.getBlockUpContext(this.maid).hasTarget()) {
            if (MemoryUtil.getBlockUpContext(this.maid).isOnLine(this.maid.blockPosition())) {
                ci.cancel();
            }
        }
    }
}
