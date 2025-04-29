package studio.fantasyit.maid_useful_task.data;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.resources.ResourceLocation;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MaidConfigKeys {
    record keyAndDefSupp<T>(TaskDataKey<T> key, Supplier<T> defaultValue) {
    }

    public static Map<ResourceLocation, keyAndDefSupp<?>> keys = new HashMap<>();

    public static <T> void addKey(ResourceLocation key, TaskDataKey<T> dataKey, Supplier<T> defaultValue) {
        keys.put(key, new keyAndDefSupp<>(dataKey, defaultValue));
    }

    public static <T> T getValue(EntityMaid maid, ResourceLocation key) {
        keyAndDefSupp<T> pair = (keyAndDefSupp<T>) keys.get(key);
        return maid.getOrCreateData(pair.key, pair.defaultValue.get());
    }
}
