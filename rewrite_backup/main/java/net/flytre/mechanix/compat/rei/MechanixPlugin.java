package net.flytre.mechanix.compat.rei;


import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.flytre.flytre_lib.client.gui.CoordinateProvider;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeScreen;
import net.flytre.mechanix.compat.rei.categories.AlloyerRecipeCategory;
import net.flytre.mechanix.compat.rei.categories.SingleIOCategory;
import net.flytre.mechanix.compat.rei.displays.AlloyerRecipeDisplay;
import net.flytre.mechanix.compat.rei.displays.SingleIODisplay;
import net.flytre.mechanix.recipe.ItemProcessingRecipe;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

//TODO: INCOMPLETE PORT OF CLASS
public class MechanixPlugin implements REIPluginV0 {

    public static final Identifier PLUGIN = new Identifier("mechanix", "rei_plugin");


    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }


    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new AlloyerRecipeCategory(RecipeRegistry.ALLOYING_RECIPE));
        recipeHelper.registerCategory(new SingleIOCategory<ItemProcessingRecipe<Inventory>>(RecipeRegistry.CRUSHER_RECIPE) {
            @Override
            public @NotNull EntryStack getLogo() {
                //TODO
                return EntryStack.create(MachineRegistry.CRUSHER.getBlock());
            }

            @Override
            public @NotNull String getCategoryName() {
                return I18n.translate("recipe.mechanix.crushing");
            }
        });

    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.ALLOYING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.ALLOYING_RECIPE, AlloyerRecipeDisplay::new);
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.CRUSHER_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.CRUSHER_RECIPE, SingleIODisplay::new);

    }

    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        BaseBoundsHandler baseBoundsHandler = BaseBoundsHandler.getInstance();
        baseBoundsHandler.registerExclusionZones(CoordinateProvider.class, () ->
        {
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            List<Rectangle> result = new ArrayList<>();
            if (currentScreen instanceof CoordinateProvider) {
                CoordinateProvider coords = (CoordinateProvider) currentScreen;
                int x = coords.getX();
                int y = coords.getY();
                result.add(new Rectangle(x, y, 176, 170));
                result.add(new Rectangle(x + 176, y, 65, 65));
            }
            return result;
        });

        baseBoundsHandler.registerExclusionZones(FluidPipeScreen.class, () ->
        {
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            List<Rectangle> result = new ArrayList<>();
            if (currentScreen instanceof FluidPipeScreen) {
                FluidPipeScreen pipeScreen = (FluidPipeScreen) currentScreen;
                int x = pipeScreen.getX();
                int y = pipeScreen.getY();
                result.add(new Rectangle(x, y, 176, 170));
                result.add(new Rectangle(x + 176, y, 20, 40));
            }
            return result;
        });
    }


    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.ALLOYING_RECIPE), EntryStack.create(MachineRegistry.ALLOYER.getBlock()));
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.CRUSHER_RECIPE), EntryStack.create(MachineRegistry.CRUSHER.getBlock()));
        recipeHelper.registerRecipeVisibilityHandler(new MechanixRenderCanceller());
    }
}
