package studio.fantasyit.maid_useful_task.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.state.BlockState;
import studio.fantasyit.maid_useful_task.memory.BlockUpContext;
import studio.fantasyit.maid_useful_task.memory.CurrentWork;
import studio.fantasyit.maid_useful_task.task.IMaidBlockUpTask;
import studio.fantasyit.maid_useful_task.util.Conditions;
import studio.fantasyit.maid_useful_task.util.MaidUtils;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;
import studio.fantasyit.maid_useful_task.util.WrappedMaidFakePlayer;

import java.util.Map;

public class BlockUpDestroyBehavior extends Behavior<EntityMaid> {
    private BlockUpContext context;
    private IMaidBlockUpTask task;

    WrappedMaidFakePlayer fakePlayer;

    public BlockUpDestroyBehavior(Map<MemoryModuleType<?>, MemoryStatus> p_22528_) {
        super(p_22528_);
    }

    public BlockUpDestroyBehavior() {
        super(Map.of(),500);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_22538_, EntityMaid p_22539_) {
        if(!Conditions.isCurrent(p_22539_, CurrentWork.BLOCKUP_DOWN)) return false;
        if (!MemoryUtil.getBlockUpContext(p_22539_).hasTarget()) return false;
        if (MemoryUtil.getBlockUpContext(p_22539_).getStatus() != BlockUpContext.STATUS.DOWN) return false;
        return Conditions.hasReachedValidTargetOrReset(p_22539_, 0.8f);
    }

    @Override
    protected boolean canStillUse(ServerLevel p_22545_, EntityMaid maid, long p_22547_) {
        if (MemoryUtil.getBlockUpContext(maid).getStatus() != BlockUpContext.STATUS.DOWN) return false;
        return MemoryUtil.getBlockUpContext(maid).isOnLine(maid.blockPosition()) && !maid.blockPosition().equals(context.getStartPos());
    }


    @Override
    protected void start(ServerLevel p_22540_, EntityMaid maid, long p_22542_) {
        context = MemoryUtil.getBlockUpContext(maid);
        task = (IMaidBlockUpTask) maid.getTask();
        fakePlayer = WrappedMaidFakePlayer.get(maid);
    }

    float progress = 0;

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long p_22553_) {
        if (!maid.onGround()) {
            progress = 0;
        } else {
            task.swapValidToolToHand(maid);
            BlockPos targetPos = maid.blockPosition().below();
            maid.swing(InteractionHand.MAIN_HAND);
            MemoryUtil.setLookAt(maid, targetPos);
            float speed = MaidUtils.getDestroyProgressDelta(maid, targetPos);
            progress += speed;
            if (progress >= 1f) {
                MaidUtils.destroyBlock(maid, targetPos);
                progress = 0;
            }
        }
    }
    @Override
    protected void stop(ServerLevel p_22548_, EntityMaid maid, long p_22550_) {
        super.stop(p_22548_, maid, p_22550_);
        context.clearStartTarget();
        MemoryUtil.clearTarget(maid);
        MemoryUtil.setCurrent(maid, CurrentWork.IDLE);
    }
}
