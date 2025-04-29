package studio.fantasyit.maid_useful_task.util;

import net.minecraft.network.chat.Component;

public class TranslateUtil {
    public static Component getBooleanTranslate(boolean b) {
        return (b ? Component.translatable("gui.maid_useful_task.yes") : Component.translatable("gui.maid_useful_task.no"));
    }
}
