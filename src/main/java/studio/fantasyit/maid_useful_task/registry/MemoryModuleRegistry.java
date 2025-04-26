package studio.fantasyit.maid_useful_task.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.memory.BlockTargetMemory;
import studio.fantasyit.maid_useful_task.memory.BlockUpContext;
import studio.fantasyit.maid_useful_task.memory.TaskRateLimitToken;

import java.util.Optional;

public class MemoryModuleRegistry {
    public static final DeferredRegister<MemoryModuleType<?>> REGISTER
            = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, MaidUsefulTask.MODID);
    public static final RegistryObject<MemoryModuleType<BlockTargetMemory>> DESTROY_TARGET
            = REGISTER.register("block_targets", () -> new MemoryModuleType<>(Optional.of(BlockTargetMemory.CODEC)));
    public static final RegistryObject<MemoryModuleType<BlockPos>> PLACE_TARGET
            = REGISTER.register("place_target", () -> new MemoryModuleType<>(Optional.empty()));
    public static final RegistryObject<MemoryModuleType<BlockUpContext>> BLOCK_UP_TARGET
            = REGISTER.register("block_up", () -> new MemoryModuleType<>(Optional.of(BlockUpContext.CODEC)));
    public static final RegistryObject<MemoryModuleType<TaskRateLimitToken>> RATE_LIMIT_TOKEN
            = REGISTER.register("task_rate_limit", () -> new MemoryModuleType<>(Optional.empty()));

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
