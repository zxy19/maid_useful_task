package studio.fantasyit.maid_useful_task.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidCheckRateTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import oshi.util.tuples.Pair;
import studio.fantasyit.maid_useful_task.memory.BlockUpContext;
import studio.fantasyit.maid_useful_task.memory.TaskRateLimitToken;
import studio.fantasyit.maid_useful_task.task.IMaidBlockUpTask;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.Map;

public class BlockUpScheduleBehavior extends Behavior<EntityMaid> {
    public BlockUpScheduleBehavior() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, InitEntities.TARGET_POS.get(), MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_22538_, EntityMaid p_22539_) {
        if (MemoryUtil.getRateLimitToken(p_22539_).isFor(TaskRateLimitToken.Level.L3)) {
            return false;
        }
        return super.checkExtraStartConditions(p_22538_, p_22539_);
    }

    @Override
    protected void start(ServerLevel p_22540_, EntityMaid maid, long p_22542_) {
        BlockUpContext context = MemoryUtil.getBlockUpContext(maid);
        IMaidBlockUpTask task = (IMaidBlockUpTask) maid.getTask();
        if (context.hasTarget()) {
            if (context.getStatus() != BlockUpContext.STATUS.IDLE && MemoryUtil.getTargetPos(maid) == null) {
                context.clearStartTarget();
            } else if (!context.isOnLine(maid.blockPosition()) || context.getStartPos().equals(context.getTargetPos())) {
                context.clearStartTarget();
            } else if (context.getStatus() == BlockUpContext.STATUS.IDLE && !context.isTarget(maid.blockPosition()) && context.isOnLine(maid.blockPosition())) {
                context.setStartTarget(context.getStartPos(), maid.blockPosition());
            } else if (context.getStatus() == BlockUpContext.STATUS.IDLE && !task.stillValid(maid, maid.blockPosition())) {
                maid.getBrain().setMemory(InitEntities.TARGET_POS.get(), new BlockPosTracker(context.getTargetPos()));
                context.setStatus(BlockUpContext.STATUS.DOWN);
            }
        } else {
            Pair<BlockPos, BlockPos> targetPosBlockUp = task.findTargetPosBlockUp(maid, maid.blockPosition());
            if (targetPosBlockUp != null) {
                context.setStartTarget(targetPosBlockUp.getA(), targetPosBlockUp.getB());
                maid.getBrain().setMemory(InitEntities.TARGET_POS.get(), new BlockPosTracker(targetPosBlockUp.getA()));
                BehaviorUtils.setWalkAndLookTargetMemories(maid, targetPosBlockUp.getA(), 0.5f, 0);
                context.setStatus(BlockUpContext.STATUS.UP);
            }
        }
    }
}
