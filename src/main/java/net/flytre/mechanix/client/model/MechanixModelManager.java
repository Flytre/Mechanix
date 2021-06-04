package net.flytre.mechanix.client.model;

import net.fabricmc.fabric.api.client.model.ExtraModelProvider;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MechanixModelManager implements ModelVariantProvider, ExtraModelProvider {

    @Override
    public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
        if (!modelId.getNamespace().equals("mechanix")) {
            return null;
        }
        String path = modelId.getPath();
        String variant = modelId.getVariant();
        if (path.endsWith("tank") && variant.equals("inventory")) {
            return new FluidTankBakedModel();
        }
        return null;
    }

    @Override
    public void provideExtraModels(ResourceManager manager, Consumer<Identifier> out) {
    }
}
