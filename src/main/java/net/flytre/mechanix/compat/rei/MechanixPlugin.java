package net.flytre.mechanix.compat.rei;


import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.flytre.flytre_lib.client.gui.CoordinateProvider;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.upgrade.UpgradeHandler;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeScreen;
import net.flytre.mechanix.compat.rei.categories.*;
import net.flytre.mechanix.compat.rei.displays.*;
import net.flytre.mechanix.recipe.*;
import net.flytre.mechanix.util.ItemRegistry;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.block.Blocks;
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

public class MechanixPlugin implements REIPluginV0 {

    public static final Identifier PLUGIN = new Identifier("mechanix", "rei_plugin");


    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }


    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new AlloyerRecipeCategory(RecipeRegistry.ALLOYING_RECIPE));
        recipeHelper.registerCategory(new Category1I1O<ItemProcessingRecipe<Inventory>>(RecipeRegistry.CRUSHING_RECIPE) {
            @Override
            public @NotNull EntryStack getLogo() {
                return EntryStack.create(MachineRegistry.CRUSHER.getBlock());
            }

            @Override
            public @NotNull String getCategoryName() {
                return I18n.translate("recipe.mechanix.crushing");
            }
        });
        recipeHelper.registerCategory(new SawmillRecipeCategory(RecipeRegistry.SAWING_RECIPE));

        recipeHelper.registerCategory(new Category1I1O<ItemFluidProcessingRecipe<DoubleInventory>>(RecipeRegistry.LIQUIFYING_RECIPE) {
            @Override
            public String getCategoryName() {
                return I18n.translate("recipe.mechanix.liquifying");
            }

            @Override
            public @NotNull EntryStack getLogo() {
                return EntryStack.create(MachineRegistry.LIQUIFIER.getBlock());
            }

        });
        recipeHelper.registerCategory(new Category1I1O<ItemFluidProcessingRecipe<DoubleInventory>>(RecipeRegistry.CASTING_RECIPE) {
            @Override
            public String getCategoryName() {
                return I18n.translate("recipe.mechanix.casting");
            }

            @Override
            public @NotNull EntryStack getLogo() {
                return EntryStack.create(MachineRegistry.FOUNDRY.getBlock());
            }

        });

        recipeHelper.registerCategory(new Category2I1O<DistillingRecipe>(RecipeRegistry.DISTILLING_RECIPE) {
            @NotNull
            public String getCategoryName() {
                return I18n.translate("recipe.mechanix.distilling");
            }

            @Override
            public @NotNull EntryStack getLogo() {
                return EntryStack.create(MachineRegistry.DISTILLER.getBlock());
            }

        });

        recipeHelper.registerCategory(new Category2I1O<EnchantingRecipe>(RecipeRegistry.ENCHANTING_RECIPE) {
            @NotNull
            public String getCategoryName() {
                return I18n.translate("recipe.mechanix.enchanting");
            }

            @Override
            public @NotNull EntryStack getLogo() {
                return EntryStack.create(MachineRegistry.ENCHANTER.getBlock());
            }

        });
        recipeHelper.registerCategory(new DisenchantingRecipeCategory(RecipeRegistry.DISENCHANTING_RECIPE));


        recipeHelper.registerCategory(new Category1I1O<ItemProcessingRecipe<Inventory>>(RecipeRegistry.COMPRESSING_RECIPE) {
            @Override
            public @NotNull EntryStack getLogo() {
                return EntryStack.create(MachineRegistry.PRESSURIZER.getBlock());
            }

            @Override
            public @NotNull String getCategoryName() {
                return I18n.translate("recipe.mechanix.compressing");
            }
        });

        recipeHelper.registerCategory(new CentrifugeRecipeCategory(RecipeRegistry.CENTRIFUGE_RECIPE));
        recipeHelper.registerCategory(new HydroponatorRecipeCategory(RecipeRegistry.HYDROPONATOR_RECIPE));

        recipeHelper.registerCategory(new Category1I1O<ItemProcessingRecipe<Inventory>>(RecipeRegistry.TRANSMUTING_RECIPE) {
            @Override
            public @NotNull EntryStack getLogo() {
                return EntryStack.create(MachineRegistry.TRANSMUTER.getBlock());
            }

            @Override
            public @NotNull String getCategoryName() {
                return I18n.translate("recipe.mechanix.transmuting");
            }
        });
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        recipeHelper.<AlloyingRecipe>registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.ALLOYING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.ALLOYING_RECIPE, ItemRecipeDisplay::new);
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.CRUSHING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.CRUSHING_RECIPE, SingleIODisplay::new);
        recipeHelper.<ItemProcessingRecipe<Inventory>>registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.SAWING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.SAWING_RECIPE, ItemRecipeDisplay::new);
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.LIQUIFYING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.LIQUIFYING_RECIPE, LiquifierRecipeDisplay::new);
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.CASTING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.CASTING_RECIPE, FoundryRecipeDisplay::new);
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.DISTILLING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.DISTILLING_RECIPE, DistillerRecipeDisplay::new);
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.ENCHANTING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.ENCHANTING_RECIPE, EnchanterRecipeDisplay::new);
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.DISENCHANTING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.DISENCHANTING_RECIPE, DisenchantingRecipeDisplay::new);
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.COMPRESSING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.COMPRESSING_RECIPE, SingleIODisplay::new);
        recipeHelper.<ItemProcessingRecipe<Inventory>>registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.CENTRIFUGE_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.CENTRIFUGE_RECIPE, ItemRecipeDisplay::new);
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.HYDROPONATOR_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.HYDROPONATOR_RECIPE, HydroponatorRecipeDisplay::new);
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(RecipeRegistry.TRANSMUTING_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == RecipeRegistry.TRANSMUTING_RECIPE, SingleIODisplay::new);
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

        baseBoundsHandler.registerExclusionZones(UpgradeScreen.class, () ->
        {
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            List<Rectangle> result = new ArrayList<>();
            if (currentScreen instanceof UpgradeScreen && ((UpgradeScreen) currentScreen).excludeUpgrades()) {
                UpgradeScreen coords = (UpgradeScreen) currentScreen;
                int x = coords.getX();
                int y = coords.getY();
                result.add(new Rectangle(x, y, 176, 170));
                result.add(new Rectangle(x + 176, y + 70, 65, 65));
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
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.CRUSHING_RECIPE), EntryStack.create(MachineRegistry.CRUSHER.getBlock()));
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.SAWING_RECIPE), EntryStack.create(MachineRegistry.SAWMILL.getBlock()));
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.LIQUIFYING_RECIPE), EntryStack.create(MachineRegistry.LIQUIFIER.getBlock()));
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.CASTING_RECIPE), EntryStack.create(MachineRegistry.FOUNDRY.getBlock()));
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.DISTILLING_RECIPE), EntryStack.create(MachineRegistry.DISTILLER.getBlock()));
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.ENCHANTING_RECIPE), EntryStack.create(MachineRegistry.ENCHANTER.getBlock()));
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.DISENCHANTING_RECIPE), EntryStack.create(MachineRegistry.DISENCHANTER.getBlock()));
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.COMPRESSING_RECIPE), EntryStack.create(MachineRegistry.PRESSURIZER.getBlock()));
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.CENTRIFUGE_RECIPE), EntryStack.create(MachineRegistry.CENTRIFUGE.getBlock()));
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.HYDROPONATOR_RECIPE), EntryStack.create(MachineRegistry.HYDROPONATOR.getBlock()));
        recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(RecipeRegistry.TRANSMUTING_RECIPE), EntryStack.create(MachineRegistry.TRANSMUTER.getBlock()));
        recipeHelper.registerWorkingStations(new Identifier("minecraft:plugins/smelting"), EntryStack.create(MachineRegistry.POWERED_FURNACE.getBlock()));
        recipeHelper.registerWorkingStations(new Identifier("minecraft:plugins/crafting"), EntryStack.create(MachineRegistry.CRAFTER.getBlock()));
//        recipeHelper.registerRecipeVisibilityHandler(new MechanixRenderCanceller());
    }
}
