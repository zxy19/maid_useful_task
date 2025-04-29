package studio.fantasyit.maid_useful_task.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

public class RotUtil {
    public static float getXRot(Vec3 from, Vec3 to) {
        double d0 = to.x() - from.x();
        double d1 = to.y() - from.y();
        double d2 = to.z() - from.z();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        return Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI))));
    }

    public static float getYRot(Vec3 from, Vec3 to) {
        double d0 = to.x() - from.x();
        double d1 = to.y() - from.y();
        double d2 = to.z() - from.z();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        return Mth.wrapDegrees((float) (-(Mth.atan2(d0, d2) * (double) (180F / (float) Math.PI))));
    }
}
