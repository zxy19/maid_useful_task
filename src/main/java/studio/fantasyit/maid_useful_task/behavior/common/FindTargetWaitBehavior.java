package studio.fantasyit.maid_useful_task.behavior.common;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import studio.fantasyit.maid_useful_task.task.IMaidFindTargetTask;
import studio.fantasyit.maid_useful_task.util.Conditions;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

public class FindTargetWaitBehavior extends Behavior<EntityMaid> {

    public FindTargetWaitBehavior() {
        super(ImmutableMap.of(InitEntities.TARGET_POS.get(), MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, EntityMaid maid) {
        IMaidFindTargetTask task = (IMaidFindTargetTask) maid.getTask();
        if (task.findTarget(serverLevel, maid) == null) return true;
        if (maid.hasRestriction()) return false;
        LivingEntity owner = maid.getOwner();
        if (owner != null && maid.distanceTo(owner) > task.maxOutDistance()) {
            return true;
        }
        return Conditions.hasReachedValidTargetOrReset(maid, 4);
    }

    @Override
    protected void start(@NotNull ServerLevel serverLevel, EntityMaid maid, long p_22542_) {
        IMaidFindTargetTask task = (IMaidFindTargetTask) maid.getTask();
        if (task.findTarget(serverLevel, maid) == null) {
            task.clearCache(maid);
        }
        MemoryUtil.clearTarget(maid);
    }
}
