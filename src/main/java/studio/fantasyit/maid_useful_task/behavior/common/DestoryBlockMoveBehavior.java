package studio.fantasyit.maid_useful_task.behavior.common;

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
import studio.fantasyit.maid_useful_task.util.PosUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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
            List<BlockPos> besides = task.getTryDestroyBlockListBesidesStart(targetPos, target, maid);
            if (besides != null && !besides.isEmpty()) {
                // 起点视线路径与相连方块的视线路径存在大量重叠，合并去重
                LinkedHashSet<BlockPos> merged = new LinkedHashSet<>(blockPosSet);
                merged.addAll(besides);
                blockPosSet = new ArrayList<>(merged);
            }
            MemoryUtil.setDestroyTargetMemory(maid, blockPosSet);
            if (Conditions.isCurrent(maid, CurrentWork.IDLE))
                MemoryUtil.setCurrent(maid, CurrentWork.DESTROY);
        }
    }

    @Override
    protected boolean shouldMoveTo(@NotNull ServerLevel serverLevel, @NotNull EntityMaid entityMaid, @NotNull BlockPos blockPos) {
        if (!task.shouldDestroyBlock(entityMaid, blockPos.immutable())) return false;
        targetPos = blockPos.immutable();
        BlockPos startPos = entityMaid.blockPosition();
        if (blockPos instanceof BlockPos.MutableBlockPos mb) {
            int reachDistance = task.reachDistance();
            int reachDistanceSq = reachDistance * reachDistance;
            // 复用同一个可变坐标，避免每轮扫描产生上千个临时 BlockPos
            BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
            for (int dx = 0; dx < reachDistance; dx = dx <= 0 ? 1 - dx : -dx) {
                for (int dy = 0; dy < reachDistance; dy = dy <= 0 ? 1 - dy : -dy) {
                    for (int dz = 0; dz < reachDistance; dz = dz <= 0 ? 1 - dz : -dz) {
                        BlockPos pos = probe.set(mb.getX() + dx, mb.getY() + dy, mb.getZ() + dz);
                        // 判断顺序：先做纯算术的廉价过滤，再做需要读取方块状态的检查，
                        // 最后才做射线检测与寻路可达判断
                        if (pos.distSqr(targetPos) > reachDistanceSq) continue;
                        if (Math.abs(startPos.getY() - pos.getY()) >= reachDistance) continue;
                        if (Math.abs(startPos.getX() - pos.getX()) >= reachDistance) continue;
                        if (Math.abs(startPos.getZ() - pos.getZ()) >= reachDistance) continue;
                        if (!Conditions.isGlobalValidTarget(entityMaid, pos, targetPos)) continue;
                        if (!PosUtils.isSafePos(serverLevel, pos)) continue;
                        if (pos.equals(entityMaid.blockPosition()) || (entityMaid.isWithinRestriction(pos) && pathfindingBFS.canPathReach(pos))) {
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
            if (maid.hasRestriction())
                this.pathfindingBFS = new MaidPathFindingBFS(maid.getNavigation().getNodeEvaluator(), worldIn, maid, (int) maid.getRestrictRadius() + 1, task.reachDistance() + 2);
            else
                this.pathfindingBFS = new MaidPathFindingBFS(maid.getNavigation().getNodeEvaluator(), worldIn, maid, task.reachDistance() + 1, task.reachDistance() + 2);
        return this.pathfindingBFS;
    }

    @Override
    protected void clearCurrentArrivalMap(MaidPathFindingBFS pathFinding) {
        super.clearCurrentArrivalMap(pathFinding);
        this.pathfindingBFS = null;
    }
}