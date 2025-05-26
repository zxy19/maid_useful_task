package studio.fantasyit.maid_useful_task.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import studio.fantasyit.maid_useful_task.Config;
import studio.fantasyit.maid_useful_task.data.MaidReviveConfig;
import studio.fantasyit.maid_useful_task.util.InvUtil;
import studio.fantasyit.maid_useful_task.util.WrappedMaidFakePlayer;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.server.PlayerReviveServer;

import java.util.*;

public class SupportEffectBehavior extends Behavior<EntityMaid> {
    public SupportEffectBehavior() {
        super(Map.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_22538_, EntityMaid maid) {
        if (maid.getExperience() <= 0) return false;
        LivingEntity player = maid.getOwner();
        if (player == null) return false;
        Optional<NearestVisibleLivingEntities> memory = maid.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        return memory.map(list -> list
                .find(entity -> entity instanceof Monster)
                .map(monster -> (Monster) monster)
                .anyMatch(monster -> {
                    if (monster.getTarget() != null && monster.getTarget().equals(player)) {
                        return true;
                    }
                    return false;
                })
        ).orElse(false);
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long p_22542_) {
        super.start(level, maid, p_22542_);
    }

    @Override
    protected boolean canStillUse(ServerLevel p_22545_, EntityMaid maid, long p_22547_) {
        return this.checkExtraStartConditions(p_22545_, maid);
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long p_22553_) {
        super.tick(level, maid, p_22553_);
        if (p_22553_ % 20 == 0) {
            //1  experience per second.
            maid.setExperience(maid.getExperience() - 1);
            reApplyEffects(level, maid);
        }
    }

    private void reApplyEffects(ServerLevel level, EntityMaid maid) {
        LivingEntity player = maid.getOwner();
        if (player == null) return;
        List<Entity> entities = level.getEntities(maid, AABB.ofSize(maid.position(), 16, 16, 16));
        for (Entity entity : entities) {
            if (entity instanceof Monster monster && entity.isAlive()) {
                monster.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30, 0, false, false));
            } else if (entity instanceof TamableAnimal animal && animal.isAlive()) {
                if (animal.getOwner() != null && animal.getOwner().equals(player)) {
                    animal.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30, 0, false, false));
                    animal.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30, 1, false, false));
                }
            } else if (entity.is(player)) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30, 1, false, false));
            }
        }
    }


    @Override
    protected void stop(ServerLevel p_22548_, EntityMaid maid, long p_22550_) {
    }

    @Override
    protected boolean timedOut(long p_22537_) {
        return false;
    }
}
