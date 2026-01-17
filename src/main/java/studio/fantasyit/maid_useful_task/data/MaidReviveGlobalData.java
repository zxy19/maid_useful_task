package studio.fantasyit.maid_useful_task.data;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.UUID;

public class MaidReviveGlobalData {
    private static HashMap<UUID, UUID> playerIsRescuingByMaid = new HashMap<>();

    public static UUID getRescuingMaid(UUID playerId) {
        return playerIsRescuingByMaid.get(playerId);
    }

    public static void setRescuingMaid(UUID playerId, UUID maidId) {
        playerIsRescuingByMaid.put(playerId, maidId);
    }

    public static void clearRescuingMaid(UUID uuid) {
        playerIsRescuingByMaid.remove(uuid);
    }

    public static boolean hasRescuingMaid(UUID uuid) {
        return playerIsRescuingByMaid.containsKey(uuid);
    }

    public static boolean checkRescuingMaid(UUID playerId, ServerLevel level) {
        return hasRescuingMaid(playerId) && level.getEntity(getRescuingMaid(playerId)) instanceof EntityMaid maid && maid.isAlive();
    }
}
