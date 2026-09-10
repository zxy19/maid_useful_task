package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.fantasyit.maid_useful_task.behavior.common.PlaceBlockBehavior;
import studio.fantasyit.maid_useful_task.behavior.common.PlaceBlockMoveBehavior;
import studio.fantasyit.maid_useful_task.util.MaidUtils;

import java.util.List;

public interface IMaidBlockPlaceTask {
    boolean shouldPlaceItemStack(EntityMaid maid, ItemStack itemStack);

    boolean shouldPlacePos(EntityMaid maid, ItemStack itemStack, BlockPos pos);

    default boolean tryPlaceBlock(EntityMaid maid, BlockPos pos){
        return MaidUtils.placeBlock(maid,pos);
    }
    default @NotNull List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(@NotNull EntityMaid entityMaid) {
        // 此处原为破坏方块的行为（复制粘贴自 IMaidBlockDestroyTask），
        // 只实现放置接口的任务会因此永远不会放置方块
        return List.of(
                Pair.of(5, new PlaceBlockBehavior()),
                Pair.of(4, new PlaceBlockMoveBehavior())
        );
    }
}
