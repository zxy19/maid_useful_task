package studio.fantasyit.maid_useful_task.memory;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

public class TaskRateLimitToken {
    public enum Level {

        IDLE(0),
        L1(1),
        L2(2),
        L3(3),
        L4(4),
        L5(5);
        private final int level;

        Level(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    int currentLevel = 0;
    int cooldown = 100;

    public void tick(EntityMaid maid) {
        if (MemoryUtil.getTargetPos(maid) != null) {
            currentLevel = 0;
            cooldown = 20;
            return;
        }
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        currentLevel++;
        if (currentLevel > 5) {
            currentLevel = 0;
            cooldown = 100;
        }
    }

    public boolean isFor(Level level) {
        return currentLevel == level.getLevel();
    }
}
