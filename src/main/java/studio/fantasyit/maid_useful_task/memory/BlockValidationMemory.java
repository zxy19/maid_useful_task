package studio.fantasyit.maid_useful_task.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class BlockValidationMemory {
    public static final Codec<BlockValidationMemory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.list(BlockPos.CODEC).fieldOf("validList").forGetter(BlockValidationMemory::getValidList),
                    Codec.list(BlockPos.CODEC).fieldOf("invalidList").forGetter(BlockValidationMemory::getInvalidList)
            ).apply(instance, BlockValidationMemory::new)
    );

    private final Set<BlockPos> validSet = new HashSet<>();
    private final Set<BlockPos> invalidSet = new HashSet<>();

    public BlockValidationMemory() {
    }

    public BlockValidationMemory(List<BlockPos> validList, List<BlockPos> invalidList) {
        validSet.addAll(validList);
        invalidSet.addAll(invalidList);
    }

    public List<BlockPos> getValidList() {
        return List.copyOf(validSet);
    }

    public List<BlockPos> getInvalidList() {
        return List.copyOf(invalidSet);
    }

    public void setValid(BlockPos blockPos) {
        validSet.add(blockPos);
        invalidSet.remove(blockPos);
    }

    public void setInvalid(BlockPos blockPos) {
        invalidSet.add(blockPos);
        validSet.remove(blockPos);
    }

    public void remove(BlockPos blockPos) {
        validSet.remove(blockPos);
        invalidSet.remove(blockPos);
    }

    public boolean isValid(BlockPos blockPos, boolean defaultValue) {
        if (validSet.contains(blockPos)) {
            return true;
        }
        if (invalidSet.contains(blockPos)) {
            return false;
        }
        return defaultValue;
    }

    public boolean hasRecord(BlockPos blockPos) {
        return validSet.contains(blockPos) || invalidSet.contains(blockPos);
    }

    public void clearFaraway(BlockPos blockPos, int range) {
        clearIf(pos->pos.distSqr(blockPos) > range * range);
    }

    public void clearIf(Predicate<BlockPos> o) {
        validSet.removeIf(o);
        invalidSet.removeIf(o);
    }
}
