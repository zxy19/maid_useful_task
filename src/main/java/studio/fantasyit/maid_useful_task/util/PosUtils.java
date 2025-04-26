package studio.fantasyit.maid_useful_task.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public class PosUtils {
    public static boolean isFourSideAir(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).isAir() &&
                level.getBlockState(pos.north()).isAir() &&
                level.getBlockState(pos.south()).isAir() &&
                level.getBlockState(pos.east()).isAir() &&
                level.getBlockState(pos.west()).isAir();
    }
}
