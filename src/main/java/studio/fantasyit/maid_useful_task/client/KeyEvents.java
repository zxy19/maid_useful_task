package studio.fantasyit.maid_useful_task.client;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;
import studio.fantasyit.maid_useful_task.network.MaidAllowHandleVehicle;
import studio.fantasyit.maid_useful_task.network.Network;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import static studio.fantasyit.maid_useful_task.client.KeyMapping.KEY_SWITCH_VEHICLE_CONTROL;

@Mod.EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
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
            Network.INSTANCE.sendToServer(new MaidAllowHandleVehicle(maid));
        }
    }
}
