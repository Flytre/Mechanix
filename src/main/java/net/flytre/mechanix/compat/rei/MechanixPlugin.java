package net.flytre.mechanix.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.*;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.gui.PanelledScreen;
import net.flytre.mechanix.compat.rei.categories.AlloyerRecipeCategory;
import net.flytre.mechanix.compat.rei.categories.DistillerRecipeCategory;
import net.flytre.mechanix.compat.rei.categories.SawmillRecipeCategory;
import net.flytre.mechanix.compat.rei.categories.SingleIOCategory;
import net.flytre.mechanix.compat.rei.displays.*;
import net.flytre.mechanix.recipe.*;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class MechanixPlugin implements REIPluginV0 {

    public static final Identifier PLUGIN = new Identifier("mechanix", "mechanix_plugin");
    public static final Map<RecipeType<?>, ItemConvertible> iconMap = new HashMap<>();
    public static final List<RecipeType<?>> types = new ArrayList<>();


    public MechanixPlugin() {
        iconMap.put(RecipeRegistry.ALLOYING_RECIPE,MachineRegistry.ALLOYER.getBlock());
        iconMap.put(RecipeRegistry.FOUNDRY_RECIPE,MachineRegistry.FOUNDRY.getBlock());
        iconMap.put(RecipeRegistry.LIQUIFIER_RECIPE,MachineRegistry.LIQUIFIER.getBlock());
        iconMap.put(RecipeRegistry.PRESSURIZER_RECIPE,MachineRegistry.PRESSURIZER.getBlock());
        iconMap.put(RecipeRegistry.CRUSHER_RECIPE,MachineRegistry.CRUSHER.getBlock());
        iconMap.put(RecipeRegistry.DISTILLER_RECIPE,MachineRegistry.DISTILLER.getBlock());
        iconMap.put(RecipeRegistry.SAWMILL_RECIPE,MachineRegistry.SAWMILL.getBlock());
        types.addAll(Arrays.asList(RecipeRegistry.ALLOYING_RECIPE, RecipeRegistry.LIQUIFIER_RECIPE,RecipeRegistry.PRESSURIZER_RECIPE,
                RecipeRegistry.CRUSHER_RECIPE, RecipeRegistry.FOUNDRY_RECIPE, RecipeRegistry.DISTILLER_RECIPE, RecipeRegistry.SAWMILL_RECIPE));
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
        recipeHelper.registerCategory(new SingleIOCategory<CrusherRecipe>(RecipeRegistry.CRUSHER_RECIPE) {
            @Override
            public String getCategoryName() {
                return  I18n.translate("recipe.mechanix.crushing");
            }
        });
        recipeHelper.registerCategory(new SingleIOCategory<FoundryRecipe>(RecipeRegistry.FOUNDRY_RECIPE) {
            @Override
            public String getCategoryName() {
                return  I18n.translate("recipe.mechanix.casting");
            }
        });
        recipeHelper.registerCategory(new DistillerRecipeCategory(RecipeRegistry.DISTILLER_RECIPE));
        recipeHelper.registerCategory(new SawmillRecipeCategory(RecipeRegistry.SAWMILL_RECIPE));
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        Function<AlloyingRecipe, RecipeDisplay> alloyDisplay = r -> new AlloyerRecipeDisplay(r) {};
        Function<LiquifierRecipe, RecipeDisplay> liquifierDisplay = r -> new LiquifierRecipeDisplay(r) {};
        Function<ItemProcessingRecipe, RecipeDisplay> pressurizerDisplay = r -> new PressurizerRecipeDisplay(r) {};
        Function<FoundryRecipe, RecipeDisplay> foundryDisplay = r -> new FoundryRecipeDisplay(r) {};
        Function<DistillerRecipe, RecipeDisplay> distillerDisplay = r -> new DistillerRecipeDisplay(r) {};
        Function<SawmillRecipe, RecipeDisplay> sawmillDisplay = r -> new SawmillRecipeDisplay(r) {};


        recipeHelper.registerRecipes(ReiUtils.getId(RecipeRegistry.ALLOYING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.ALLOYING_RECIPE, alloyDisplay);
        recipeHelper.registerRecipes(ReiUtils.getId(RecipeRegistry.LIQUIFIER_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.LIQUIFIER_RECIPE, liquifierDisplay);
        recipeHelper.registerRecipes(ReiUtils.getId(RecipeRegistry.PRESSURIZER_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.PRESSURIZER_RECIPE, pressurizerDisplay);
        recipeHelper.registerRecipes(ReiUtils.getId(RecipeRegistry.CRUSHER_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.CRUSHER_RECIPE, pressurizerDisplay);
        recipeHelper.registerRecipes(ReiUtils.getId(RecipeRegistry.FOUNDRY_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.FOUNDRY_RECIPE, foundryDisplay);
        recipeHelper.registerRecipes(ReiUtils.getId(RecipeRegistry.DISTILLER_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.DISTILLER_RECIPE, distillerDisplay);
        recipeHelper.registerRecipes(ReiUtils.getId(RecipeRegistry.SAWMILL_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.SAWMILL_RECIPE, sawmillDisplay);

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

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        for(RecipeType<?> type : types)
            recipeHelper.registerWorkingStations(ReiUtils.getId(type), EntryStack.create(iconMap.get(type)));
    }

}
