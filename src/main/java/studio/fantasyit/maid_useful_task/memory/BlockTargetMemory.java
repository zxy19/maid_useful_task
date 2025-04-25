package studio.fantasyit.maid_useful_task.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Set;

public class BlockTargetMemory {
    public static final Codec<BlockTargetMemory> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos
                            .CODEC
                            .listOf()
                            .fieldOf("blockPosSet")
                            .forGetter(BlockTargetMemory::getBlockPosSet)
            ).apply(instance, BlockTargetMemory::new)
    );

    List<BlockPos> blockPosSet;

    public BlockTargetMemory(List<BlockPos> blockPosSet) {
        this.blockPosSet = blockPosSet;
    }

    public List<BlockPos> getBlockPosSet() {
        return blockPosSet;
    }

    public void setBlockPosSet(List<BlockPos> blockPosSet) {
        this.blockPosSet = blockPosSet;
    }
}