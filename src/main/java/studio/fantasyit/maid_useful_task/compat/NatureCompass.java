package studio.fantasyit.maid_useful_task.compat;

import com.chaosthedude.naturescompass.NaturesCompass;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class NatureCompass {
    public static BlockPos getCompassTarget(EntityMaid maid, ItemStack itemStack) {
        if (itemStack.is(NaturesCompass.naturesCompass)) {
            return new BlockPos(
                    NaturesCompass.naturesCompass.getFoundBiomeX(itemStack),
                    maid.level().getSeaLevel(),
                    NaturesCompass.naturesCompass.getFoundBiomeZ(itemStack)
            );
        }
        return null;
    }

}
