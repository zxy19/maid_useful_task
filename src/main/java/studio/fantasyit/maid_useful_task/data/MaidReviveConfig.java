package studio.fantasyit.maid_useful_task.data;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;

public class MaidReviveConfig implements TaskDataKey<MaidReviveConfig.Data> {
    public static final class Data implements IConfigSetter {
        private boolean ownerOnly;

        public Data(boolean ownerOnly) {
            this.ownerOnly = ownerOnly;
        }

        public static Data getDefault() {
            return new Data(false);
        }

        public boolean ownerOnly() {
            return ownerOnly;
        }

        public void ownerOnly(boolean ownerOnly) {
            this.ownerOnly = ownerOnly;
        }

        @Override
        public void setConfigValue(String name, String value) {
            switch (name) {
                case "ownerOnly":
                    ownerOnly = Boolean.parseBoolean(value);
                    break;
            }
        }
    }

    public static TaskDataKey<Data> KEY = null;
    public static final ResourceLocation LOCATION = new ResourceLocation(MaidUsefulTask.MODID, "revive");

    @Override
    public ResourceLocation getKey() {
        return LOCATION;
    }

    @Override
    public CompoundTag writeSaveData(Data data) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("ownerOnly", data.ownerOnly);
        return tag;
    }

    @Override
    public Data readSaveData(CompoundTag compound) {
        boolean plant = compound.getBoolean("ownerOnly");
        return new Data(plant);
    }
}
