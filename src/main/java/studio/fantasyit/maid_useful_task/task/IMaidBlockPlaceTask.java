package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.fantasyit.maid_useful_task.behavior.common.DestoryBlockBehavior;
import studio.fantasyit.maid_useful_task.behavior.common.DestoryBlockMoveBehavior;
import studio.fantasyit.maid_useful_task.util.MaidUtils;

import java.util.List;

public interface IMaidBlockPlaceTask {
    boolean shouldPlaceItemStack(EntityMaid maid, ItemStack itemStack);

    boolean shouldPlacePos(EntityMaid maid, ItemStack itemStack, BlockPos pos);

    default boolean tryPlaceBlock(EntityMaid maid, BlockPos pos){
        return MaidUtils.placeBlock(maid,pos);
    }
    default @NotNull List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(@NotNull EntityMaid entityMaid) {
        return List.of(
                Pair.of(5, new DestoryBlockBehavior()),
                Pair.of(4, new DestoryBlockMoveBehavior())
        );
    }
}
