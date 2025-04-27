package studio.fantasyit.maid_useful_task.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidRunOne;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import studio.fantasyit.maid_useful_task.memory.CurrentWork;
import studio.fantasyit.maid_useful_task.util.Conditions;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

@Mixin(MaidRunOne.class)
abstract public class MaidRunOneMixin {
    @Inject(method = "tryStart(Lnet/minecraft/server/level/ServerLevel;Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;J)Z", at = @At("HEAD"), cancellable = true, remap = false)
    public void runOne(ServerLevel pLevel, EntityMaid maid, long pGameTime, CallbackInfoReturnable<Boolean> cir) {
        if (!Conditions.isCurrent(maid, CurrentWork.IDLE)) {
            cir.setReturnValue(false);
        }
    }
}
