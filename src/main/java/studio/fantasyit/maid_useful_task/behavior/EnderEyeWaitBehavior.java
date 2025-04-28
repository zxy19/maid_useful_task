package studio.fantasyit.maid_useful_task.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import studio.fantasyit.maid_useful_task.util.Conditions;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

public class EnderEyeWaitBehavior extends Behavior<EntityMaid> {

    public EnderEyeWaitBehavior() {
        super(ImmutableMap.of(InitEntities.TARGET_POS.get(), MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_22538_, EntityMaid maid) {
        if (!maid.getMainHandItem().is(Items.ENDER_EYE)) return false;
        if (maid.hasRestriction()) return false;
        LivingEntity owner = maid.getOwner();
        if (owner != null && maid.distanceTo(owner) > 6) {
            return true;
        }
        return Conditions.hasReachedValidTargetOrReset(maid, 4);
    }

    @Override
    protected void start(ServerLevel serverlevel, EntityMaid maid, long p_22542_) {
        MemoryUtil.clearTarget(maid);
    }
}
