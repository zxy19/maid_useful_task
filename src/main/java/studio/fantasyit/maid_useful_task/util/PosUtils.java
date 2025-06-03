package studio.fantasyit.maid_useful_task.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;

public class PosUtils {
    public static boolean isFourSideAir(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).isAir() &&
                level.getBlockState(pos.north()).isAir() &&
                level.getBlockState(pos.south()).isAir() &&
                level.getBlockState(pos.east()).isAir() &&
                level.getBlockState(pos.west()).isAir();
    }
    static protected boolean isEmptyBlockPos(Level level, BlockPos pos) {
        return level.getBlockState(pos).isAir() || level.getBlockState(pos).getCollisionShape(
                level,
                pos,
                CollisionContext.empty()
        ).isEmpty();
    }

    static public boolean isSafePos(Level level, BlockPos pos) {
        return isEmptyBlockPos(level, pos)
                && isEmptyBlockPos(level, pos.above())
                && !isEmptyBlockPos(level, pos.below());
    }
}
