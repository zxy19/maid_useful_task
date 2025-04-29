package studio.fantasyit.maid_useful_task.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;

@Mod.EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyMapping {

    public static final Lazy<net.minecraft.client.KeyMapping> KEY_SWITCH_VEHICLE_CONTROL = Lazy.of(() -> new net.minecraft.client.KeyMapping(
            "key.maid_useful_tasks.switch_vehicle_control",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.maid_useful_tasks.categories.main"
    ));

    @SubscribeEvent
    public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(KEY_SWITCH_VEHICLE_CONTROL.get());
    }
}
