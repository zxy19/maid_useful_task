package studio.fantasyit.maid_useful_task.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MaidConfigurePacket {
    public String name;
    public String value;

    public MaidConfigurePacket(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public MaidConfigurePacket(FriendlyByteBuf buffer) {
        this.name = buffer.readUtf();
        this.value = buffer.readUtf();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUtf(name);
        buffer.writeUtf(value);
    }

    public static void handle(MaidConfigurePacket msg, Supplier<NetworkEvent.Context> context) {

    }
}
