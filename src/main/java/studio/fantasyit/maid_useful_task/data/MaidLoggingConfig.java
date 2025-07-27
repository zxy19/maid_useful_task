package studio.fantasyit.maid_useful_task.data;

import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import studio.fantasyit.maid_useful_task.MaidUsefulTask;

public class MaidLoggingConfig implements TaskDataKey<MaidLoggingConfig.Data> {


    public static Data get(EntityMaid maid) {
        return maid.getOrCreateData(KEY, Data.getDefault());
    }

    public static final class Data implements IConfigSetter {
        private boolean plant;
        private boolean blockUp;
        private boolean skipNonNature;

        public Data(boolean plant, boolean blockUp, boolean skipNonNature) {
            this.plant = plant;
            this.blockUp = blockUp;
            this.skipNonNature = skipNonNature;
        }

        public static Data getDefault() {
            return new Data(true, true, true);
        }

        public boolean plant() {
            return plant;
        }

        public void plant(boolean plant) {
            this.plant = plant;
        }

        public boolean blockUp() {
            return blockUp;
        }

        public void blockUp(boolean blockUp) {
            this.blockUp = blockUp;
        }

        public boolean skipNonNature() {
            return skipNonNature;
        }

        public void skipNonNature(boolean skipNonNature) {
            this.skipNonNature = skipNonNature;
        }

        @Override
        public void setConfigValue(String name, String value) {
            switch (name) {
                case "plant":
                    plant = Boolean.parseBoolean(value);
                    break;
                case "blockUp":
                    blockUp = Boolean.parseBoolean(value);
                    break;
                case "skipNonNature":
                    skipNonNature = Boolean.parseBoolean(value);
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
        tag.putBoolean("blockUp", data.blockUp);
        tag.putBoolean("skipNonNature", data.skipNonNature);
        return tag;
    }

    @Override
    public Data readSaveData(CompoundTag compound) {
        boolean plant = compound.getBoolean("plant");
        boolean blockUp = compound.getBoolean("blockUp");
        boolean skipNonNature = compound.getBoolean("skipNonNature");
        return new Data(plant, blockUp, skipNonNature);
    }
}
