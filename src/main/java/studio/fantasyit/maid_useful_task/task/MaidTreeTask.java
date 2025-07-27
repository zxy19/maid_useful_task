package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.Config;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.behavior.common.*;
import studio.fantasyit.maid_useful_task.data.MaidLoggingConfig;
import studio.fantasyit.maid_useful_task.memory.BlockValidationMemory;
import studio.fantasyit.maid_useful_task.menu.MaidLoggingConfigGui;
import studio.fantasyit.maid_useful_task.util.MaidUtils;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;
import studio.fantasyit.maid_useful_task.util.WrappedMaidFakePlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MaidTreeTask implements IMaidTask, IMaidBlockPlaceTask, IMaidBlockDestroyTask, IMaidBlockUpTask {
    public static final ResourceLocation UID = new ResourceLocation(MaidUsefulTask.MODID, "maid_tree");

    @Override
    public ResourceLocation getUid() {
        return UID;
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
    public boolean isEnable(EntityMaid maid) {
        return Config.enableLoggingTask;
    }

    @Override
    public MenuProvider getTaskConfigGuiProvider(EntityMaid maid) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("");
            }

            @Override
            public AbstractContainerMenu createMenu(int index, Inventory playerInventory, Player player) {
                return new MaidLoggingConfigGui.Container(index, playerInventory, maid.getId());
            }
        };
    }

    @Override
    public boolean shouldDestroyBlock(EntityMaid maid, BlockPos pos) {
        if (MemoryUtil.getBlockUpContext(maid).hasTarget()) {
            if (pos.getY() < maid.getBlockY() && pos.getX() == maid.getBlockX() && pos.getZ() == maid.getBlockZ()) {
                return false;
            }
        }
        BlockState blockState = maid.level().getBlockState(pos);
        if (blockState.is(BlockTags.LOGS)) {
            return !MaidLoggingConfig.get(maid).skipNonNature() || isValidNatureTree(maid, pos);
        }
        return false;
    }

    @Override
    public boolean mayDestroy(EntityMaid maid, BlockPos pos) {
        if (MemoryUtil.getBlockUpContext(maid).hasTarget()) {
            if (pos.getY() < maid.getBlockY() && pos.getX() == maid.getBlockX() && pos.getZ() == maid.getBlockZ()) {
                return false;
            }
        }
        BlockState blockState = maid.level().getBlockState(pos);
        if (blockState.is(BlockTags.LEAVES)) {
            return true;
        }
        return blockState.is(BlockTags.LOGS);
    }

    @Override
    public boolean shouldPlaceItemStack(EntityMaid maid, ItemStack itemStack) {
        if (!maid.getOrCreateData(MaidLoggingConfig.KEY, MaidLoggingConfig.Data.getDefault()).plant()) return false;
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
            if (inv.getStackInSlot(i).is(Items.SHEARS) || inv.getStackInSlot(i).is(ItemTags.HOES)) {
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
        return isValidNatureTree(maid, startPos, new HashSet<>(), 0);
    }

    protected boolean isValidNatureTree(EntityMaid maid, BlockPos startPos, Set<BlockPos> visited, int depth) {
        BlockValidationMemory validationMemory = MemoryUtil.getBlockValidationMemory(maid);
        if (validationMemory.hasRecord(startPos))
            return validationMemory.isValid(startPos, false);
        if (visited.contains(startPos))
            return false;
        if (depth > 100) return false;
        visited.add(startPos);
        boolean valid = false;
        final int[] dv = {0, 1, -1};
        for (int dx : dv) {
            for (int dz : dv) {
                for (int dy : dv) {
                    BlockPos offset = startPos.offset(dx, dy, dz);
                    BlockState blockState = maid.level().getBlockState(offset);
                    if (blockState.is(BlockTags.LEAVES) && blockState.hasProperty(LeavesBlock.PERSISTENT) && !blockState.getValue(LeavesBlock.PERSISTENT)) {
                        valid = true;
                    }
                    if (blockState.is(BlockTags.LOGS) && isValidNatureTree(maid, offset, visited, depth + 1)) {
                        valid = true;
                    }
                }
            }
        }
        if (valid)
            validationMemory.setValid(startPos);
        else
            validationMemory.setInvalid(startPos);
        return valid;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid entityMaid) {
        ArrayList<Pair<Integer, BehaviorControl<? super EntityMaid>>> list = new ArrayList<>();

        list.add(Pair.of(1, new DestoryBlockBehavior()));
        list.add(Pair.of(1, new DestoryBlockMoveBehavior()));

        list.add(Pair.of(2, new BlockUpScheduleBehavior()));
        list.add(Pair.of(2, new BlockUpPlaceBehavior()));
        list.add(Pair.of(2, new BlockUpDestroyBehavior()));

        list.add(Pair.of(3, new PlaceBlockBehavior()));
        list.add(Pair.of(3, new PlaceBlockMoveBehavior()));

        list.add(Pair.of(4, new UpdateValidationMemoryBehavior()));

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
        if (maid.level().getBlockState(target).is(BlockTags.LOGS)) {
            return !MaidLoggingConfig.get(maid).skipNonNature() || isValidNatureTree(maid, target);
        }
        return false;
    }

    @Override
    public boolean tryDestroyBlockUp(EntityMaid maid, BlockPos targetPos) {
        return tryDestroyBlock(maid, targetPos);
    }

    @Override
    public boolean tryPlaceBlock(EntityMaid maid, BlockPos pos) {
        if (IMaidBlockPlaceTask.super.tryPlaceBlock(maid, pos)) {
            MemoryUtil.getBlockValidationMemory(maid).setValid(pos);
            return true;
        }
        return false;
    }

    @Override
    public boolean tryDestroyBlock(EntityMaid maid, BlockPos blockPos) {
        if (MaidUtils.destroyBlock(maid, blockPos)) {
            MemoryUtil.getBlockValidationMemory(maid).remove(blockPos);
            return true;
        }
        return false;
    }

    /**
     * 此处判断当home模式未开启时，不允许上搭。
     *
     * @param maid
     * @param center
     * @param maxUp
     * @return
     */
    @Override
    public oshi.util.tuples.Pair<BlockPos, BlockPos> findTargetPosBlockUp(EntityMaid maid, BlockPos center, int maxUp) {
        if (maid.isHomeModeEnable() && MaidLoggingConfig.get(maid).blockUp() && !Config.disableLoggingBlockUp)
            return IMaidBlockUpTask.super.findTargetPosBlockUp(maid, center, maxUp);
        return null;
    }
}