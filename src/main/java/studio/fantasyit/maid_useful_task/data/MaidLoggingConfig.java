package studio.fantasyit.maid_useful_task.data;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;

public class MaidLoggingConfig implements TaskDataKey<MaidLoggingConfig.Data> {


    public static final class Data implements IConfigSetter {
        private boolean plant;

        public Data(boolean plant) {
            this.plant = plant;
        }

        public static Data getDefault() {
            return new Data(true);
        }

        public boolean plant() {
            return plant;
        }

        public void plant(boolean plant) {
            this.plant = plant;
        }

        @Override
        public void setConfigValue(String name, String value) {
            switch (name) {
                case "plant":
                    plant = Boolean.parseBoolean(value);
                    break;
            }
        }
    }

    public static TaskDataKey<Data> KEY = null;
    public static final ResourceLocation LOCATION = new ResourceLocation(MaidUsefulTask.MODID, "logging");

    @Override
    public ResourceLocation getKey() {
        return LOCATION;
    }

    @Override
    public CompoundTag writeSaveData(Data data) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("plant", data.plant);
        return tag;
    }

    @Override
    public Data readSaveData(CompoundTag compound) {
        boolean plant = compound.getBoolean("plant");
        return new Data(plant);
    }
}
