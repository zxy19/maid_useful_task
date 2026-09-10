package studio.fantasyit.maid_useful_task.data;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import studio.fantasyit.maid_useful_task.Config;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MaidReviveGlobalData {
    private static final Map<UUID, UUID> playerIsRescuingByMaid = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> playerHasStartBeingRescued = new ConcurrentHashMap<>();

    public static UUID getRescuingMaid(UUID playerId) {
        return playerIsRescuingByMaid.get(playerId);
    }

    public static void setRescuingMaid(UUID playerId, UUID maidId) {
        playerIsRescuingByMaid.put(playerId, maidId);
        playerHasStartBeingRescued.put(playerId, false);
    }

    public static void clearRescuingMaid(UUID uuid) {
        playerIsRescuingByMaid.remove(uuid);
        // 原实现写入 false 而非移除，条目会随登录过的玩家数无限增长
        playerHasStartBeingRescued.remove(uuid);
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
