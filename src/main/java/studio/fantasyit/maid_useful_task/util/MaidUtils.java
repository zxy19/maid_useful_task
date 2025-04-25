package studio.fantasyit.maid_useful_task.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.wrapper.RangedWrapper;

import java.util.UUID;
import java.util.function.Predicate;

public class MaidUtils {
    public static void swapToHand(EntityMaid maid, Predicate<ItemStack> isSuitable) {
        RangedWrapper availableBackpackInv = maid.getAvailableBackpackInv();
        for (int i = 0; i < availableBackpackInv.getSlots(); i++) {
            ItemStack itemStack = availableBackpackInv.getStackInSlot(i);
            if (isSuitable.test(itemStack)) {
                ItemStack tmp = availableBackpackInv.getStackInSlot(i);
                availableBackpackInv.setStackInSlot(i, maid.getMainHandItem());
                maid.setItemInHand(InteractionHand.MAIN_HAND, tmp);
                return;
            }
        }
    }
}
