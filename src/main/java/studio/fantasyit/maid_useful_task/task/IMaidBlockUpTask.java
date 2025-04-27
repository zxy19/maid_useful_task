package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidPathFindingBFS;
import com.github.tartaricacid.touhoulittlemaid.util.CenterOffsetBlockPosSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import oshi.util.tuples.Pair;
import studio.fantasyit.maid_useful_task.util.MaidUtils;
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
                    if (isFindingBlock(maid, targetPos, startPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    default Pair<BlockPos, BlockPos> findTargetPosBlockUp(EntityMaid maid, BlockPos center, int maxUp) {
        ServerLevel level = (ServerLevel) maid.level();
        int maxHeight = verticalOffset() + verticalDistance();
        CenterOffsetBlockPosSet notAvailable = new CenterOffsetBlockPosSet(scanRange(maid), scanRange(maid) + maxHeight / 2 + 1, scanRange(maid), center.getX(), center.getY() + maxHeight / 2, center.getZ());
        MaidPathFindingBFS pathFindingBFS = new MaidPathFindingBFS(maid.getNavigation().getNodeEvaluator(), level, maid, 7, scanRange(maid));
        for (int dx = 0; dx < scanRange(maid); dx = dx <= 0 ? 1 - dx : -dx) {
            for (int dz = 0; dz < scanRange(maid); dz = dz <= 0 ? 1 - dz : -dz) {
                //计算地面的位置
                BlockPos.MutableBlockPos ground = center.offset(dx, 0, dz).mutable();
                while (level.getBlockState(ground).canBeReplaced()) ground.move(0, -1, 0);
                while (!level.getBlockState(ground).canBeReplaced()) ground.move(0, 1, 0);
                if (notAvailable.isVis(ground)) continue;
                //地面基本判断
                if (!PosUtils.isFourSideAir(level, ground.immutable())) continue;
                if (!pathFindingBFS.canPathReach(ground)) continue;
                boolean valid = true;
                for (int dy = 0; dy < verticalOffset(); dy++) {
                    BlockPos targetPos = ground.above(dy);
                    if (!level.getBlockState(targetPos).canBeReplaced()) {
                        valid = false;
                        break;
                    }
                }
                for (int dy = verticalOffset(); dy < verticalOffset() + 2; dy++) {
                    BlockPos targetPos = ground.above(dy);
                    if (!level.getBlockState(targetPos).isAir()) {
                        valid = false;
                        break;
                    }
                }
                if (!valid) {
                    notAvailable.markVis(ground.immutable());
                    continue;
                }
                //竖直方向触及
                for (int sdx = 0; sdx < 2; sdx = sdx <= 0 ? 1 - sdx : -sdx)
                    for (int sdz = 0; sdz < 2; sdz = sdz <= 0 ? 1 - sdz : -sdz) {
                        int touchLimit = touchLimit() + 1;
                        boolean continuous = true;
                        BlockPos standPos = ground.above(verticalOffset()).offset(sdx, 0, sdz);
                        if (standPos.getY() - ground.getY() > maxUp)
                            continue;
                        for (int dy = verticalOffset(); dy < verticalDistance() + verticalOffset(); dy++) {
                            BlockPos targetPos = ground.offset(sdx, dy, sdz);
                            if (targetPos.distSqr(standPos) > touchLimit * touchLimit) break;
                            if (maid.hasRestriction() && !maid.isWithinRestriction(standPos)) break;
                            if (isFindingBlock(maid, targetPos, standPos)) {
                                return new Pair<>(ground.immutable(), standPos);
                            }
                            //头顶一格是不是空气，不是：不连续空间（不能继续垫了，那么需要计算触及范围
                            if (continuous) {
                                if (!level.getBlockState(standPos.above().above()).isAir()) {
                                    continuous = false;
                                } else if (maid.hasRestriction() && !maid.isWithinRestriction(standPos.above())) {
                                    continuous = false;
                                }
                            }

                            if (!continuous) {
                                touchLimit--;
                            } else {
                                standPos = standPos.above();
                                if (standPos.getY() - ground.getY() > maxUp)
                                    break;
                            }
                            if (touchLimit <= 0)
                                break;
                        }
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
                count += stack.getCount();
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

    default int scanRange(EntityMaid maid) {
        return maid.hasRestriction() ? (int) maid.getRestrictRadius() : 15;
    }

    default int touchLimit() {
        return 7;
    }

    default boolean tryPlaceBlockUp(EntityMaid maid, BlockPos targetPos) {
        return MaidUtils.placeBlock(maid, targetPos);
    }

    default boolean tryDestroyBlockUp(EntityMaid maid, BlockPos targetPos) {
        return MaidUtils.destroyBlock(maid, targetPos);
    }
}
