package studio.fantasyit.maid_useful_task.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.Optional;

public class BlockUpContext {
    public static final Codec<BlockUpContext> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            BlockPos.CODEC.optionalFieldOf("startPos").forGetter(BlockUpContext::getOptionalStartPos),
                            BlockPos.CODEC.optionalFieldOf("targetPos").forGetter(BlockUpContext::getOptionalTargetPos)
                    )
                    .apply(instance, BlockUpContext::new)
    );

    public enum STATUS {
        IDLE,
        DOWN,
        UP
    }

    boolean isGoingDown = false;
    /**
     * 结束位置（女仆所在位置，实际方块为target-1）
     */
    BlockPos targetPos;
    BlockPos startPos;

    public BlockUpContext() {
        this(Optional.empty(), Optional.empty());
    }

    public BlockUpContext(BlockPos startPos, BlockPos targetPos) {
        this.startPos = startPos;
        this.targetPos = targetPos;
    }

    public BlockUpContext(Optional<BlockPos> startPos, Optional<BlockPos> targetPos) {
        this.startPos = startPos.orElse(null);
        this.targetPos = targetPos.orElse(null);
    }

    public BlockPos getStartPos() {
        return startPos;
    }

    public BlockPos getTargetPos() {
        return targetPos;
    }

    public Optional<BlockPos> getOptionalStartPos() {
        return Optional.ofNullable(startPos);
    }

    public Optional<BlockPos> getOptionalTargetPos() {
        return Optional.ofNullable(targetPos);
    }


    public boolean isOnLine(BlockPos pos) {
        return pos.getX() == startPos.getX() && pos.getZ() == startPos.getZ() && pos.getY() >= startPos.getY() && pos.getY() <= targetPos.getY();
    }

    public void setStartTarget(BlockPos startPos, BlockPos targetPos) {
        this.startPos = startPos;
        this.targetPos = targetPos;
        setStatus(STATUS.IDLE);
    }

    public void clearStartTarget() {
        this.startPos = null;
        this.targetPos = null;
        setStatus(STATUS.IDLE);
    }

    public boolean hasTarget() {
        return startPos != null && targetPos != null;
    }

    public boolean isTarget(BlockPos pos) {
        return pos.equals(targetPos);
    }

    private STATUS status = STATUS.IDLE;

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }
}
