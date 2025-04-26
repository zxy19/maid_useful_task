package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidPathFindingBFS;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import oshi.util.tuples.Pair;
import studio.fantasyit.maid_useful_task.util.PosUtils;

public interface IMaidBlockUpTask {
    default boolean isFindingBlock(EntityMaid maid, BlockPos target, BlockPos standPos) {
        if (target.distSqr(standPos) > touchLimit() * touchLimit())
            return false;
        IMaidTask task = maid.getTask();
        if (task instanceof IMaidBlockDestroyTask destroyTask) {
            return destroyTask.shouldDestroyBlock(maid, target);
        }
        return false;
    }

    default boolean stillValid(EntityMaid maid, BlockPos startPos) {
        for (int dx = 0; dx < touchLimit(); dx = dx <= 0 ? 1 - dx : -dx) {
            for (int dz = 0; dz < touchLimit(); dz = dz <= 0 ? 1 - dz : -dz) {
                for (int dy = 0; dy < verticalDistance(); dy++) {
                    BlockPos targetPos = startPos.offset(dx, dy, dz);
                    if (maid.hasRestriction() && !maid.isWithinRestriction(targetPos)) break;
                    if (isFindingBlock(maid, targetPos, startPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    default Pair<BlockPos, BlockPos> findTargetPosBlockUp(EntityMaid maid, BlockPos center) {
        ServerLevel level = (ServerLevel) maid.level();
        MaidPathFindingBFS pathFindingBFS = new MaidPathFindingBFS(maid.getNavigation().getNodeEvaluator(), level, maid);
        for (int dx = 0; dx < scanRange(); dx = dx <= 0 ? 1 - dx : -dx) {
            for (int dz = 0; dz < scanRange(); dz = dz <= 0 ? 1 - dz : -dz) {
                BlockPos.MutableBlockPos mb = center.offset(dx, 0, dz).mutable();
                while (level.getBlockState(mb).canBeReplaced()) mb.move(0, -1, 0);
                while (!level.getBlockState(mb).canBeReplaced()) mb.move(0, 1, 0);
                if (!PosUtils.isFourSideAir(level, mb)) continue;
                if (!pathFindingBFS.canPathReach(mb)) continue;
                boolean valid = true;
                for (int dy = 0; dy < verticalOffset(); dy++) {
                    BlockPos targetPos = center.offset(dx, dy, dz);
                    if (!level.getBlockState(targetPos).canBeReplaced()) {
                        valid = false;
                        break;
                    }
                }
                for (int dy = verticalDistance(); dy < verticalDistance() + 2; dy++) {
                    BlockPos targetPos = center.offset(dx, dy, dz);
                    if (!level.getBlockState(targetPos).isAir()) {
                        valid = false;
                        break;
                    }
                }
                if (!valid)
                    continue;
                int touchLimit = touchLimit() + 1;
                boolean continuous = true;
                BlockPos standPos = center.offset(dx, verticalOffset(), dz);
                for (int dy = verticalOffset(); dy < verticalDistance() + verticalOffset(); dy++) {
                    BlockPos targetPos = center.offset(dx, dy, dz);
                    if (maid.hasRestriction() && !maid.isWithinRestriction(targetPos)) break;
                    if (isFindingBlock(maid, targetPos, standPos)) {
                        return new Pair<>(mb, standPos);
                    }
                    //头顶一格是不是空气，不是：不连续空间（不能继续垫了，那么需要计算触及范围
                    if (!level.getBlockState(standPos.above().above()).isAir()) {
                        continuous = false;
                    }

                    if (!continuous) {
                        touchLimit--;
                    } else {
                        standPos = standPos.above();
                    }
                    if (touchLimit <= 0)
                        break;
                }
            }
        }
        return null;
    }

    default int countMaxUsableBlockItems(EntityMaid maid) {
        CombinedInvWrapper inv = maid.getAvailableInv(true);
        int count = 0;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (isValidItemStack(maid, stack)) {
                count++;
            }
        }
        return count;
    }

    default boolean swapValidItemToHand(EntityMaid maid) {
        CombinedInvWrapper inv = maid.getAvailableInv(true);
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (isValidItemStack(maid, stack)) {
                inv.setStackInSlot(i, maid.getMainHandItem());
                maid.setItemInHand(InteractionHand.MAIN_HAND, stack);
                return true;
            }
        }
        return false;
    }

    default boolean swapValidToolToHand(EntityMaid maid) {
        CombinedInvWrapper inv = maid.getAvailableInv(true);
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (isDestroyTool(maid, stack)) {
                inv.setStackInSlot(i, maid.getMainHandItem());
                maid.setItemInHand(InteractionHand.MAIN_HAND, stack);
                return true;
            }
        }
        return false;
    }

    boolean isValidItemStack(EntityMaid maid, ItemStack stack);

    boolean isDestroyTool(EntityMaid maid, ItemStack stack);

    default int verticalOffset() {
        return 2;
    }

    default int verticalDistance() {
        return 15;
    }

    default int scanRange() {
        return 10;
    }

    default int touchLimit() {
        return 7;
    }
}
