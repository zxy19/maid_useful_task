package studio.fantasyit.maid_useful_task.behavior.common;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;
import studio.fantasyit.maid_useful_task.task.IMaidFindTargetTask;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.Optional;

public class FindTargetMoveBehavior extends Behavior<EntityMaid> {
    /**
     * 竖直方向扫描的最大格数。原实现中向上寻找空气的 while 无上界，
     * 一旦整列都是实心方块会一路访问到世界顶端（最多约 400 次 getBlockState/列），
     * 且可能触及未加载区块并触发区块生成。这里显式限幅。
     */
    private static final int MAX_VERTICAL_SCAN = 24;

    public FindTargetMoveBehavior() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                InitEntities.TARGET_POS.get(), MemoryStatus.VALUE_ABSENT
        ));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_22538_, EntityMaid maid) {
        if (!(maid.getTask() instanceof IMaidFindTargetTask task)) return false;
        if (task.findTarget(p_22538_, maid) == null) return false;
        if (maid.hasRestriction()) return false;
        LivingEntity owner = maid.getOwner();
        return (owner != null && maid.distanceTo(owner) < task.minReScheduleDistance());
    }

    @Override
    protected void start(ServerLevel serverlevel, EntityMaid maid, long p_22542_) {
        if (!(maid.getTask() instanceof IMaidFindTargetTask task)) return;
        LivingEntity owner = maid.getOwner();
        if (owner == null) return;
        BlockPos target = task.findTarget(serverlevel, maid);
        if (target == null) return;
        if (maid.distanceToSqr(target.getCenter()) < 9) {
            maid.getJumpControl().jump();
            return;
        }
        BlockPos finalTarget = target;
        BlockPos ownerPos = owner.blockPosition();
        BlockPos maidPos = maid.blockPosition();
        double maxOutDistanceSq = (double) task.maxOutDistance() * task.maxOutDistance();
        if (finalTarget.distSqr(ownerPos) > maxOutDistanceSq) {
            Vec3 dVec = target.getCenter().subtract(owner.position());
            dVec = dVec.normalize().scale(task.maxOutDistance() - (double) (task.maxOutDistance() - task.minReScheduleDistance()) / 2);
            BlockPos fTarget = maidPos.offset((int) dVec.x, (int) dVec.y, (int) dVec.z);

            for (int x = 0; x < 3; x = x <= 0 ? 1 - x : -x) {
                for (int z = 0; z < 3; z = z <= 0 ? 1 - z : -z) {
                    Optional<BlockPos> candidate = findStandablePos(serverlevel, fTarget, x, z);
                    if (candidate.isEmpty()) continue;
                    BlockPos pos = candidate.get();
                    if (pos.distSqr(ownerPos) < maxOutDistanceSq) {
                        finalTarget = pos;
                    }
                }
            }
        }
        if (finalTarget.distSqr(maidPos) < maxOutDistanceSq) {
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

    /**
     * 在偏移后的列上找一个可站立位置：先向上脱离实心方块，再向下贴地。
     * 原实现只有向上那一步，且紧随其后的第二个 while 条件与上一步结束条件互斥，
     * 属于永远不会执行的死代码，导致女仆可能停留在半空中的方块里。
     */
    private static Optional<BlockPos> findStandablePos(ServerLevel level, BlockPos origin, int offsetX, int offsetZ) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(origin.getX() + offsetX, origin.getY(), origin.getZ() + offsetZ);
        if (cursor.getY() < level.getMinBuildHeight() || cursor.getY() >= level.getMaxBuildHeight()) {
            return Optional.empty();
        }

        int up = 0;
        while (!level.getBlockState(cursor).isAir()) {
            if (++up > MAX_VERTICAL_SCAN) return Optional.empty();
            int above = cursor.getY() + 1;
            if (above >= level.getMaxBuildHeight()) return Optional.empty();
            cursor.setY(above);
        }

        BlockPos.MutableBlockPos below = new BlockPos.MutableBlockPos();
        for (int down = 0; down < MAX_VERTICAL_SCAN; down++) {
            int under = cursor.getY() - 1;
            if (under < level.getMinBuildHeight()) break;
            below.set(cursor.getX(), under, cursor.getZ());
            // 脚下为空气说明还在半空，继续下探直到踩到实体方块
            if (!level.getBlockState(below).isAir()) break;
            cursor.setY(under);
        }
        return Optional.of(cursor.immutable());
    }
}
