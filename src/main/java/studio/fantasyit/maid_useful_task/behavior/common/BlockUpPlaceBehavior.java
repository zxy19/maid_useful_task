package studio.fantasyit.maid_useful_task.behavior.common;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import studio.fantasyit.maid_useful_task.memory.BlockUpContext;
import studio.fantasyit.maid_useful_task.memory.CurrentWork;
import studio.fantasyit.maid_useful_task.task.IMaidBlockUpTask;
import studio.fantasyit.maid_useful_task.util.Conditions;
import studio.fantasyit.maid_useful_task.util.MaidUtils;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.Map;

public class BlockUpPlaceBehavior extends Behavior<EntityMaid> {
    private BlockUpContext context;
    private IMaidBlockUpTask task;
    private int tickCount;


    public BlockUpPlaceBehavior(Map<MemoryModuleType<?>, MemoryStatus> p_22528_) {
        super(p_22528_);
    }

    public BlockUpPlaceBehavior() {
        super(Map.of(), 200);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_22538_, EntityMaid p_22539_) {
        if (!Conditions.isCurrent(p_22539_, CurrentWork.BLOCKUP_UP)) return false;
        if (!MemoryUtil.getBlockUpContext(p_22539_).hasTarget()) return false;
        if (MemoryUtil.getBlockUpContext(p_22539_).getStatus() != BlockUpContext.STATUS.UP) return false;
        return Conditions.hasReachedValidTargetOrReset(p_22539_, 0.8f);
    }

    @Override
    protected boolean canStillUse(ServerLevel p_22545_, EntityMaid maid, long p_22547_) {
        if (MemoryUtil.getBlockUpContext(maid).getStatus() != BlockUpContext.STATUS.UP) return false;
        if (maid.onGround() && !p_22545_.getBlockState(maid.blockPosition().above().above()).isAir()) return false;
        return !(maid.blockPosition().equals(context.getTargetPos()) && maid.onGround());
    }


    @Override
    protected void start(ServerLevel p_22540_, EntityMaid maid, long p_22542_) {
        context = MemoryUtil.getBlockUpContext(maid);
        task = (IMaidBlockUpTask) maid.getTask();
        tickCount = 0;
    }

    protected boolean alignOrTryMove(ServerLevel level, EntityMaid maid) {
        AABB boundingBox = maid.getBoundingBox();
        BlockPos startPos = context.getStartPos();
        Vec3 move = maid.getDeltaMovement();
        if (boundingBox.maxX <= startPos.getX() + 1 && boundingBox.maxZ <= startPos.getZ() + 1
                && boundingBox.minX >= startPos.getX() && boundingBox.minZ >= startPos.getZ()
        ) {
            maid.setDeltaMovement(0, move.y, 0);
            return true;
        }

        Vec3 dv = startPos.getCenter().subtract(boundingBox.getCenter()).normalize().scale(0.02);
        maid.setDeltaMovement(dv.x, move.y, dv.z);
        return false;
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long p_22553_) {
        if (!alignOrTryMove(level, maid)) return;
        tickCount += 1;
        if (!maid.onGround()) {
            task.swapValidItemToHand(maid);
            BlockPos pos = maid.blockPosition();
            BlockPos below = pos.below();
            if (context.isOnLine(pos))
                if (below.equals(context.getTargetPos()))
                    return;
            if (level.getBlockState(below).canBeReplaced() && level.getBlockState(pos).canBeReplaced()) {
                maid.swing(InteractionHand.MAIN_HAND);
                MaidUtils.placeBlock(maid, below);
            }
        } else {
            maid.getJumpControl().jump();
        }
    }

    @Override
    protected boolean timedOut(long p_22537_) {
        return tickCount > 240;
    }

    @Override
    protected void stop(ServerLevel p_22548_, EntityMaid maid, long p_22550_) {
        super.stop(p_22548_, maid, p_22550_);
        context.setStatus(BlockUpContext.STATUS.IDLE);
        if (context.hasTarget()) {
            if (!maid.blockPosition().equals(context.getTargetPos())) {
                BlockPos startPos = context.getStartPos();
                BlockPos blockPos = maid.blockPosition();
                context.setStartTarget(new BlockPos(blockPos.getX(), startPos.getY(), blockPos.getZ()), blockPos);
            }

            MemoryUtil.setCurrent(maid, CurrentWork.BLOCKUP_DESTROY);
        }
        MemoryUtil.clearTarget(maid);
    }
}
