package studio.fantasyit.maid_useful_task.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidFollowOwnerTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import studio.fantasyit.maid_useful_task.task.MaidLocateTask;

@Mixin(MaidFollowOwnerTask.class)
abstract public class FollowMaidMixin {
    @Shadow(remap = false)
    @Final
    private int stopDistance;

    @Inject(method = "Lcom/github/tartaricacid/touhoulittlemaid/entity/ai/brain/task/MaidFollowOwnerTask;checkExtraStartConditions(Lnet/minecraft/server/level/ServerLevel;Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;)Z", at = @At("HEAD"),remap = false, cancellable = true)
    public void checkExtraStartConditions(ServerLevel level, EntityMaid maid, CallbackInfoReturnable<Boolean> cir) {
        if (maid.getTask().getUid().equals(MaidLocateTask.UID)) {
            LivingEntity owner = maid.getOwner();
            if (owner != null && maid.distanceTo(owner) < 6) {
                cir.setReturnValue(false);
            }
        }
    }

    @ModifyVariable(method = "start(Lnet/minecraft/server/level/ServerLevel;Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;J)V", at = @At(value = "STORE"),remap = false)
    public int start(int startDistance, @Local(argsOnly = true) EntityMaid maid) {
        if (maid.getTask().getUid().equals(MaidLocateTask.UID)) {
            return 10;
        }
        return startDistance;
    }

    @ModifyArg(method = "start(Lnet/minecraft/server/level/ServerLevel;Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/BehaviorUtils;setWalkAndLookTargetMemories(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;FI)V"))
    public int maid_useful_task$stop_distance(int stopDistance, @Local(argsOnly = true) EntityMaid maid) {
        if (maid.getTask().getUid().equals(MaidLocateTask.UID)) {
            return 10;
        }
        return stopDistance;
    }
}
