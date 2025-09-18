package studio.fantasyit.maid_useful_task.api;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ItemLocateEvent extends Event implements ICancellableEvent {
    public final ItemStack itemStack;
    public final EntityMaid maid;
    public final BlockPos cache;
    public BlockPos target = null;

    public ItemLocateEvent(ItemStack itemStack, EntityMaid maid, BlockPos cache) {
        this.itemStack = itemStack;
        this.maid = maid;
        this.cache = cache;
    }

    public BlockPos getTarget() {
        return target;
    }

    public void setTarget(BlockPos target) {
        this.target = target;
    }
}