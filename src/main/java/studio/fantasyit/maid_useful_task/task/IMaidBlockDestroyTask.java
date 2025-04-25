package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.behavior.DestoryBlockBehavior;
import studio.fantasyit.maid_useful_task.behavior.DestoryBlockMoveBehavior;
import studio.fantasyit.maid_useful_task.util.WrappedMaidFakePlayer;

import java.util.*;

public interface IMaidBlockDestroyTask {
    default @Nullable List<BlockPos> toDestroyFromStanding(EntityMaid maid, BlockPos targetPos, BlockPos standPos) {
        List<BlockPos> list = new ArrayList<>();
        Vec3 eyePos = standPos.getCenter().add(0, maid.getEyeHeight() - 0.5, 0);
        Boolean available = BlockGetter.traverseBlocks(eyePos, targetPos.getCenter(), maid.level(), (level, pos) -> {
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) {
                return null;
            }
            if (mayDestroy(maid, pos)) {
                list.add(pos.immutable());
                return null;
            } else return false;
        }, (a) -> true);
        if (available) {
            return list;
        }
        return null;
    }

    /**
     * 获取尝试破坏的方块列表。不包含起点列表
     *
     * @param startPos 开始位置
     * @param maid     执行操作的女仆
     * @return 相连的所有方块
     */
    default List<BlockPos> getTryDestroyBlockListBesidesStart(BlockPos startPos, BlockPos standPos, EntityMaid maid) {
        Set<BlockPos> marked = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        List<BlockPos> result = new ArrayList<>();
        final int[] dv = {0, 1, -1};
        final int maxDXZ = 2;
        queue.add(startPos);
        marked.add(startPos);
        // C(9), O(N)
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            for (int dx : dv) {
                for (int dy : dv) {
                    for (int dz : dv) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos target = pos.offset(dx, dy, dz);
                        if (Math.abs(target.getX() - standPos.getX()) > maxDXZ || Math.abs(target.getZ() - standPos.getZ()) > maxDXZ)
                            continue;
                        if (target.distSqr(standPos) > reachDistance() * reachDistance()) continue;
                        if (marked.contains(target)) continue;
                        if (!shouldDestroyBlock(maid, target)) continue;
                        List<BlockPos> targetList = toDestroyFromStanding(maid, target, standPos);
                        if (targetList == null) continue;
                        marked.add(target);
                        result.addAll(targetList);
                        queue.add(target);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 女仆是否想要破坏这个方块，需要判断工具符合等，将用于寻路判断
     *
     * @param maid 女仆
     * @param pos  目标位置
     * @return 是否想要破坏
     */
    boolean shouldDestroyBlock(EntityMaid maid, BlockPos pos);

    /**
     * 女仆是否可以破坏这个方块，光线投射确定女仆的挖掘地点
     *
     * @param maid
     * @param pos
     * @return
     */
    boolean mayDestroy(EntityMaid maid, BlockPos pos);

    /**
     * 女仆是否可以破坏这个方块，用于进行破坏前检查
     *
     * @param maid 女仆
     * @param pos  位置
     * @return 是否可以破坏
     */
    default boolean canDestroyBlock(EntityMaid maid, BlockPos pos) {
        if (maid.distanceToSqr(pos.getCenter()) > Math.pow(reachDistance(), 2)) {
            return false;
        }
        return true;
    }

    /**
     * 尝试破坏方块
     *
     * @param maid 女仆
     * @param blockPos  位置
     * @return
     */
    default boolean tryDestroyBlock(EntityMaid maid, BlockPos blockPos) {
        ServerLevel level = (ServerLevel) maid.level();
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.isAir()) {
            return false;
        } else {
            FluidState fluidState = level.getFluidState(blockPos);
            if (!(blockState.getBlock() instanceof BaseFireBlock)) {
                level.levelEvent(2001, blockPos, Block.getId(blockState));
            }

            BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
            //改用MainHandItem来roll loot
            maid.dropResourcesToMaidInv(blockState, level, blockPos, blockEntity, maid, maid.getMainHandItem());

            boolean setResult = level.setBlock(blockPos, fluidState.createLegacyBlock(), 3);
            if (setResult) {
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(maid, blockState));
            }

            return setResult;
        }
    }

    /**
     * 尝试拿取工具。如果没有工具什么都不需要操作，后续交给canDestroy和shouldDestroy判断
     *
     * @param maid
     */
    default void tryTakeOutTool(EntityMaid maid) {
    }

    default void tryTakeOutToolForTarget(EntityMaid maid, BlockPos pos) {
        tryTakeOutTool(maid);
    }

    default boolean availableToGetDrop(EntityMaid maid, WrappedMaidFakePlayer fakePlayer, BlockPos pos, BlockState targetBlockState) {
        return fakePlayer.hasCorrectToolForDrops(targetBlockState);
    }

    default int reachDistance() {
        return 6;
    }

    default @NotNull List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(@NotNull EntityMaid entityMaid) {
        return List.of(
                Pair.of(5, new DestoryBlockBehavior()),
                Pair.of(4, new DestoryBlockMoveBehavior())
        );
    }
}