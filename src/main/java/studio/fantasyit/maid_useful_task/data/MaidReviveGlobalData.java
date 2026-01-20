package studio.fantasyit.maid_useful_task.data;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import studio.fantasyit.maid_useful_task.Config;

import java.util.HashMap;
import java.util.UUID;

public class MaidReviveGlobalData {
    private static final HashMap<UUID, UUID> playerIsRescuingByMaid = new HashMap<>();
    private static final HashMap<UUID, Boolean> playerHasStartBeingRescued = new HashMap<>();

    public static UUID getRescuingMaid(UUID playerId) {
        return playerIsRescuingByMaid.get(playerId);
    }

    public static void setRescuingMaid(UUID playerId, UUID maidId) {
        playerIsRescuingByMaid.put(playerId, maidId);
        playerHasStartBeingRescued.put(playerId, false);
    }

    public static void clearRescuingMaid(UUID uuid) {
        playerIsRescuingByMaid.remove(uuid);
        playerHasStartBeingRescued.put(uuid, false);
    }

    public static boolean hasStartRescue(UUID uuid) {
        return playerHasStartBeingRescued.getOrDefault(uuid, false);
    }

    public static void startRescue(UUID uuid) {
        playerHasStartBeingRescued.put(uuid, true);
    }

    public static boolean hasRescuingMaid(UUID uuid) {
        return playerIsRescuingByMaid.containsKey(uuid);
    }

    public static boolean isBeingRescueByOtherMaid(UUID uuid, UUID currentMaid) {
        return hasRescuingMaid(uuid) && !getRescuingMaid(uuid).equals(currentMaid) && hasStartRescue(uuid);
    }

    public static boolean checkRescuingMaid(UUID playerId, EntityMaid incomingMaid, ServerLevel level) {
        if (hasRescuingMaid(playerId) && level.getEntity(getRescuingMaid(playerId)) instanceof EntityMaid maid && maid.isAlive()) {
            return getRescuePriority(incomingMaid.getTask().getUid()) < getRescuePriority(maid.getTask().getUid());
        }
        return true;
    }

    public static int getRescuePriority(ResourceLocation jobId) {
        return Config.passiveReviveJobPriority.getOrDefault(jobId, 3);
    }
}
