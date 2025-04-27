package studio.fantasyit.maid_useful_task.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import studio.fantasyit.maid_useful_task.memory.CurrentWork;
import studio.fantasyit.maid_useful_task.util.Conditions;
import studio.fantasyit.maid_useful_task.util.MemoryUtil;

@Mixin(EntityMaid.class)
public abstract class MaidCheckPickupItem {
    @Inject(method = "pickupItem", at = @At("HEAD"), cancellable = true, remap = false)
    public void maid_storage_manager$pickupItem(ItemEntity entityItem, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (!Conditions.isCurrent((EntityMaid) (Object) this, CurrentWork.IDLE)) {
            cir.setReturnValue(false);
        }
    }
}