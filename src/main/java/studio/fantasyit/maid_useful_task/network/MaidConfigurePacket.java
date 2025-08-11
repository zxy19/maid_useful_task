package studio.fantasyit.maid_useful_task.network;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.data.IConfigSetter;
import studio.fantasyit.maid_useful_task.data.MaidConfigKeys;

public class MaidConfigurePacket implements CustomPacketPayload {
    final public int maidId;
    final public String name;
    final public String value;
    final public ResourceLocation key;
    public static final CustomPacketPayload.Type<MaidConfigurePacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(
                    MaidUsefulTask.MODID, "maid_config"
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public MaidConfigurePacket(int maidId, ResourceLocation key, String name, String value) {
        this.maidId = maidId;
        this.key = key;
        this.name = name;
        this.value = value;
    }

    public static Codec<MaidConfigurePacket> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("maidId").forGetter(packet -> packet.maidId),
                    ResourceLocation.CODEC.fieldOf("key").forGetter(packet -> packet.key),
                    Codec.STRING.fieldOf("name").forGetter(packet -> packet.name),
                    Codec.STRING.fieldOf("value").forGetter(packet -> packet.value)
            ).apply(instance, MaidConfigurePacket::new)
    );
    public static StreamCodec<ByteBuf, MaidConfigurePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            t -> t.maidId,
            ResourceLocation.STREAM_CODEC,
            t -> t.key,
            ByteBufCodecs.STRING_UTF8,
            t -> t.name,
            ByteBufCodecs.STRING_UTF8,
            t -> t.value,
            MaidConfigurePacket::new
    );

    public static void send(EntityMaid maid, ResourceLocation key, String name, String value) {
        PacketDistributor.sendToServer(new MaidConfigurePacket(maid.getId(), key, name, value));
    }


    public static void handle(MaidConfigurePacket msg, IPayloadContext context) {
        @Nullable ServerPlayer sender = (ServerPlayer) context.player();
        if (sender.level().getEntity(msg.maidId) instanceof EntityMaid entityMaid) {
            if (MaidConfigKeys.getValue(entityMaid, msg.key) instanceof IConfigSetter ics) {
                ics.setConfigValue(msg.name, msg.value);
            }
        }
    }
}
