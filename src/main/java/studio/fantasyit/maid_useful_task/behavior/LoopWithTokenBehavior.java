package studio.fantasyit.maid_useful_task.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.Map;

public class LoopWithTokenBehavior extends Behavior<EntityMaid> {
    public LoopWithTokenBehavior() {
        super(Map.of());
    }

    @Override
    protected void start(ServerLevel p_22540_, EntityMaid p_22541_, long p_22542_) {
        MemoryUtil.getRateLimitToken(p_22541_).tick(p_22541_);
    }
}
