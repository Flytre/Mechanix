package net.flytre.mechanix.recipe;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.flytre.mechanix.api.recipe.RecipeUtils;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class MechanixReloader implements SimpleSynchronousResourceReloadListener {

    public static final MechanixReloader INSTANCE = new MechanixReloader();

    private MechanixReloader() {}

    @Override
    public Identifier getFabricId() {
        return new Identifier("mechanix:reload");
    }

    @Override
    public void apply(ResourceManager manager) {
        RecipeUtils.clearCache();
    }
}
