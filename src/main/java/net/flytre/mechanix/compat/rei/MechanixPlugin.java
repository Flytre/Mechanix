package net.flytre.mechanix.compat.rei;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MechanixPlugin implements REIPluginV0 {
    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier("mechanix:test");
    }


    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerWorkingStations(new Identifier("mechanix:test2"), EntryStack.create(MachineRegistry.ALLOYER.getBlock()));
    }

}
