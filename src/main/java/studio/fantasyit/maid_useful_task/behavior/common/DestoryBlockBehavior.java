package studio.fantasyit.maid_useful_task.behavior.common;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import studio.fantasyit.maid_useful_task.memory.BlockTargetMemory;
import studio.fantasyit.maid_useful_task.memory.CurrentWork;
import studio.fantasyit.maid_useful_task.registry.MemoryModuleRegistry;
import studio.fantasyit.maid_useful_task.task.IMaidBlockDestroyTask;
import studio.fantasyit.maid_useful_task.util.Conditions;
import studio.fantasyit.maid_useful_task.util.MaidUtils;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;
import studio.fantasyit.maid_useful_task.util.WrappedMaidFakePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DestoryBlockBehavior extends Behavior<EntityMaid> {
    private IMaidBlockDestroyTask task;

    public DestoryBlockBehavior() {
        super(Map.of(
                InitEntities.TARGET_POS.get(), MemoryStatus.VALUE_PRESENT,
                MemoryModuleRegistry.DESTROY_TARGET.get(), MemoryStatus.VALUE_PRESENT
        ));
    }

    @Override
    protected boolean checkExtraStartConditions(@NotNull ServerLevel worldIn, @NotNull EntityMaid maid) {
        if (!Conditions.isCurrent(maid, CurrentWork.DESTROY) && !Conditions.isCurrent(maid, CurrentWork.BLOCKUP_DESTROY))
            return false;
        return Conditions.hasReachedValidTargetOrReset(maid, 1);
    }


    List<BlockPos> blockPosSet = null;
    int index = 0;
    float destroyProgress = 0f;
    BlockPos targetPos;
    BlockState targetBlockState;
    WrappedMaidFakePlayer fakePlayer;

    @Override
    protected void start(ServerLevel p_22540_, @NotNull EntityMaid maid, long p_22542_) {
        super.start(p_22540_, maid, p_22542_);
        BlockTargetMemory blockTargetMemory = MemoryUtil.getDestroyTargetMemory(maid);
        if (blockTargetMemory != null) {
            blockPosSet = new ArrayList<>(blockTargetMemory.getBlockPosSet());
            blockPosSet.sort((o1, o2) -> (int) (o1.distSqr(maid.blockPosition()) - o2.distSqr(maid.blockPosition())));
        }
        index = 0;
        task = (IMaidBlockDestroyTask) maid.getTask();
        fakePlayer = WrappedMaidFakePlayer.get(maid);
        targetPos = null;
        targetBlockState = null;
        maid.getNavigation().stop();
    }

    @Override
    protected boolean canStillUse(ServerLevel p_22545_, EntityMaid p_22546_, long p_22547_) {
        return (blockPosSet != null && index < blockPosSet.size()) || targetPos != null;
    }

    @Override
    protected void tick(@NotNull ServerLevel level, @NotNull EntityMaid maid, long p_22553_) {
        if (!Conditions.stopAndCheckStopped(maid)) return;
        if (targetPos != null) {
            tickDestroyProgress(maid);
            return;
        }
        while (index < blockPosSet.size()) {
            targetPos = blockPosSet.get(index++);
            targetBlockState = level.getBlockState(targetPos);
            if (!targetBlockState.isAir()) {
                task.tryTakeOutToolForTarget(maid, targetPos);
                if (task.canDestroyBlock(maid, targetPos)) {
                    destroyProgress = 0f;
                    return;
                }
            }
            targetPos = null;
            targetBlockState = null;
        }
    }

    private void tickDestroyProgress(EntityMaid maid) {
        float speed = MaidUtils.getDestroyProgressDelta(maid, targetPos);
        MemoryUtil.setLookAt(maid, targetPos);
        if (speed != 0.0f && task.availableToGetDrop(maid, fakePlayer, targetPos, targetBlockState)) {
            maid.swing(InteractionHand.MAIN_HAND);
            destroyProgress += speed;
            if (destroyProgress >= 1f) {
                task.tryDestroyBlock(maid, targetPos);
                destroyProgress = 0f;
                targetPos = null;
                targetBlockState = null;
            }
        } else {
            targetPos = null;
            targetBlockState = null;
        }
    }

    @Override
    protected void stop(ServerLevel p_22548_, EntityMaid p_22549_, long p_22550_) {
        super.stop(p_22548_, p_22549_, p_22550_);
        MemoryUtil.clearDestroyTargetMemory(p_22549_);
        MemoryUtil.clearTarget(p_22549_);
        MemoryUtil.setCurrent(p_22549_, CurrentWork.IDLE);
    }

    @Override
    protected boolean timedOut(long p_22537_) {
        return false;
    }
}
