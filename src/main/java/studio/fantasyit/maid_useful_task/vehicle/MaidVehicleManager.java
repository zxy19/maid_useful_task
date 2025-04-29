package studio.fantasyit.maid_useful_task.vehicle;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.network.PacketDistributor;
import studio.fantasyit.maid_useful_task.network.MaidSyncVehiclePacket;
import studio.fantasyit.maid_useful_task.network.Network;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaidVehicleManager {
    public static List<AbstractMaidControllableVehicle> controllableVehicles = new ArrayList<>();

    public static void register() {
        controllableVehicles.add(new VehicleBroom());
    }

    public static void addControllableVehicle(AbstractMaidControllableVehicle vehicle) {
        controllableVehicles.add(vehicle);
    }

    public static Optional<AbstractMaidControllableVehicle> getControllableVehicle(EntityMaid maid) {
        for (AbstractMaidControllableVehicle vehicle : controllableVehicles) {
            if (vehicle.isMaidOnThisVehicle(maid)) return Optional.of(vehicle);
        }
        return Optional.empty();
    }

    public static void syncVehicleParameter(EntityMaid maid) {
        getControllableVehicle(maid).ifPresent(vehicle -> {
            CompoundTag syncVehicleParameter = vehicle.getSyncVehicleParameter(maid);
            if (syncVehicleParameter != null) {
                Network.INSTANCE.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> maid),
                        new MaidSyncVehiclePacket(maid.getId(), syncVehicleParameter)
                );
            }
        });
    }
}
