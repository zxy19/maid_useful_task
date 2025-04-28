package studio.fantasyit.maid_useful_task.animation;

import com.github.tartaricacid.touhoulittlemaid.api.animation.ICustomAnimation;
import com.github.tartaricacid.touhoulittlemaid.api.animation.IModelRenderer;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.AnimatedGeoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Mob;

import java.util.HashMap;

public class FlyAnimation implements ICustomAnimation<Mob> {
    @Override
    public void setupRotations(Mob entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        ICustomAnimation.super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public void setRotationAngles(Mob entity, HashMap<String, ? extends IModelRenderer> models, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ICustomAnimation.super.setRotationAngles(entity, models, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void setupGeckoRotations(Mob entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        ICustomAnimation.super.setupGeckoRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public void setGeckoRotationAngles(Mob entity, AnimatedGeoModel model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ICustomAnimation.super.setGeckoRotationAngles(entity, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}
