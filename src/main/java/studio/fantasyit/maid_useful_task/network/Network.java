package studio.fantasyit.maid_useful_task.network;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;

public class Network {
    private static final String PROTOCOL_VERSION = "1";

    private static void registerMessage(PayloadRegistrar registrar) {
        registrar.playToServer(
                MaidConfigurePacket.TYPE,
                MaidConfigurePacket.STREAM_CODEC,
                MaidConfigurePacket::handle
        );
        registrar.playToServer(
                MaidAllowHandleVehicle.TYPE,
                MaidAllowHandleVehicle.STREAM_CODEC,
                MaidAllowHandleVehicle::handle
        );
        registrar.playToClient(
                MaidSyncVehiclePacket.TYPE,
                MaidSyncVehiclePacket.STREAM_CODEC,
                MaidSyncVehiclePacket::handle
        );
    }

    @EventBusSubscriber(modid = MaidUsefulTask.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class Event {
        @net.neoforged.bus.api.SubscribeEvent
        public static void regis(RegisterPayloadHandlersEvent event) {
            registerMessage(event.registrar(PROTOCOL_VERSION));
        }
    }
}
