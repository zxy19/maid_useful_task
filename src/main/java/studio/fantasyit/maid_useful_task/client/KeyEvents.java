package studio.fantasyit.maid_useful_task.client;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.network.MaidAllowHandleVehicle;

import static studio.fantasyit.maid_useful_task.client.KeyMapping.KEY_SWITCH_VEHICLE_CONTROL;

@EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class KeyEvents {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void keyInput(InputEvent.Key event) {
        while (KEY_SWITCH_VEHICLE_CONTROL.get().consumeClick()) {
            Player player = Minecraft.getInstance().player;
            if (!player.isPassenger()) return;
            EntityMaid maid = player
                    .getVehicle()
                    .getPassengers()
                    .stream()
                    .filter(entity -> entity instanceof EntityMaid)
                    .map(entity -> (EntityMaid) entity)
                    .findAny()
                    .orElse(null);
            if (maid == null) return;
            PacketDistributor.sendToServer(new MaidAllowHandleVehicle(maid));
        }
    }
}
