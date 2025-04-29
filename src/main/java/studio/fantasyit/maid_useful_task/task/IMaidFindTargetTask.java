package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public interface IMaidFindTargetTask extends IMaidVehicleControlTask {
    @Nullable BlockPos findTarget(ServerLevel level, EntityMaid maid);

    void clearCache(EntityMaid maid);

    default int maxOutDistance() {
        return 10;
    }

    default int minReScheduleDistance() {
        return 3;
    }

    default int moveDistance() {
        return 10;
    }
}
