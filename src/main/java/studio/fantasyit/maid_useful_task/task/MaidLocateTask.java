package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.Config;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.behavior.common.FindTargetMoveBehavior;
import studio.fantasyit.maid_useful_task.behavior.common.FindTargetWaitBehavior;
import studio.fantasyit.maid_useful_task.compat.CompatEntry;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

public class MaidLocateTask implements IMaidTask, IMaidFindTargetTask {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MaidUsefulTask.MODID, "locate");

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
        BlockPos target = null;
        ItemStack itemStack = maid.getMainHandItem();
        ItemStack last = MemoryUtil.getLocateItem(maid);
        if (!last.isEmpty() && !itemStack.isEmpty() && ItemStack.isSameItemSameComponents(last, itemStack)) {
            MemoryUtil.setLocateItem(maid, itemStack);
            MemoryUtil.clearCommonBlockCache(maid);
        }
        if (maid.getMainHandItem().is(Items.ENDER_EYE)) {
            target = MemoryUtil.getCommonBlockCache(maid);
            if (target == null) {
                BlockPos blockpos = level.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, maid.blockPosition(), 100, false);
                if (blockpos != null) {
                    MemoryUtil.setCommonBlockCache(maid, blockpos);
                    target = blockpos;
                }
            }
        } else if (maid.getMainHandItem().is(Items.COMPASS)) {
            target = MemoryUtil.getCommonBlockCache(maid);
            if (target == null) {
                GlobalPos globalPos;
                LodestoneTracker lodeData = itemStack.get(DataComponents.LODESTONE_TRACKER);
                if (lodeData != null && lodeData.target().isPresent()) {
                    globalPos = lodeData.target().get();
                } else {
                    globalPos = CompassItem.getSpawnPosition(level);
                }
                if (globalPos != null && level.dimension().equals(globalPos.dimension())) {
                    MemoryUtil.setCommonBlockCache(maid, globalPos.pos());
                    target = globalPos.pos();
                }
            }
        } else if (maid.getMainHandItem().is(ItemTags.BEDS)) {
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
                    if (target == null) {
                        MemoryUtil.setCommonBlockCache(maid, target);
                    }
                }
            }
        } else if (maid.getMainHandItem().is(Items.FILLED_MAP)) {
            target = MemoryUtil.getCommonBlockCache(maid);
            if (target == null) {
                MapItemSavedData savedData = MapItem.getSavedData(itemStack, maid.level());
                if (savedData != null) {
                    BlockPos.MutableBlockPos tmpTarget = new BlockPos.MutableBlockPos(savedData.centerX, level.getSeaLevel(), savedData.centerZ);

                    savedData.getBanners()
                            .stream()
                            .findFirst()
                            .ifPresent(t -> {
                                tmpTarget.set(t.pos().immutable());
                            });
                    MapDecorations decoList = itemStack.get(DataComponents.MAP_DECORATIONS);
                    if (decoList != null)
                        decoList.decorations().entrySet().stream()
                                .filter(t -> t.getValue().type() == MapDecorationTypes.RED_X)
                                .findFirst()
                                .ifPresent(t -> {
                                    tmpTarget.setX((int) t.getValue().x());
                                    tmpTarget.setZ((int) t.getValue().z());
                                });

                    target = tmpTarget.immutable();
                    MemoryUtil.setCommonBlockCache(maid, target);
                }
            }
        } else {
            target = CompatEntry.getLocateTarget(maid, maid.getMainHandItem());
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
    }
}

