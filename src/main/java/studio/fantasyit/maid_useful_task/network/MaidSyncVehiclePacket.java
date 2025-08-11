package studio.fantasyit.maid_useful_task.network;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleManager;

public class MaidSyncVehiclePacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MaidSyncVehiclePacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(
                    MaidUsefulTask.MODID, "sync_vehicle"
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    final CompoundTag tag;
    final int maidId;

    public MaidSyncVehiclePacket(int maidId, CompoundTag tag) {
        this.maidId = maidId;
        this.tag = tag;
    }

    public static Codec<MaidSyncVehiclePacket> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("maidId").forGetter(packet -> packet.maidId),
                    CompoundTag.CODEC.fieldOf("tag").forGetter(packet -> packet.tag)
            ).apply(instance, MaidSyncVehiclePacket::new)
    );
    public static StreamCodec<RegistryFriendlyByteBuf, MaidSyncVehiclePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            t -> t.maidId,
            ByteBufCodecs.COMPOUND_TAG,
            t -> t.tag,
            MaidSyncVehiclePacket::new
    );


    public static void handle(MaidSyncVehiclePacket msg, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(msg.maidId);
        if (entity instanceof EntityMaid maid) {
            MaidVehicleManager.getControllableVehicle(maid).ifPresent(vehicle -> {
                vehicle.syncVehicleParameter(maid, msg.tag);
            });
        }
    }
}
