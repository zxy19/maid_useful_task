package studio.fantasyit.maid_useful_task.network;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import studio.fantasyit.maid_useful_task.Config;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleControlType;

public class MaidAllowHandleVehicle implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MaidAllowHandleVehicle> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(
                    MaidUsefulTask.MODID, "allow_handle_vehicle"
            )
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    final int maidId;

    public MaidAllowHandleVehicle(EntityMaid maid) {
        this.maidId = maid.getId();
    }

    public MaidAllowHandleVehicle(int maidId) {
        this.maidId = maidId;
    }


    public static Codec<MaidAllowHandleVehicle> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("maidId").forGetter(packet -> packet.maidId)
            ).apply(instance, MaidAllowHandleVehicle::new)
    );
    public static StreamCodec<ByteBuf, MaidAllowHandleVehicle> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            t -> t.maidId,
            MaidAllowHandleVehicle::new
    );

    public static void handle(MaidAllowHandleVehicle msg, IPayloadContext context) {
        ServerPlayer sender = (ServerPlayer) context.player();
        Entity entity = sender.level().getEntity(msg.maidId);
        if (entity instanceof EntityMaid maid) {
            MaidVehicleControlType[] values = MaidVehicleControlType.values();
            MaidVehicleControlType allowMode = values[(MemoryUtil.getAllowHandleVehicle(maid).ordinal() + 1) % values.length];
            while ((allowMode == MaidVehicleControlType.FULL && !Config.enableVehicleControlFull)
                    || (allowMode == MaidVehicleControlType.ROT_ONLY && !Config.enableVehicleControlRotate)) {
                allowMode = values[(allowMode.ordinal() + 1) % values.length];
            }
            MemoryUtil.setAllowHandleVehicle(maid, allowMode);
            Component component = switch (allowMode) {
                case NONE -> Component.translatable("maid_useful_task.allow_handle_vehicle.none");
                case ROT_ONLY -> Component.translatable("maid_useful_task.allow_handle_vehicle.rot_only");
                case FULL -> Component.translatable("maid_useful_task.allow_handle_vehicle.full");
            };
            sender.sendSystemMessage(component);
        }
    }
}
