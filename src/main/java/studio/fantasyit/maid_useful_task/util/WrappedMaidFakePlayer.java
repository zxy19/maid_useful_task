package studio.fantasyit.maid_useful_task.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class WrappedMaidFakePlayer extends FakePlayer {
    public static class WrappedMaidInventory extends Inventory {
        private final EntityMaid maid;

        public WrappedMaidInventory(EntityMaid p_35983_, WrappedMaidFakePlayer fakePlayer) {
            super(fakePlayer);
            this.maid = p_35983_;
        }

        @Override
        public @NotNull ItemStack getSelected() {
            return maid.getMainHandItem();
        }

        @Override
        public float getDestroySpeed(BlockState p_36021_) {
            return maid.getMainHandItem().getDestroySpeed(p_36021_);
        }
    }

    private static ConcurrentHashMap<UUID, WrappedMaidFakePlayer> cache = new ConcurrentHashMap<>();
    private final EntityMaid maid;

    public static WrappedMaidFakePlayer get(EntityMaid maid) {
        if (cache.containsKey(maid.getUUID())) {
            WrappedMaidFakePlayer wrappedMaidFakePlayer = cache.get(maid.getUUID());
            if (!wrappedMaidFakePlayer.maid.isAlive()) {
                cache.remove(maid.getUUID());
            } else {
                return wrappedMaidFakePlayer;
            }
        }
        WrappedMaidFakePlayer fakePlayer = new WrappedMaidFakePlayer(maid);
        cache.put(maid.getUUID(), fakePlayer);
        return fakePlayer;

    }

    private WrappedMaidFakePlayer(EntityMaid maid) {
        super((ServerLevel) maid.level(), new GameProfile(UUID.randomUUID(), maid.getName().getString()));
        this.maid = maid;
        this.inventory = new WrappedMaidInventory(maid, this);
    }

    @Override
    public boolean removeEffect(Holder<MobEffect> p_21196_) {
        if (maid == null) return false;
        return maid.removeEffect(p_21196_);
    }

    @Nullable
    @Override
    public MobEffectInstance removeEffectNoUpdate(@Nullable Holder<MobEffect> p_21164_) {
        if (maid == null) return super.removeEffectNoUpdate(p_21164_);
        return maid.removeEffectNoUpdate(p_21164_);
    }

    @Override
    public boolean removeAllEffects() {
        if (maid == null) return false;
        return maid.removeAllEffects();
    }

    @Override
    public boolean addEffect(MobEffectInstance p_147208_, @Nullable Entity p_147209_) {
        if (maid == null) return super.addEffect(p_147208_, p_147209_);
        return maid.addEffect(p_147208_, p_147209_);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance p_21197_) {
        if (maid == null) return super.canBeAffected(p_21197_);
        return maid.canBeAffected(p_21197_);
    }

    @Override
    public void forceAddEffect(MobEffectInstance p_147216_, @Nullable Entity p_147217_) {
        maid.forceAddEffect(p_147216_, p_147217_);
    }

    @Nullable
    @Override
    public MobEffectInstance getEffect(Holder<MobEffect> p_21125_) {
        if (maid == null) return super.getEffect(p_21125_);
        return maid.getEffect(p_21125_);
    }

    @Override
    public Collection<MobEffectInstance> getActiveEffects() {
        if (maid == null) return super.getActiveEffects();
        return maid.getActiveEffects();
    }

    @Override
    public Map<Holder<MobEffect>, MobEffectInstance> getActiveEffectsMap() {
        if (maid == null) return super.getActiveEffectsMap();
        return maid.getActiveEffectsMap();
    }

    @Override
    public boolean hasEffect(Holder<MobEffect> p_21024_) {
        if (maid == null) return super.hasEffect(p_21024_);
        return maid.hasEffect(p_21024_);
    }

    @Override
    public ItemStack getMainHandItem() {
        if (maid == null) return ItemStack.EMPTY;
        return maid.getMainHandItem();
    }

    @Override
    public ItemStack getItemInHand(InteractionHand p_21121_) {
        if (maid == null) return super.getItemInHand(p_21121_);
        return maid.getItemInHand(p_21121_);
    }

    @Override
    public void setItemInHand(InteractionHand p_21009_, ItemStack p_21010_) {
        if (maid == null) return;
        maid.setItemInHand(p_21009_, p_21010_);
    }

    @Override
    public void setItemSlot(EquipmentSlot p_36161_, ItemStack p_36162_) {
        if (maid == null) return;
        maid.setItemSlot(p_36161_, p_36162_);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_36257_) {
        if (maid == null) return ItemStack.EMPTY;
        return maid.getItemBySlot(p_36257_);
    }

    @Override
    public boolean isEyeInFluid(TagKey<Fluid> p_204030_) {
        if (maid == null) return false;
        return maid.isEyeInFluid(p_204030_);
    }

    @Override
    public boolean onGround() {
        if (maid == null) return false;
        return maid.onGround();
    }

    @Override
    public Level level() {
        if (maid == null) return super.level();
        return maid.level();
    }

    @Override
    public ServerLevel serverLevel() {
        if (maid == null) return super.serverLevel();
        return (ServerLevel) maid.level();
    }

    @Override
    public BlockPos blockPosition() {
        if (maid == null) return BlockPos.ZERO;
        return maid.blockPosition();
    }

    @Override
    public Vec3 position() {
        if (maid == null) return Vec3.ZERO;
        return maid.position();
    }

    @Override
    public float distanceTo(Entity p_20271_) {
        if (maid == null) return super.distanceTo(p_20271_);
        return maid.distanceTo(p_20271_);
    }

    @Override
    public double distanceToSqr(double p_20276_, double p_20277_, double p_20278_) {
        if (maid == null) return super.distanceToSqr(p_20276_, p_20277_, p_20278_);
        return maid.distanceToSqr(p_20276_, p_20277_, p_20278_);
    }

    @Override
    public double distanceToSqr(Vec3 p_20239_) {
        if (maid == null) return super.distanceToSqr(p_20239_);
        return maid.distanceToSqr(p_20239_);
    }

    @Override
    public void teleportTo(double p_8969_, double p_8970_, double p_8971_) {
        if (maid == null) return;
        maid.teleportTo(p_8969_, p_8970_, p_8971_);
    }

    @Override
    public boolean teleportTo(ServerLevel p_265564_, double p_265424_, double p_265680_, double p_265312_, Set<RelativeMovement> p_265192_, float p_265059_, float p_265266_) {
        if (maid == null) return false;
        return maid.teleportTo(p_265564_, p_265424_, p_265680_, p_265312_, p_265192_, p_265059_, p_265266_);
    }

    @Override
    public void teleportRelative(double p_251611_, double p_248861_, double p_252266_) {
        if (maid == null) return;
        maid.teleportRelative(p_251611_, p_248861_, p_252266_);
    }

    @Override
    public void moveTo(double p_9171_, double p_9172_, double p_9173_) {
        if (maid == null) return;
        maid.moveTo(p_9171_, p_9172_, p_9173_);
    }

    @Override
    public ChunkPos chunkPosition() {
        if (maid == null) return new ChunkPos(0, 0);
        return maid.chunkPosition();
    }
}
