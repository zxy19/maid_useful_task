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

public class PlayerReviveBehavior extends Behavior<EntityMaid> {
    protected static class TryAttackMaidGoal extends TargetGoal {
        private final EntityMaid maid;

        public TryAttackMaidGoal(Mob p_26140_, EntityMaid maid) {
            super(p_26140_, true);
            this.maid = maid;
        }

        @Override
        public boolean canUse() {
            return mob.canAttack(maid);
        }

        @Override
        public void start() {
            mob.setTarget(maid);
            super.start();
        }

        public boolean isMaid(EntityMaid maid) {
            return maid.getUUID().equals(this.maid.getUUID());
        }
    }

    public PlayerReviveBehavior() {
        super(Map.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT), 600);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel p_22538_, EntityMaid maid) {
        Optional<NearestVisibleLivingEntities> memory = maid.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        return memory.map(list -> list
                .find(entity -> entity instanceof Player)
                .map(ep -> PlayerReviveServer.getBleeding((ServerPlayer) ep))
                .anyMatch(IBleeding::isBleeding)
        ).orElse(false);
    }

    ServerPlayer targetPlayer;
    IBleeding bleeding;
    boolean startedRevive;
    Set<UUID> aggroEntities;

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long p_22542_) {
        super.start(level, maid, p_22542_);
        aggroEntities = new HashSet<>();
        startedRevive = false;
        boolean ownerOnly = maid.getOrCreateData(MaidReviveConfig.KEY, MaidReviveConfig.Data.getDefault()).ownerOnly();
        LivingEntity owner = maid.getOwner();
        Optional<NearestVisibleLivingEntities> memory = maid.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        targetPlayer = memory.flatMap(list -> list
                .find(entity -> entity instanceof Player)
                .map(ep -> (ServerPlayer) ep)
                .filter(sp -> (owner != null && sp.is(owner)) || !ownerOnly)
                .filter(ep -> PlayerReviveServer.getBleeding(ep).isBleeding())
                .findFirst()
        ).orElse(null);
        if (targetPlayer != null) {
            bleeding = PlayerReviveServer.getBleeding(targetPlayer);
            BehaviorUtils.setWalkAndLookTargetMemories(maid, targetPlayer, 0.5f, 2);
        }
        useTotemOfUndying(level, maid);
    }

    private void useTotemOfUndying(ServerLevel level, EntityMaid maid) {
        if (!Config.enableReviveTotem) return;
        ItemStack itemstack = InvUtil.tryExtractOneMatches(maid.getMaidBauble(), (stack) -> stack.is(Items.TOTEM_OF_UNDYING));
        if (!itemstack.isEmpty()) {
            targetPlayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
            CriteriaTriggers.USED_TOTEM.trigger(targetPlayer, itemstack);

            targetPlayer.setHealth(1.0F);
            targetPlayer.removeAllEffects();
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            targetPlayer.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            level.broadcastEntityEvent(targetPlayer, (byte) 35);

            PlayerReviveServer.revive(targetPlayer);
        }
    }

    private void checkCanReviveAndStartRevive(ServerLevel level, EntityMaid maid) {
        if (PlayerRevive.CONFIG.revive.needReviveItem) {
            if (PlayerRevive.CONFIG.revive.consumeReviveItem && !bleeding.isItemConsumed()) {
                ItemStack extractedForConsume = InvUtil.tryExtractOneMatches(maid.getAvailableInv(true), PlayerRevive.CONFIG.revive.reviveItem::is);
                if (!PlayerRevive.CONFIG.revive.reviveItem.is(extractedForConsume)) {
                    targetPlayer = null;
                    return;
                }

                bleeding.setItemConsumed();
            }
        }

        PlayerReviveServer.removePlayerAsHelper(WrappedMaidFakePlayer.get(maid));
        bleeding.revivingPlayers().add(WrappedMaidFakePlayer.get(maid));
        aggroEntitiesAround(level, maid);
    }

    @Override
    protected boolean canStillUse(ServerLevel p_22545_, EntityMaid maid, long p_22547_) {
        if (targetPlayer == null) return false;
        if (targetPlayer.distanceTo(maid) > PlayerRevive.CONFIG.revive.maxDistance) return false;
        return bleeding.isBleeding();
    }

    protected void aggroEntitiesAround(ServerLevel level, EntityMaid maid) {
        if (!Config.enableReviveAggro) return;
        List<Monster> entities = level.getEntities(EntityTypeTest.forClass(Monster.class),
                AABB.ofSize(maid.position(), 16, 16, 16),
                entity -> true
        );
        for (Monster entity : entities) {
            if (!aggroEntities.contains(entity.getUUID())) {
                entity.targetSelector.addGoal(10, new TryAttackMaidGoal(entity, maid));
                aggroEntities.add(entity.getUUID());
            }
        }
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long p_22553_) {
        super.tick(level, maid, p_22553_);
        if (p_22553_ % 20 == 0)
            BehaviorUtils.setWalkAndLookTargetMemories(maid, targetPlayer, 0.5f, 2);
        if (!startedRevive) {
            if (maid.distanceTo(targetPlayer) < PlayerRevive.CONFIG.revive.maxDistance) {
                checkCanReviveAndStartRevive(level, maid);
                startedRevive = true;
            }
        } else {
            if (p_22553_ % 20 == 0)
                aggroEntitiesAround(level, maid);
        }
    }

    @Override
    protected void stop(ServerLevel p_22548_, EntityMaid maid, long p_22550_) {
        PlayerReviveServer.removePlayerAsHelper(WrappedMaidFakePlayer.get(maid));
        for (UUID uuid : aggroEntities) {
            Entity entity = p_22548_.getEntity(uuid);
            if (entity instanceof Monster monster && entity.isAlive())
                monster.targetSelector.removeAllGoals(g -> g instanceof TryAttackMaidGoal tg && tg.isMaid(maid));
        }
    }
}
