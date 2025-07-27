package studio.fantasyit.maid_useful_task.menu;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.task.MaidTaskConfigGui;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.MaidConfigButton;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.task.TaskConfigContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import studio.fantasyit.maid_useful_task.Config;
import studio.fantasyit.maid_useful_task.data.MaidLoggingConfig;
import studio.fantasyit.maid_useful_task.network.MaidConfigurePacket;
import studio.fantasyit.maid_useful_task.registry.GuiRegistry;
import studio.fantasyit.maid_useful_task.util.TranslateUtil;

public class MaidLoggingConfigGui extends MaidTaskConfigGui<MaidLoggingConfigGui.Container> {
    private MaidLoggingConfig.Data currentData;

    public MaidLoggingConfigGui(Container screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    public static class Container extends TaskConfigContainer {
        public Container(int id, Inventory inventory, int entityId) {
            super(GuiRegistry.MAID_LOGGING_CONFIG_GUI.get(), id, inventory, entityId);
        }
    }

    @Override
    protected void initAdditionData() {
        this.currentData = this.maid.getOrCreateData(MaidLoggingConfig.KEY, MaidLoggingConfig.Data.getDefault());
    }

    @Override
    protected void initAdditionWidgets() {
        super.initAdditionWidgets();

        int startLeft = leftPos + 87;
        int startTop = topPos + 36;
        this.addRenderableWidget(new MaidConfigButton(startLeft, startTop,
                Component.translatable("gui.maid_useful_task.logging.plant"),
                TranslateUtil.getBooleanTranslate(this.currentData.plant()),
                button -> {
                    this.currentData.plant(false);
                    button.setValue(TranslateUtil.getBooleanTranslate(false));
                    MaidConfigurePacket.send(this.maid, MaidLoggingConfig.LOCATION, "plant", "false");
                },
                button -> {
                    this.currentData.plant(true);
                    button.setValue(TranslateUtil.getBooleanTranslate(true));
                    MaidConfigurePacket.send(this.maid, MaidLoggingConfig.LOCATION, "plant", "true");
                }
        ));
        if (!Config.disableLoggingBlockUp) {
            startTop += 13;
            this.addRenderableWidget(new MaidConfigButton(startLeft, startTop,
                    Component.translatable("gui.maid_useful_task.logging.block_up"),
                    TranslateUtil.getBooleanTranslate(this.currentData.blockUp()),
                    button -> {
                        this.currentData.blockUp(false);
                        button.setValue(TranslateUtil.getBooleanTranslate(false));
                        MaidConfigurePacket.send(this.maid, MaidLoggingConfig.LOCATION, "blockUp", "false");
                    },
                    button -> {
                        this.currentData.blockUp(true);
                        button.setValue(TranslateUtil.getBooleanTranslate(true));
                        MaidConfigurePacket.send(this.maid, MaidLoggingConfig.LOCATION, "blockUp", "true");
                    }
            ));
        }
        startTop += 13;
        this.addRenderableWidget(new MaidConfigButton(startLeft, startTop,
                Component.translatable("gui.maid_useful_task.logging.skip_non_nature"),
                TranslateUtil.getBooleanTranslate(this.currentData.skipNonNature()),
                button -> {
                    this.currentData.skipNonNature(false);
                    button.setValue(TranslateUtil.getBooleanTranslate(false));
                    MaidConfigurePacket.send(this.maid, MaidLoggingConfig.LOCATION, "skipNonNature", "false");
                },
                button -> {
                    this.currentData.skipNonNature(true);
                    button.setValue(TranslateUtil.getBooleanTranslate(true));
                    MaidConfigurePacket.send(this.maid, MaidLoggingConfig.LOCATION, "skipNonNature", "true");
                }
        ));
    }
}
