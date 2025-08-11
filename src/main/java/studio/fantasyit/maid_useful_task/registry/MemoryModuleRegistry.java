package studio.fantasyit.maid_useful_task.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.memory.BlockTargetMemory;
import studio.fantasyit.maid_useful_task.memory.BlockUpContext;
import studio.fantasyit.maid_useful_task.memory.BlockValidationMemory;
import studio.fantasyit.maid_useful_task.memory.CurrentWork;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleControlType;

import java.util.Optional;

public class MemoryModuleRegistry {
    public static final DeferredRegister<MemoryModuleType<?>> REGISTER
            = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, MaidUsefulTask.MODID);
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockTargetMemory>> DESTROY_TARGET
            = REGISTER.register("block_targets", () -> new MemoryModuleType<>(Optional.of(BlockTargetMemory.CODEC)));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> PLACE_TARGET
            = REGISTER.register("place_target", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockUpContext>> BLOCK_UP_TARGET
            = REGISTER.register("block_up", () -> new MemoryModuleType<>(Optional.of(BlockUpContext.CODEC)));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockValidationMemory>> BLOCK_VALIDATION
            = REGISTER.register("block_validation", () -> new MemoryModuleType<>(Optional.of(BlockValidationMemory.CODEC)));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> COMMON_BLOCK_CACHE
            = REGISTER.register("common_block_cache", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ItemStack>> LOCATE_ITEM = REGISTER.register("locate_item", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<CurrentWork>> CURRENT_WORK = REGISTER.register("current_work", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<MaidVehicleControlType>> IS_ALLOW_HANDLE_VEHICLE = REGISTER.register("is_allow_handle_vehicle", () -> new MemoryModuleType<>(Optional.empty()));

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
