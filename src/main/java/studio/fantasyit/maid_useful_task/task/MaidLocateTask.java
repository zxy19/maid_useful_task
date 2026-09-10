package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.Config;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.api.ItemLocateEvent;
import studio.fantasyit.maid_useful_task.behavior.common.FindTargetMoveBehavior;
import studio.fantasyit.maid_useful_task.behavior.common.FindTargetWaitBehavior;
import studio.fantasyit.maid_useful_task.compat.CompatEntry;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MaidLocateTask implements IMaidTask, IMaidFindTargetTask {
    public static final ResourceLocation UID = new ResourceLocation(MaidUsefulTask.MODID, "locate");

    /**
     * 同一 tick 内 findTarget 会被多个行为（移动/等待）重复调用，
     * 其中首次调用才会真正计算（含事件派发），后续复用结果。
     * 使用 WeakHashMap，女仆被回收后条目自动消失，不会泄漏。
     */
    private static final Map<EntityMaid, TickCache> TICK_CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    private record TickCache(long gameTime, @Nullable BlockPos target) {
    }

    public static void invalidateCache(EntityMaid maid) {
        TICK_CACHE.remove(maid);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public ItemStack getIcon() {
        return Items.ENDER_EYE.getDefaultInstance();
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound(EntityMaid entityMaid) {
        return null;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid entityMaid) {
        List<Pair<Integer, BehaviorControl<? super EntityMaid>>> list = new ArrayList<>();
        list.add(Pair.of(1, new FindTargetMoveBehavior()));
        list.add(Pair.of(2, new FindTargetWaitBehavior()));
        return list;
    }

    @Override
    public boolean enableLookAndRandomWalk(EntityMaid maid) {
        return false;
    }

    @Override
    public boolean isEnable(EntityMaid maid) {
        return Config.enableLocateTask;
    }

    @Override
    public @Nullable BlockPos findTarget(ServerLevel level, EntityMaid maid) {
        long now = level.getGameTime();
        TickCache cached = TICK_CACHE.get(maid);
        if (cached != null && cached.gameTime() == now) {
            return cached.target();
        }

        BlockPos target = computeTarget(level, maid, now);
        TICK_CACHE.put(maid, new TickCache(now, target));
        return target;
    }

    private @Nullable BlockPos computeTarget(ServerLevel level, EntityMaid maid, long now) {
        BlockPos target = null;
        ItemStack itemStack = maid.getMainHandItem();
        ItemStack last = MemoryUtil.getLocateItem(maid);
        // 手持物品发生变化（含数量与 NBT 差异）时，旧的定位结果不再有效
        boolean itemChanged = last.isEmpty() != itemStack.isEmpty()
                || (!last.isEmpty() && !ItemStack.isSameItemSameTags(last, itemStack));
        if (itemChanged) {
            MemoryUtil.setLocateItem(maid, itemStack.copy());
            MemoryUtil.clearCommonBlockCache(maid);
        }

        ItemLocateEvent event = new ItemLocateEvent(itemStack, maid, MemoryUtil.getCommonBlockCache(maid));
        if (MinecraftForge.EVENT_BUS.post(event)) {
            target = event.getTarget();
        } else if (itemStack.is(Items.ENDER_EYE)) {
            target = MemoryUtil.getCommonBlockCache(maid);
            if (target == null) {
                BlockPos blockpos = level.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, maid.blockPosition(), 100, false);
                if (blockpos != null) {
                    MemoryUtil.setCommonBlockCache(maid, blockpos);
                    target = blockpos;
                }
            }
        } else if (itemStack.is(Items.COMPASS)) {
            target = MemoryUtil.getCommonBlockCache(maid);
            if (target == null) {
                GlobalPos globalPos;
                if (CompassItem.isLodestoneCompass(itemStack)) {
                    globalPos = CompassItem.getLodestonePosition(itemStack.getOrCreateTag());
                } else {
                    globalPos = CompassItem.getSpawnPosition(level);
                }
                if (globalPos != null && level.dimension().equals(globalPos.dimension())) {
                    MemoryUtil.setCommonBlockCache(maid, globalPos.pos());
                    target = globalPos.pos();
                }
            }
        } else if (itemStack.is(ItemTags.BEDS)) {
            target = MemoryUtil.getCommonBlockCache(maid);
            if (target == null) {
                LivingEntity owner = maid.getOwner();
                if (owner instanceof ServerPlayer player) {
                    if (player.getRespawnDimension().equals(level.dimension())) {
                        target = player.getRespawnPosition();
                        if (target == null) {
                            GlobalPos globalRespawn = CompassItem.getSpawnPosition(level);
                            if (globalRespawn != null && level.dimension().equals(globalRespawn.dimension())) {
                                target = globalRespawn.pos();
                            }
                        }
                    }
                    if (target != null) {
                        MemoryUtil.setCommonBlockCache(maid, target);
                    } else {
                        MemoryUtil.clearCommonBlockCache(maid);
                    }
                }
            }
        } else if (itemStack.is(Items.FILLED_MAP)) {
            target = MemoryUtil.getCommonBlockCache(maid);
            if (target == null) {
                MapItemSavedData savedData = MapItem.getSavedData(itemStack, maid.level());
                if (savedData != null) {
                    BlockPos.MutableBlockPos tmpTarget = new BlockPos.MutableBlockPos(savedData.centerX, level.getSeaLevel(), savedData.centerZ);

                    CompoundTag tag = itemStack.getOrCreateTag();
                    savedData.getBanners()
                            .stream()
                            .findFirst()
                            .ifPresent(t -> tmpTarget.set(t.getPos().immutable()));
                    tag.getList("Decorations", Tag.TAG_COMPOUND)
                            .stream()
                            .filter(t -> ((CompoundTag) t).getByte("type") == 26)
                            .findFirst()
                            .ifPresent(t -> {
                                CompoundTag decoration = (CompoundTag) t;
                                tmpTarget.setX(decoration.getInt("x"));
                                tmpTarget.setZ(decoration.getInt("z"));
                            });

                    target = tmpTarget.immutable();
                    MemoryUtil.setCommonBlockCache(maid, target);
                }
            }
        } else {
            target = CompatEntry.getLocateTarget(maid, itemStack);
            if (target != null) {
                MemoryUtil.setCommonBlockCache(maid, target);
                return target;
            }
            MemoryUtil.clearCommonBlockCache(maid);
        }
        return target;
    }

    @Override
    public void clearCache(EntityMaid maid) {
        MemoryUtil.clearCommonBlockCache(maid);
        invalidateCache(maid);
    }
}
