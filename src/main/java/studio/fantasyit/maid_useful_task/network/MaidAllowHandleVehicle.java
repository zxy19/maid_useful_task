package studio.fantasyit.maid_useful_task.network;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;
import studio.fantasyit.maid_useful_task.vehicle.MaidVehicleControlType;

import java.util.function.Supplier;

public class MaidAllowHandleVehicle {
    final int maidId;

    public MaidAllowHandleVehicle(EntityMaid maid) {
        this.maidId = maid.getId();
    }

    public MaidAllowHandleVehicle(FriendlyByteBuf buffer) {
        maidId = buffer.readInt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(maidId);
    }

    public static void handle(MaidAllowHandleVehicle msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer sender = context.getSender();
        Entity entity = sender.level().getEntity(msg.maidId);
        if (entity instanceof EntityMaid maid) {
            MaidVehicleControlType[] values = MaidVehicleControlType.values();
            MaidVehicleControlType allowMode = values[(MemoryUtil.getAllowHandleVehicle(maid).ordinal() + 1) % values.length];
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
