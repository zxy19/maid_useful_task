package studio.fantasyit.maid_useful_task.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.behavior.common.FindTargetMoveBehavior;
import studio.fantasyit.maid_useful_task.behavior.common.FindTargetWaitBehavior;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

public class MaidLocateTask implements IMaidTask, IMaidFindTargetTask {
    public static final ResourceLocation UID = new ResourceLocation(MaidUsefulTask.MODID, "locate");

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
    public @Nullable BlockPos findTarget(ServerLevel level, EntityMaid maid) {
        BlockPos target = null;
        ItemStack itemStack = maid.getMainHandItem();
        ItemStack last = MemoryUtil.getLocateItem(maid);
        if (!last.isEmpty() && !itemStack.isEmpty() && ItemStack.isSameItem(last, itemStack)) {
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
                LivingEntity owner = maid.getOwner();
                if (owner != null) {
                    target = owner.getSleepingPos().orElse(maid.level().getSharedSpawnPos());
                }
            }
        } else {
            MemoryUtil.clearCommonBlockCache(maid);
        }
        return target;
    }

    @Override
    public void clearCache(EntityMaid maid) {
        MemoryUtil.clearCommonBlockCache(maid);
    }
}
