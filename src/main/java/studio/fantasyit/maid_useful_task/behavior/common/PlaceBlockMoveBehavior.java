package studio.fantasyit.maid_useful_task.behavior.common;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidPathFindingBFS;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.memory.CurrentWork;
import studio.fantasyit.maid_useful_task.task.IMaidBlockPlaceTask;
import studio.fantasyit.maid_useful_task.util.Conditions;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

public class PlaceBlockMoveBehavior extends MaidCenterMoveToBlockTask {
    private IMaidBlockPlaceTask task;
    private MaidPathFindingBFS pathfindingBFS;
    private BlockPos targetPos;
    private ItemStack targetItem;

    public PlaceBlockMoveBehavior() {
        super(0.5f, 4);
    }


    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_22538_, EntityMaid maid) {
        if (!Conditions.isCurrent(maid, CurrentWork.IDLE)) return false;
        return super.checkExtraStartConditions(p_22538_, maid);
    }

    @Override
    protected void start(ServerLevel p_22540_, EntityMaid maid, long p_22542_) {
        super.start(p_22540_, maid, p_22542_);
        if (maid.hasRestriction())
            this.setSearchRange((int) maid.getRestrictRadius());
        task = (IMaidBlockPlaceTask) maid.getTask();
        CombinedInvWrapper inv = maid.getAvailableInv(true);
        List<ItemStack> markedVis = new ArrayList<>();
        for (int i = 0; i < inv.getSlots(); i++) {
            if (!task.shouldPlaceItemStack(maid, inv.getStackInSlot(i))) continue;
            int finalI = i;
            if (markedVis.stream().anyMatch(t -> ItemStack.isSameItem(t, inv.getStackInSlot(finalI)))) continue;
            targetItem = inv.getStackInSlot(i);
            searchForDestination(p_22540_, maid);
            @Nullable BlockPos target = MemoryUtil.getTargetPos(maid);
            if (target != null) {
                MemoryUtil.setPlaceTarget(maid, targetPos);
                MemoryUtil.setCurrent(maid, CurrentWork.PLACE);
                inv.setStackInSlot(finalI, maid.getMainHandItem());
                maid.setItemInHand(InteractionHand.MAIN_HAND, targetItem);
                return;
            }
            markedVis.add(targetItem);
        }
    }

    @Override
    protected boolean shouldMoveTo(ServerLevel serverLevel, EntityMaid entityMaid, BlockPos blockPos) {
        if (!task.shouldPlacePos(entityMaid, targetItem, blockPos.immutable())) return false;
        if (!entityMaid.isWithinRestriction(blockPos)) return false;
        targetPos = blockPos.immutable();
        if (blockPos instanceof BlockPos.MutableBlockPos mb) {
            final int[] dv = {0, 1, -1};
            for (int dx : dv) {
                for (int dy : dv) {
                    for (int dz : dv) {
                        BlockPos pos = mb.offset(dx, dy, dz);
                        if (!Conditions.isGlobalValidTarget(entityMaid, pos, targetPos)) continue;
                        if (entityMaid.isWithinRestriction(pos) && pathfindingBFS.canPathReach(pos)) {
                            mb.set(pos);
                            return true;
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