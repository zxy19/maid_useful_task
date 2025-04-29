package studio.fantasyit.maid_useful_task.network;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleControlType;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleManager;

import java.util.function.Supplier;

public class MaidSyncVehiclePacket {
    final CompoundTag tag;
    final int maidId;

    public MaidSyncVehiclePacket(int maidId, CompoundTag tag) {
        this.maidId = maidId;
        this.tag = tag;
    }

    public MaidSyncVehiclePacket(FriendlyByteBuf buffer) {
        this.maidId = buffer.readInt();
        this.tag = buffer.readNbt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(maidId);
        buffer.writeNbt(tag);
    }

    public static void handle(MaidSyncVehiclePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        Entity entity = Network.getLocalPlayer().level().getEntity(msg.maidId);
        if (entity instanceof EntityMaid maid) {
            MaidVehicleManager.getControllableVehicle(maid).ifPresent(vehicle -> {
                vehicle.syncVehicleParameter(maid, msg.tag);
            });
        }
    }
}
