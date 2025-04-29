package studio.fantasyit.maid_useful_task.behavior.common;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidCheckRateTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import studio.fantasyit.maid_useful_task.registry.MemoryModuleRegistry;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.Map;

public class UpdateValidationMemoryBehavior extends MaidCheckRateTask {
    public UpdateValidationMemoryBehavior() {
        super(Map.of(MemoryModuleRegistry.BLOCK_VALIDATION.get(), MemoryStatus.VALUE_PRESENT));
        this.setMaxCheckRate(600);
    }

    @Override
    protected void start(ServerLevel p_22540_, EntityMaid p_22541_, long p_22542_) {
        //Hardcoded range?
        MemoryUtil.getBlockValidationMemory(p_22541_).clearFaraway(p_22541_.blockPosition(), 72);
        MemoryUtil.getBlockValidationMemory(p_22541_).clearIf(pos -> p_22540_.getBlockState(pos).isAir());
    }
}
