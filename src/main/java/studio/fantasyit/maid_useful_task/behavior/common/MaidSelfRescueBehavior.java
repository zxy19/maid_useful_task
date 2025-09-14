package studio.fantasyit.maid_useful_task.behavior.common;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import studio.fantasyit.maid_useful_task.Config;
import studio.fantasyit.maid_useful_task.memory.CurrentWork;
import studio.fantasyit.maid_useful_task.task.IMaidBlockDestroyTask;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaidSelfRescueBehavior extends Behavior<EntityMaid> {
    public MaidSelfRescueBehavior() {
        super(Map.of(), 600);
    }

    boolean started = false;

    private static boolean isNotSafeAndCanTryToDestroy(ServerLevel level, EntityMaid maid, BlockPos pos, IMaidBlockDestroyTask task) {
        BlockState bs = level.getBlockState(pos);
        if (bs.isAir()) return false;
        VoxelShape collision = bs.getCollisionShape(level, pos);
        if (collision.isEmpty()) return false;
        return maid.getBoundingBox().intersects(collision.bounds().move(pos)) && bs.isSuffocating(level, pos) && task.mayDestroy(maid, pos);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        if (!Config.enableSelfRescue) return false;
        if (!(maid.getTask() instanceof IMaidBlockDestroyTask ibdt)) return false;
        if (isNotSafeAndCanTryToDestroy(level, maid, maid.blockPosition(), ibdt) || isNotSafeAndCanTryToDestroy(level, maid, maid.blockPosition().above(), ibdt))
            return true;
        return false;
    }

    @Override
    protected boolean canStillUse(ServerLevel p_22545_, EntityMaid p_22546_, long p_22547_) {
        return started && checkExtraStartConditions(p_22545_, p_22546_);
    }

    @Override
    protected void start(ServerLevel p_22540_, EntityMaid maid, long p_22542_) {
        started = false;
        if (MemoryUtil.getCurrent(maid) == CurrentWork.DESTROY) {
            //如果执行中破坏任务，则立刻停止。用以清空相关状态
            MemoryUtil.setCurrent(maid, CurrentWork.IDLE);
            return;
        }
        //等待上一个任务结束
        if (MemoryUtil.getDestroyTargetMemory(maid) != null)
            return;
        if (!(maid.getTask() instanceof IMaidBlockDestroyTask task))
            return;

        started = true;

        List<BlockPos> list = new ArrayList<>();
        if (isNotSafeAndCanTryToDestroy(p_22540_, maid, maid.blockPosition(), task)) {
            list.add(maid.blockPosition());
        }
        if (isNotSafeAndCanTryToDestroy(p_22540_, maid, maid.blockPosition().above(), task)) {
            list.add(maid.blockPosition().above());
        }
        MemoryUtil.setDestroyTargetMemory(maid, list);
        MemoryUtil.setTarget(maid, maid.blockPosition(), 0.5f);
        MemoryUtil.setCurrent(maid, CurrentWork.DESTROY);
    }

    @Override
    protected void stop(ServerLevel p_22548_, EntityMaid p_22549_, long p_22550_) {
        MemoryUtil.clearTarget(p_22549_);
        MemoryUtil.setCurrent(p_22549_, CurrentWork.IDLE);
    }
}
