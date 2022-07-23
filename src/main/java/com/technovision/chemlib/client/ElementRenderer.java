package com.technovision.chemlib.client;

import com.technovision.chemlib.ChemLib;
import com.technovision.chemlib.common.items.ElementItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class ElementRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    public static final ModelIdentifier SOLID_MODEL_LOCATION = new ModelIdentifier(new Identifier(ChemLib.MOD_ID, "element_solid_model"), "inventory");
    public static final ModelIdentifier LIQUID_MODEL_LOCATION = new ModelIdentifier(new Identifier(ChemLib.MOD_ID, "element_liquid_model"), "inventory");
    public static final ModelIdentifier GAS_MODEL_LOCATION = new ModelIdentifier(new Identifier(ChemLib.MOD_ID, "element_gas_model"), "inventory");

    @Override
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean gui = mode == ModelTransformation.Mode.GUI;
        boolean frame = mode == ModelTransformation.Mode.FIXED;

        ModelIdentifier elementModel;
        switch(((ElementItem) stack.getItem()).getMatterState()) {
            case LIQUID -> elementModel = LIQUID_MODEL_LOCATION;
            case GAS -> elementModel = GAS_MODEL_LOCATION;
            default -> elementModel = SOLID_MODEL_LOCATION;
        }
        BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModels().getModelManager().getModel(elementModel);
        System.out.println(model.getParticleSprite());

        matrices.push();
        matrices.translate(0.5D, 0.5D, 0D);
        if (gui) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        matrices.push();

        switch (mode) {
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                matrices.translate(0, -0.2D, 0.45D);
            }
            case FIRST_PERSON_LEFT_HAND -> {
                matrices.translate(-0.025D, -0.025D, 0.75D);
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(25));
                matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(45));
                matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(80));
            }
            case FIRST_PERSON_RIGHT_HAND -> {
                matrices.translate(-0.20D, -0.05D, 0.75D);
                matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(25));
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(45));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(100));
                matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(45));
            }
            case HEAD -> {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                matrices.translate(0, -0.75D, -0.75D);
            }
            case GROUND -> {
                matrices.translate(0, -0.25D, 0.5D);
                matrices.scale(1.5F, 1.5F, 1.5F);
            }
            case FIXED -> {
                matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(180));
                matrices.translate(0, 0, -0.5D);
            }
        }
        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack,
                mode,
                false,
                matrices,
                vertexConsumers,
                gui ? 0xF000F0 : light,
                gui ? OverlayTexture.DEFAULT_UV : overlay,
                model);
        matrices.pop();

        if (gui || frame) {
            matrices.push();
            matrices.multiply(Vec3f.NEGATIVE_X.getRadialQuaternion(180));
            matrices.translate(-0.16D, 0, -0.55D);
            matrices.scale(0.05F, 0.08F, 0.08F);

            if (frame) {
                matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(180));
                matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(53));
                matrices.translate(-8D, -1D, 1.7D);
                matrices.scale(1F, 0.65F, 1F);
            }
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, ((ElementItem) stack.getItem()).getAbbreviation(), -5, 0, 0xFFFFFF);
            matrices.pop();
        }
        matrices.pop();
    }
}
