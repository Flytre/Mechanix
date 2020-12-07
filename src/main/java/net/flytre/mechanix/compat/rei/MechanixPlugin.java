package net.flytre.mechanix.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.gui.PanelledScreen;
import net.flytre.mechanix.block.alloyer.AlloyingRecipe;
import net.flytre.mechanix.block.foundry.FoundryRecipe;
import net.flytre.mechanix.block.liquifier.LiquifierRecipe;
import net.flytre.mechanix.block.pressurizer.PressurizerRecipe;
import net.flytre.mechanix.compat.rei.categories.AlloyerRecipeCategory;
import net.flytre.mechanix.compat.rei.categories.SingleIOCategory;
import net.flytre.mechanix.compat.rei.displays.AlloyerRecipeDisplay;
import net.flytre.mechanix.compat.rei.displays.FoundryRecipeDisplay;
import net.flytre.mechanix.compat.rei.displays.LiquifierRecipeDisplay;
import net.flytre.mechanix.compat.rei.displays.PressurizerRecipeDisplay;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class MechanixPlugin implements REIPluginV0 {

    public static final Identifier PLUGIN = new Identifier("mechanix", "mechanix_plugin");
    public static final Map<RecipeType<?>, ItemConvertible> iconMap = new HashMap<>();

    public MechanixPlugin() {
        iconMap.put(RecipeRegistry.ALLOYING_RECIPE,MachineRegistry.ALLOYER.getBlock());
        iconMap.put(RecipeRegistry.FOUNDRY_RECIPE,MachineRegistry.FOUNDRY.getBlock());
        iconMap.put(RecipeRegistry.LIQUIFIER_RECIPE,MachineRegistry.LIQUIFIER.getBlock());
        iconMap.put(RecipeRegistry.PRESSURIZER_RECIPE,MachineRegistry.PRESSURIZER.getBlock());
    }

    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }


    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new AlloyerRecipeCategory(RecipeRegistry.ALLOYING_RECIPE));
        recipeHelper.registerCategory(new SingleIOCategory<LiquifierRecipe>(RecipeRegistry.LIQUIFIER_RECIPE) {
            @Override
            public String getCategoryName() {
                return  I18n.translate("recipe.mechanix.liquifying");
            }
        });
        recipeHelper.registerCategory(new SingleIOCategory<PressurizerRecipe>(RecipeRegistry.PRESSURIZER_RECIPE) {
            @Override
            public String getCategoryName() {
                return  I18n.translate("recipe.mechanix.compressing");
            }
        });
        recipeHelper.registerCategory(new SingleIOCategory<FoundryRecipe>(RecipeRegistry.FOUNDRY_RECIPE) {
            @Override
            public String getCategoryName() {
                return  I18n.translate("recipe.mechanix.casting");
            }
        });
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        Function<AlloyingRecipe, RecipeDisplay> alloyDisplay = r -> new AlloyerRecipeDisplay(r) {};
        Function<LiquifierRecipe, RecipeDisplay> liquifierDisplay = r -> new LiquifierRecipeDisplay(r) {};
        Function<PressurizerRecipe, RecipeDisplay> pressurizerDisplay = r -> new PressurizerRecipeDisplay(r) {};
        Function<FoundryRecipe, RecipeDisplay> foundryDisplay = r -> new FoundryRecipeDisplay(r) {};


        recipeHelper.registerRecipes(ReiUtils.getId(RecipeRegistry.ALLOYING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.ALLOYING_RECIPE, alloyDisplay);
        recipeHelper.registerRecipes(ReiUtils.getId(RecipeRegistry.LIQUIFIER_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.LIQUIFIER_RECIPE, liquifierDisplay);
        recipeHelper.registerRecipes(ReiUtils.getId(RecipeRegistry.PRESSURIZER_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.PRESSURIZER_RECIPE, pressurizerDisplay);
        recipeHelper.registerRecipes(ReiUtils.getId(RecipeRegistry.FOUNDRY_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.FOUNDRY_RECIPE, foundryDisplay);

    }

    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        BaseBoundsHandler baseBoundsHandler = BaseBoundsHandler.getInstance();
        baseBoundsHandler.registerExclusionZones(PanelledScreen.class,() ->
        {
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            List<Rectangle> result = new ArrayList<>();
            if(currentScreen instanceof PanelledScreen<?>) {
                PanelledScreen<?> actualScreen = (PanelledScreen<?>) currentScreen;
                int x = actualScreen.getX();
                int y = actualScreen.getY();
                result.add(new Rectangle(x,y,176,170));
                result.add(new Rectangle(x + 176,y,65,65));
            }
            return result;
        });
    }

}
