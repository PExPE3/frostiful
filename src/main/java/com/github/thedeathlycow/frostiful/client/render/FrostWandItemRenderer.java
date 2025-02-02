package com.github.thedeathlycow.frostiful.client.render;

import com.github.thedeathlycow.frostiful.client.model.FrostWandItemModel;
import com.github.thedeathlycow.frostiful.init.Frostiful;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FrostWandItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, SimpleSynchronousResourceReloadListener {

    public static final Identifier ID = new Identifier(Frostiful.MODID, "frost_wand_renderer");
    public static final ModelIdentifier INVENTORY_MODEL_ID = new ModelIdentifier(new Identifier(Frostiful.MODID, "frost_wand_in_inventory"), "inventory");

    private final EntityModelLayer modelLayer;
    private FrostWandItemModel model;
    private ItemRenderer itemRenderer;
    private BakedModel inventoryModel;

    public FrostWandItemRenderer(EntityModelLayer modelLayer) {
        this.modelLayer = modelLayer;
    }

    /**
     * Code largely based on similar functionality in the mod 'Impaled':
     * https://github.com/Ladysnake/Impaled/
     *
     * @param stack           the rendered item stack
     * @param mode            the model transformation mode
     * @param matrices        the matrix stack
     * @param vertexConsumers the vertex consumer provider
     * @param light           packed lightmap coordinates
     * @param overlay         the overlay UV passed to {@link net.minecraft.client.render.VertexConsumer#overlay(int)}
     */
    @Override
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean renderAsItem = mode == ModelTransformation.Mode.GUI
                || mode == ModelTransformation.Mode.GROUND
                || mode == ModelTransformation.Mode.FIXED;


        if (renderAsItem) {
            matrices.pop();
            matrices.push();
            itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, this.inventoryModel);
        } else {
            matrices.push();
            matrices.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(
                    vertexConsumers, this.model.getLayer(FrostWandItemModel.TEXTURE), false, stack.hasGlint()
            );
            this.model.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrices.pop();
        }
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        MinecraftClient client = MinecraftClient.getInstance();
        this.model = new FrostWandItemModel(client.getEntityModelLoader().getModelPart(this.modelLayer));
        this.itemRenderer = client.getItemRenderer();
        this.inventoryModel = client.getBakedModelManager().getModel(INVENTORY_MODEL_ID);
    }
}
