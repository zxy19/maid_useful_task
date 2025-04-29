package studio.fantasyit.maid_useful_task.behavior.common;


import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidCheckRateTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidPathFindingBFS;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import studio.fantasyit.maid_useful_task.util.MaidUtils;

/**
 * From https://github.com/TartaricAcid/TouhouLittleMaid
 * 为了重载getWorkSearchPos而写的
 */
abstract public class MaidCenterMoveToBlockTask extends Behavior<EntityMaid> {

    private static final int MAX_DELAY_TIME = 120;
    private final float movementSpeed;
    private final int verticalSearchRange;
    private int searchRange;
    protected int verticalSearchStart;

    public MaidCenterMoveToBlockTask(float movementSpeed) {
        this(movementSpeed, 1);
    }

    public MaidCenterMoveToBlockTask(float movementSpeed, int verticalSearchRange) {
        this(movementSpeed, verticalSearchRange, 7);
    }

    public MaidCenterMoveToBlockTask(float movementSpeed, int verticalSearchRange, int defaultSearchRange) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, InitEntities.TARGET_POS.get(), MemoryStatus.VALUE_ABSENT));
        this.movementSpeed = movementSpeed;
        this.verticalSearchRange = verticalSearchRange;
        this.searchRange = defaultSearchRange;
    }
    public void setSearchRange(int searchRange) {
        this.searchRange = searchRange;
    }
    protected final void searchForDestination(ServerLevel worldIn, EntityMaid maid) {
        MaidPathFindingBFS pathFinding = this.getOrCreateArrivalMap(worldIn, maid);
        BlockPos centrePos = this.getWorkSearchPos(maid);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for (int y = this.verticalSearchStart; y <= this.verticalSearchRange; y = y > 0 ? -y : 1 - y) {
            for (int i = 0; i < searchRange; ++i) {
                for (int x = 0; x <= i; x = x > 0 ? -x : 1 - x) {
                    for (int z = x < i && x > -i ? i : 0; z <= i; z = z > 0 ? -z : 1 - z) {
                        mutableBlockPos.setWithOffset(centrePos, x, y - 1, z);
                        if (this.shouldMoveTo(worldIn, maid, mutableBlockPos) && this.checkPathReach(maid, pathFinding, mutableBlockPos) && this.checkOwnerPos(maid, mutableBlockPos)) {
                            BehaviorUtils.setWalkAndLookTargetMemories(maid, mutableBlockPos, this.movementSpeed, 0);
                            maid.getBrain().setMemory((MemoryModuleType) InitEntities.TARGET_POS.get(), new BlockPosTracker(mutableBlockPos));
                            this.clearCurrentArrivalMap(pathFinding);
                            return;
                        }
                    }
                }
            }
        }

        this.clearCurrentArrivalMap(pathFinding);
    }

    protected void clearCurrentArrivalMap(MaidPathFindingBFS pathFinding) {
        pathFinding.finish();
    }

    protected MaidPathFindingBFS getOrCreateArrivalMap(ServerLevel worldIn, EntityMaid maid) {
        return new MaidPathFindingBFS(maid.getNavigation().getNodeEvaluator(), worldIn, maid);
    }

    private BlockPos getWorkSearchPos(EntityMaid maid) {
        return MaidUtils.getMaidRestrictCenter(maid);
    }

    private boolean checkOwnerPos(EntityMaid maid, BlockPos mutableBlockPos) {
        if (maid.isHomeModeEnable()) {
            return true;
        } else {
            return maid.getOwner() != null && mutableBlockPos.closerToCenterThan(maid.getOwner().position(), 8.0);
        }
    }

    protected abstract boolean shouldMoveTo(ServerLevel var1, EntityMaid var2, BlockPos var3);

    /**
     * @deprecated
     */
    @Deprecated(
            forRemoval = true
    )
    protected boolean checkPathReach(EntityMaid maid, BlockPos pos) {
        return maid.canPathReach(pos);
    }

    protected boolean checkPathReach(EntityMaid maid, MaidPathFindingBFS pathFinding, BlockPos pos) {
        return pathFinding.canPathReach(pos);
    }
}
