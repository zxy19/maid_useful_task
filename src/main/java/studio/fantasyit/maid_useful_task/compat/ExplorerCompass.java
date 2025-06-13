package studio.fantasyit.maid_useful_task.compat;

import com.chaosthedude.explorerscompass.ExplorersCompass;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class ExplorerCompass {
    public static BlockPos getCompassTarget(EntityMaid maid, ItemStack itemStack) {
       if (itemStack.is(ExplorersCompass.explorersCompass)) {
            return new BlockPos(
                    ExplorersCompass.explorersCompass.getFoundStructureX(itemStack),
                    maid.level().getSeaLevel(),
                    ExplorersCompass.explorersCompass.getFoundStructureZ(itemStack)
            );
        }
        return null;
    }

}
