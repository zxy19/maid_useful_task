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
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

public class EnderEyeMoveBehavior extends Behavior<EntityMaid> {

    public EnderEyeMoveBehavior() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, InitEntities.TARGET_POS.get(), MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_22538_, EntityMaid maid) {
        if (!maid.getMainHandItem().is(Items.ENDER_EYE)) return false;
        if (maid.hasRestriction()) return false;
        LivingEntity owner = maid.getOwner();
        return (owner != null && maid.distanceTo(owner) < 5);
    }

    @Override
    protected void start(ServerLevel serverlevel, EntityMaid maid, long p_22542_) {
        BlockPos target = MemoryUtil.getCommonBlockCache(maid);
        LivingEntity owner = maid.getOwner();
        if (owner == null) return;
        if (target == null) {
            BlockPos blockpos = serverlevel.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, maid.blockPosition(), 100, false);
            if (blockpos != null) {
                MemoryUtil.setCommonBlockCache(maid, blockpos);
                target = blockpos;
            }
        }
        if (target != null) {
            if (maid.distanceToSqr(target.getCenter()) < 9) {
                maid.getJumpControl().jump();
            }

            BlockPos finalTarget = target;
            BlockPos ownerPos = owner.blockPosition();
            BlockPos maidPos = maid.blockPosition();
            if (finalTarget.distSqr(ownerPos) > 10 * 10) {
                Vec3 dVec = target.getCenter().subtract(owner.position());
                dVec = dVec.normalize().scale(7);
                BlockPos fTarget = maidPos.offset((int) dVec.x, (int) dVec.y, (int) dVec.z);

                for (int x = 0; x < 3; x = x <= 0 ? 1 - x : -x) {
                    for (int z = 0; z < 3; z = z <= 0 ? 1 - z : -z) {
                        int y = 0;
                        while (!serverlevel.getBlockState(fTarget.offset(x, y, z)).isAir()) y++;
                        while (!serverlevel.getBlockState(fTarget.offset(x, y, z)).isAir()) y--;

                        if (fTarget.offset(x, y, z).distSqr(ownerPos) < 10 * 10) {
                            finalTarget = fTarget.offset(x, y, z);
                        }
                    }
                }
            }
            if (finalTarget.distSqr(maidPos) < 10 * 10) {
                double distanceToOwner = maidPos.distSqr(ownerPos);
                double speed = 0.4;
                if (distanceToOwner < 4 * 4) {
                    speed = 0.5;
                }
                if (distanceToOwner < 3 * 3) {
                    speed = 0.64;
                }
                MemoryUtil.setTarget(maid, finalTarget, (float) speed);
            }
        }
    }
}
