package studio.fantasyit.maid_useful_task.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.function.Predicate;

public class InvUtil {
    public static ItemStack tryExtractOneMatches(IItemHandler inv, Predicate<ItemStack> predicate) {
        return tryExtractOneMatches(inv, predicate, false);
    }

    public static ItemStack tryExtractOneMatches(IItemHandler inv, Predicate<ItemStack> predicate, boolean simulate) {
        int count = 0;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stackInSlot = inv.getStackInSlot(i);
            if (stackInSlot.isEmpty()) continue;
            if (predicate.test(stackInSlot)) {
                ItemStack get = inv.extractItem(i, 1, simulate);
                if (!get.isEmpty()) {
                    return get;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
