package studio.fantasyit.maid_useful_task.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class InvUtil {
    public static ItemStack tryExtractOneMatches(IItemHandler inv, Predicate<ItemStack> predicate) {
        int count = 0;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stackInSlot = inv.getStackInSlot(i);
            if(stackInSlot.isEmpty()) continue;
            if (predicate.test(stackInSlot)) {
                ItemStack get = inv.extractItem(i, 1, false);
                if (!get.isEmpty()) {
                    return get;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
