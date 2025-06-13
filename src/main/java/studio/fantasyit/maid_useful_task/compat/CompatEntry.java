package studio.fantasyit.maid_useful_task.compat;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class CompatEntry {
    public static BlockPos getLocateTarget(EntityMaid maid, ItemStack itemStack) {
        BlockPos tmp = null;
        if (tmp == null && ModList.get().isLoaded("naturescompass")) {
            tmp = NatureCompass.getCompassTarget(maid, itemStack);
        }
        if (tmp == null && ModList.get().isLoaded("explorerscompass")) {
            tmp = ExplorerCompass.getCompassTarget(maid, itemStack);
        }

        return tmp;
    }
}
