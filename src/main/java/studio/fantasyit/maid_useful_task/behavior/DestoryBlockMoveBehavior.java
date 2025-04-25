package studio.fantasyit.maid_useful_task.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidMoveToBlockTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidPathFindingBFS;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.task.IMaidBlockDestroyTask;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.List;

public class DestoryBlockMoveBehavior extends MaidMoveToBlockTask {
    private IMaidBlockDestroyTask task;
    private MaidPathFindingBFS pathfindingBFS;
    private BlockPos targetPos;
    List<BlockPos> blockPosSet;

    public DestoryBlockMoveBehavior() {
        super(0.5f, 4);
    }

    @Override
    protected void start(@NotNull ServerLevel p_22540_, @NotNull EntityMaid maid, long p_22542_) {
        super.start(p_22540_, maid, p_22542_);
        task = (IMaidBlockDestroyTask) maid.getTask();
        task.tryTakeOutTool(maid);
        searchForDestination(p_22540_, maid);
        @Nullable BlockPos target = MemoryUtil.getTargetPos(maid);
        if (target != null && blockPosSet != null) {
            blockPosSet.addAll(task.getTryDestroyBlockListBesidesStart(targetPos, target, maid));
            MemoryUtil.setDestroyTargetMemory(maid, blockPosSet);
        }
    }

    @Override
    protected boolean shouldMoveTo(@NotNull ServerLevel serverLevel, @NotNull EntityMaid entityMaid, @NotNull BlockPos blockPos) {
        if (!task.shouldDestroyBlock(entityMaid, blockPos)) return false;
        targetPos = blockPos.immutable();
        if (blockPos instanceof BlockPos.MutableBlockPos mb) {
            for (int dx = 0; dx < task.reachDistance(); dx = dx <= 0 ? 1 - dx : -dx) {
                for (int dy = 0; dy < task.reachDistance(); dy = dy <= 0 ? 1 - dy : -dy) {
                    for (int dz = 0; dz < task.reachDistance(); dz = dz <= 0 ? 1 - dz : -dz) {
                        BlockPos pos = mb.offset(dx, dy, dz);
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
        this.pathfindingBFS = super.getOrCreateArrivalMap(worldIn, maid);
        return this.pathfindingBFS;
    }
}
