package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import studio.fantasyit.maid_useful_task.behavior.DestoryBlockBehavior;
import studio.fantasyit.maid_useful_task.behavior.DestoryBlockMoveBehavior;
import studio.fantasyit.maid_useful_task.util.WrappedMaidFakePlayer;

import java.util.List;

public interface IMaidBlockPlaceTask {
    boolean shouldPlaceItemStack(EntityMaid maid, ItemStack itemStack);

    boolean shouldPlacePos(EntityMaid maid, ItemStack itemStack, BlockPos pos);

    default boolean tryPlaceBlock(EntityMaid maid, ItemStack itemStack, BlockPos pos){
        Player fakePlayer = WrappedMaidFakePlayer.get(maid);
        BlockHitResult result = null;
        ClipContext rayTraceContext = new ClipContext(maid.getPosition(0).add(0, maid.getEyeHeight(), 0),
                pos.getCenter(),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                fakePlayer);
        result = maid.level().clip(rayTraceContext);
        UseOnContext useContext = new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, result);
        InteractionResult actionresult = fakePlayer.getItemInHand(InteractionHand.MAIN_HAND).onItemUseFirst(useContext);
        if (actionresult == InteractionResult.PASS) {
            InteractionResult interactionResult = fakePlayer.getItemInHand(InteractionHand.MAIN_HAND).useOn(useContext);
            if (interactionResult.consumesAction()) {
                return true;
            }
        }
        return false;
    }
    default @NotNull List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(@NotNull EntityMaid entityMaid) {
        return List.of(
                Pair.of(5, new DestoryBlockBehavior()),
                Pair.of(4, new DestoryBlockMoveBehavior())
        );
    }
}
