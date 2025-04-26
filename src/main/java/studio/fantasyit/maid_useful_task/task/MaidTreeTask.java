package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.behavior.*;
import studio.fantasyit.maid_useful_task.util.WrappedMaidFakePlayer;

import java.util.ArrayList;
import java.util.List;

public class MaidTreeTask implements IMaidTask, IMaidBlockPlaceTask, IMaidBlockDestroyTask, IMaidBlockUpTask {
    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(MaidUsefulTask.MODID, "maid_tree");
    }

    @Override
    public ItemStack getIcon() {
        return Items.OAK_SAPLING.getDefaultInstance();
    }

    @Override
    public boolean enableLookAndRandomWalk(EntityMaid maid) {
        return true;
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound(EntityMaid entityMaid) {
        return null;
    }

    @Override
    public boolean shouldDestroyBlock(EntityMaid maid, BlockPos pos) {
        BlockState blockState = maid.level().getBlockState(pos);
        return blockState.is(BlockTags.LOGS) && isValidNatureTree(maid, pos);
    }

    @Override
    public boolean mayDestroy(EntityMaid maid, BlockPos pos) {
        BlockState blockState = maid.level().getBlockState(pos);
        if (blockState.is(BlockTags.LEAVES)) {
            return true;
        }
        return blockState.is(BlockTags.LOGS);
    }

    @Override
    public boolean shouldPlaceItemStack(EntityMaid maid, ItemStack itemStack) {
        return itemStack.is(ItemTags.SAPLINGS);
    }

    @Override
    public boolean shouldPlacePos(EntityMaid maid, ItemStack itemStack, BlockPos pos) {
        ServerLevel level = (ServerLevel) maid.level();
        if (!level.getBlockState(pos.below()).is(BlockTags.DIRT)) return false;
        if (!level.getBlockState(pos).canBeReplaced()) return false;
        final int[] dv = {0, 1, -1, 2, -2};
        for (int dx : dv) {
            for (int dy = 0; dy < 4; dy++) {
                for (int dz : dv) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    if (!level.getBlockState(pos.offset(dx, dy, dz)).canBeReplaced())
                        return false;
                }
            }
        }
        return true;
    }

    @Override
    public void tryTakeOutTool(EntityMaid maid) {
        CombinedInvWrapper inv = maid.getAvailableInv(true);
        for (int i = 0; i < inv.getSlots(); i++) {
            if (inv.getStackInSlot(i).is(ItemTags.AXES)) {
                @NotNull ItemStack tmp = inv.getStackInSlot(i);
                inv.setStackInSlot(i, maid.getMainHandItem());
                maid.setItemInHand(InteractionHand.MAIN_HAND, tmp);
                return;
            }
        }
    }

    public void swapShearsOrNone(EntityMaid maid) {
        CombinedInvWrapper inv = maid.getAvailableInv(true);
        int target = -1;
        for (int i = 0; i < inv.getSlots(); i++) {
            if (inv.getStackInSlot(i).is(Items.SHEARS)) {
                target = i;
                break;
            }
            if (!inv.getStackInSlot(i).isDamageableItem()) {
                target = i;
            }
        }
        if (target != -1) {
            @NotNull ItemStack tmp = inv.getStackInSlot(target);
            inv.setStackInSlot(target, maid.getMainHandItem());
            maid.setItemInHand(InteractionHand.MAIN_HAND, tmp);
        }
    }

    @Override
    public void tryTakeOutToolForTarget(EntityMaid maid, BlockPos pos) {
        if (maid.level().getBlockState(pos).is(BlockTags.LEAVES)) {
            swapShearsOrNone(maid);
        } else {
            tryTakeOutTool(maid);
        }
    }

    @Override
    public boolean availableToGetDrop(EntityMaid maid, WrappedMaidFakePlayer fakePlayer, BlockPos pos, BlockState targetBlockState) {
        if (targetBlockState.is(BlockTags.LEAVES))
            return true;
        return IMaidBlockDestroyTask.super.availableToGetDrop(maid, fakePlayer, pos, targetBlockState);
    }

    protected boolean isValidNatureTree(EntityMaid maid, BlockPos startPos) {
        final int[] dv = {0, 1, -1};
        for (int dx : dv) {
            for (int dz : dv) {
                for (int dy = 0; dy < 6; dy++) {
                    BlockState blockState = maid.level().getBlockState(startPos.offset(dx, dy, dz));
                    if (blockState.is(BlockTags.LEAVES) && !blockState.getValue(LeavesBlock.PERSISTENT)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid entityMaid) {
        ArrayList<Pair<Integer, BehaviorControl<? super EntityMaid>>> list = new ArrayList<>();
        list.add(Pair.of(6, new LoopWithTokenBehavior()));

        list.add(Pair.of(5, new DestoryBlockBehavior()));
        list.add(Pair.of(4, new DestoryBlockMoveBehavior()));
        list.add(Pair.of(3, new PlaceBlockBehavior()));
        list.add(Pair.of(2, new PlaceBlockMoveBehavior()));

        list.add(Pair.of(1, new BlockUpScheduleBehavior()));
        list.add(Pair.of(0, new BlockUpPlaceBehavior()));
        list.add(Pair.of(0, new BlockUpDestroyBehavior()));

        return list;
    }

    @Override
    public boolean isValidItemStack(EntityMaid maid, ItemStack stack) {
        return stack.is(ItemTags.LOGS);
    }

    @Override
    public boolean isDestroyTool(EntityMaid maid, ItemStack stack) {
        return stack.is(ItemTags.AXES);
    }

    @Override
    public boolean isFindingBlock(EntityMaid maid, BlockPos target, BlockPos standPos) {
        if (target.distSqr(standPos) > touchLimit() * touchLimit())
            return false;
        return maid.level().getBlockState(target).is(BlockTags.LOGS) && isValidNatureTree(maid, target);
    }
}
