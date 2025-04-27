package studio.fantasyit.maid_useful_task.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidPathFindingBFS;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.memory.CurrentWork;
import studio.fantasyit.maid_useful_task.task.IMaidBlockDestroyTask;
import studio.fantasyit.maid_useful_task.util.Conditions;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.List;

public class DestoryBlockMoveBehavior extends MaidCenterMoveToBlockTask {
    private IMaidBlockDestroyTask task;
    private MaidPathFindingBFS pathfindingBFS;
    private BlockPos targetPos;
    List<BlockPos> blockPosSet;

    public DestoryBlockMoveBehavior() {
        super(0.5f, 7, 8);
    }


    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_22538_, EntityMaid maid) {
        if (!Conditions.isCurrent(maid, CurrentWork.IDLE) && !Conditions.isCurrent(maid, CurrentWork.BLOCKUP_DESTROY))
            return false;
        return super.checkExtraStartConditions(p_22538_, maid);
    }

    @Override
    protected void start(@NotNull ServerLevel p_22540_, @NotNull EntityMaid maid, long p_22542_) {
        super.start(p_22540_, maid, p_22542_);
        if (maid.hasRestriction())
            this.setSearchRange((int) maid.getRestrictRadius());
        task = (IMaidBlockDestroyTask) maid.getTask();
        task.tryTakeOutTool(maid);
        searchForDestination(p_22540_, maid);
        @Nullable BlockPos target = MemoryUtil.getTargetPos(maid);
        if (target != null && blockPosSet != null) {
            blockPosSet.addAll(task.getTryDestroyBlockListBesidesStart(targetPos, target, maid));
            MemoryUtil.setDestroyTargetMemory(maid, blockPosSet);
            if (Conditions.isCurrent(maid, CurrentWork.IDLE))
                MemoryUtil.setCurrent(maid, CurrentWork.DESTROY);
        }
    }

    @Override
    protected boolean shouldMoveTo(@NotNull ServerLevel serverLevel, @NotNull EntityMaid entityMaid, @NotNull BlockPos blockPos) {
        if (!task.shouldDestroyBlock(entityMaid, blockPos.immutable())) return false;
        targetPos = blockPos.immutable();
        if (blockPos instanceof BlockPos.MutableBlockPos mb) {
            for (int dx = 0; dx < task.reachDistance(); dx = dx <= 0 ? 1 - dx : -dx) {
                for (int dy = 0; dy < task.reachDistance(); dy = dy <= 0 ? 1 - dy : -dy) {
                    for (int dz = 0; dz < task.reachDistance(); dz = dz <= 0 ? 1 - dz : -dz) {
                        BlockPos pos = mb.offset(dx, dy, dz);
                        if (!Conditions.isGlobalValidTarget(entityMaid, pos, targetPos)) continue;
                        if (pos.distSqr(targetPos) > task.reachDistance() * task.reachDistance()) continue;
                        if (entityMaid.isWithinRestriction(pos) && pathfindingBFS.canPathReach(pos)) {
                            blockPosSet = task.toDestroyFromStanding(entityMaid, targetPos, pos);
                            if (blockPosSet != null) {
                                mb.set(pos);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        targetPos = null;
        return false;
    }


    @Override
    protected @NotNull MaidPathFindingBFS getOrCreateArrivalMap(@NotNull ServerLevel worldIn, @NotNull EntityMaid maid) {
        if (this.pathfindingBFS == null)
            if(maid.hasRestriction())
                this.pathfindingBFS = new MaidPathFindingBFS(maid.getNavigation().getNodeEvaluator(), worldIn, maid, 14, (int) maid.getRestrictRadius());
            else
                this.pathfindingBFS = new MaidPathFindingBFS(maid.getNavigation().getNodeEvaluator(), worldIn, maid, 14);
        return this.pathfindingBFS;
    }

    @Override
    protected void clearCurrentArrivalMap(MaidPathFindingBFS pathFinding) {
        super.clearCurrentArrivalMap(pathFinding);
        this.pathfindingBFS = null;
    }
}