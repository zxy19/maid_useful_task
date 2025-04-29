package studio.fantasyit.maid_useful_task.network;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import studio.fantasyit.maid_useful_task.data.IConfigSetter;
import studio.fantasyit.maid_useful_task.data.MaidConfigKeys;

import java.util.function.Supplier;

public class MaidConfigurePacket {
    final public int maidId;
    final public String name;
    final public String value;
    final public ResourceLocation key;

    public MaidConfigurePacket(int maidId, ResourceLocation key, String name, String value) {
        this.maidId = maidId;
        this.key = key;
        this.name = name;
        this.value = value;
    }

    public MaidConfigurePacket(FriendlyByteBuf buffer) {
        this.maidId = buffer.readInt();
        this.key = ResourceLocation.tryParse(buffer.readUtf());
        this.name = buffer.readUtf();
        this.value = buffer.readUtf();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(maidId);
        buffer.writeUtf(key.toString());
        buffer.writeUtf(name);
        buffer.writeUtf(value);
    }

    public static void handle(MaidConfigurePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        @Nullable ServerPlayer sender = context.getSender();
        if (sender != null) {
            if (sender.level().getEntity(msg.maidId) instanceof EntityMaid entityMaid) {
                if (MaidConfigKeys.getValue(entityMaid, msg.key) instanceof IConfigSetter ics) {
                    ics.setConfigValue(msg.name, msg.value);
                }
            }
        }
    }

    public static void send(EntityMaid maid, ResourceLocation key, String name, String value) {
        Network.INSTANCE.sendToServer(new MaidConfigurePacket(maid.getId(), key, name, value));
    }
}
