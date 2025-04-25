package studio.fantasyit.maid_useful_task.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class Conditions {
    public static boolean hasReachedValidTargetOrReset(EntityMaid maid){
        return hasReachedValidTargetOrReset(maid, 2);
    }
    public static boolean hasReachedValidTargetOrReset(EntityMaid maid, float closeEnough) {
        Brain<EntityMaid> brain = maid.getBrain();
        return brain.getMemory(InitEntities.TARGET_POS.get()).map(targetPos -> {
            Vec3 targetV3d = targetPos.currentPosition();
            if (maid.distanceToSqr(targetV3d) > Math.pow(closeEnough, 2)) {
                Optional<WalkTarget> walkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET);
                if (walkTarget.isEmpty() || !walkTarget.get().getTarget().currentPosition().equals(targetV3d)) {
                    brain.eraseMemory(InitEntities.TARGET_POS.get());
                }
                return false;
            }
            return true;
        }).orElse(false);
    }

    public static boolean stopAndCheckStopped(EntityMaid maid) {
        if (!maid.getNavigation().isDone()) {
            maid.getNavigation().stop();
            return false;
        }
        return maid.getDeltaMovement().length() < 0.2;
    }
}
