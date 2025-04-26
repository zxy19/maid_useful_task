package studio.fantasyit.maid_useful_task.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import studio.fantasyit.maid_useful_task.registry.MemoryModuleRegistry;
import studio.fantasyit.maid_useful_task.task.IMaidBlockPlaceTask;
import studio.fantasyit.maid_useful_task.util.Conditions;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.Map;

public class PlaceBlockBehavior extends Behavior<EntityMaid> {
    private BlockPos target;

    public PlaceBlockBehavior() {
        super(Map.of(MemoryModuleRegistry.PLACE_TARGET.get(), MemoryStatus.VALUE_PRESENT,
                InitEntities.TARGET_POS.get(), MemoryStatus.VALUE_PRESENT
        ));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_22538_, EntityMaid p_22539_) {
        return Conditions.hasReachedValidTargetOrReset(p_22539_);
    }

    @Override
    protected boolean canStillUse(ServerLevel p_22545_, EntityMaid p_22546_, long p_22547_) {
        return target != null;
    }

    @Override
    protected void start(ServerLevel p_22540_, EntityMaid maid, long p_22542_) {
        super.start(p_22540_, maid, p_22542_);
        target = MemoryUtil.getPlaceTarget(maid);
        if (target == null)
            return;
        MemoryUtil.setLookAt(maid, target);
        maid.getNavigation().stop();
    }

    @Override
    protected void tick(ServerLevel p_22551_, EntityMaid maid, long p_22553_) {
        if (!Conditions.stopAndCheckStopped(maid)) return;
        IMaidBlockPlaceTask task = (IMaidBlockPlaceTask) maid.getTask();
        if (task.shouldPlacePos(maid, maid.getMainHandItem(), target)) {
            maid.swing(InteractionHand.MAIN_HAND);
            task.tryPlaceBlock(maid, target);
        }
        target = null;
    }

    @Override
    protected void stop(ServerLevel p_22548_, EntityMaid p_22549_, long p_22550_) {
        super.stop(p_22548_, p_22549_, p_22550_);
        MemoryUtil.clearPlaceTarget(p_22549_);
        MemoryUtil.clearTarget(p_22549_);
    }
}
