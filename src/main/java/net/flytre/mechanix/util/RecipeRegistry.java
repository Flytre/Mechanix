package net.flytre.mechanix.util;

import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.recipe.*;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RecipeRegistry {


    public static ItemProcessingRecipeSerializer<Inventory, AlloyingRecipe> ALLOYING_SERIALIZER;
    public static RecipeType<AlloyingRecipe> ALLOYING_RECIPE;
    public static ItemProcessingRecipeSerializer<Inventory, ItemProcessingRecipe<Inventory>> CRUSHING_SERIALIZER;
    public static RecipeType<ItemProcessingRecipe<Inventory>> CRUSHING_RECIPE;
    public static ItemProcessingRecipeSerializer<Inventory, ItemProcessingRecipe<Inventory>> SAWING_SERIALIZER;
    public static RecipeType<ItemProcessingRecipe<Inventory>> SAWING_RECIPE;
    public static ItemFluidProcessingRecipeSerializer<DoubleInventory, ItemFluidProcessingRecipe<DoubleInventory>> LIQUIFYING_SERIALIZER;
    public static RecipeType<ItemFluidProcessingRecipe<DoubleInventory>> LIQUIFYING_RECIPE;
    public static ItemFluidProcessingRecipeSerializer<DoubleInventory, ItemFluidProcessingRecipe<DoubleInventory>> CASTING_SERIALIZER;
    public static RecipeType<ItemFluidProcessingRecipe<DoubleInventory>> CASTING_RECIPE;
    public static ItemFluidProcessingRecipeSerializer<DoubleInventory, ItemFluidProcessingRecipe<DoubleInventory>> DISTILLING_SERIALIZER;
    public static RecipeType<DistillingRecipe> DISTILLING_RECIPE;
    public static EnchanterRecipeSerializer<EnchantingRecipe> ENCHANTING_SERIALIZER;
    public static RecipeType<EnchantingRecipe> ENCHANTING_RECIPE;
    public static EnchanterRecipeSerializer<DisenchantingRecipe> DISENCHANTING_SERIALIZER;
    public static RecipeType<DisenchantingRecipe> DISENCHANTING_RECIPE;
    public static ItemProcessingRecipeSerializer<Inventory, ItemProcessingRecipe<Inventory>> COMPRESSING_SERIALIZER;
    public static RecipeType<ItemProcessingRecipe<Inventory>> COMPRESSING_RECIPE;
    public static ItemProcessingRecipeSerializer<Inventory, ItemProcessingRecipe<Inventory>> CENTRIFUGE_SERIALIZER;
    public static RecipeType<ItemProcessingRecipe<Inventory>> CENTRIFUGE_RECIPE;
    public static ItemFluidProcessingRecipeSerializer<DoubleInventory, ItemFluidProcessingRecipe<DoubleInventory>> HYDROPONATOR_SERIALIZER;
    public static RecipeType<HydroponatorRecipe> HYDROPONATOR_RECIPE;
    public static FakeTransmutingSerializer TRANSMUTING_SERIALIZER_FAKE;
    public static RecipeType<FakeTransmutingRecipe> TRANSMUTING_RECIPE_FAKE;
    public static ItemProcessingRecipeSerializer<Inventory, ItemProcessingRecipe<Inventory>> TRANSMUTING_SERIALIZER;
    public static RecipeType<ItemProcessingRecipe<Inventory>> TRANSMUTING_RECIPE;


    public static void init() {
        ALLOYING_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:alloying"), new MechanixRecipeType<AlloyingRecipe>() {
            public String toString() {
                return "mechanix:alloying";
            }
        });
        ALLOYING_SERIALIZER = RecipeSerializer.register("mechanix:alloying", new ItemProcessingRecipeSerializer<>(AlloyingRecipe::new));

        CRUSHING_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:crushing"), new MechanixRecipeType<ItemProcessingRecipe<Inventory>>() {
            public String toString() {
                return "mechanix:crushing";
            }
        });
        CRUSHING_SERIALIZER = RecipeSerializer.register("mechanix:crushing", new ItemProcessingRecipeSerializer<>((id, inputs, outputs, craftTime, upgrades) -> new ItemProcessingRecipe<Inventory>(id, inputs, outputs, craftTime, upgrades) {

            @Override
            public RecipeSerializer<?> getSerializer() {
                return RecipeRegistry.CRUSHING_SERIALIZER;
            }

            @Override
            public RecipeType<?> getType() {
                return RecipeRegistry.CRUSHING_RECIPE;
            }
        }));

        SAWING_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:sawing"), new MechanixRecipeType<ItemProcessingRecipe<Inventory>>() {
            public String toString() {
                return "mechanix:sawing";
            }
        });

        SAWING_SERIALIZER = RecipeSerializer.register("mechanix:sawing", new ItemProcessingRecipeSerializer<>((id, inputs, outputs, craftTime, upgrades) -> new ItemProcessingRecipe<Inventory>(id, inputs, outputs, craftTime, upgrades) {

            @Override
            public RecipeSerializer<?> getSerializer() {
                return RecipeRegistry.SAWING_SERIALIZER;
            }

            @Override
            public RecipeType<?> getType() {
                return RecipeRegistry.SAWING_RECIPE;
            }

            @Override
            public int outputSlotStart() {
                return 1;
            }
        }));

        LIQUIFYING_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:liquifying"), new MechanixRecipeType<ItemFluidProcessingRecipe<DoubleInventory>>() {
            public String toString() {
                return "mechanix:liquifying";
            }
        });

        LIQUIFYING_SERIALIZER = RecipeSerializer.register("mechanix:liquifying", new ItemFluidProcessingRecipeSerializer<>(( id,  inputs, outputs,  fluidInputs,  fluidOutputs, craftTime, upgrades) -> new ItemFluidProcessingRecipe<DoubleInventory>(id, inputs, outputs, fluidInputs, fluidOutputs, craftTime, upgrades) {

            @Override
            public RecipeSerializer<?> getSerializer() {
                return RecipeRegistry.LIQUIFYING_SERIALIZER;
            }

            @Override
            public RecipeType<?> getType() {
                return RecipeRegistry.LIQUIFYING_RECIPE;
            }

            @Override
            public int fluidOutputStartSlot() {
                return 0;
            }
        }));

        CASTING_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:casting"), new MechanixRecipeType<ItemFluidProcessingRecipe<DoubleInventory>>() {
            public String toString() {
                return "mechanix:casting";
            }
        });

        CASTING_SERIALIZER = RecipeSerializer.register("mechanix:casting", new ItemFluidProcessingRecipeSerializer<>(( id,  inputs, outputs,  fluidInputs,  fluidOutputs, craftTime, upgrades) -> new ItemFluidProcessingRecipe<DoubleInventory>(id, inputs, outputs, fluidInputs, fluidOutputs, craftTime, upgrades) {

            @Override
            public RecipeSerializer<?> getSerializer() {
                return RecipeRegistry.CASTING_SERIALIZER;
            }

            @Override
            public RecipeType<?> getType() {
                return RecipeRegistry.CASTING_RECIPE;
            }

            @Override
            public int fluidOutputStartSlot() {
                return 1;
            }
        }));


        DISTILLING_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:distilling"), new MechanixRecipeType<DistillingRecipe>() {
            public String toString() {
                return "mechanix:distilling";
            }
        });

        DISTILLING_SERIALIZER = RecipeSerializer.register("mechanix:distilling", new ItemFluidProcessingRecipeSerializer<>(DistillingRecipe::new));

        ENCHANTING_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:enchanting"), new MechanixRecipeType<EnchantingRecipe>() {
            public String toString() {
                return "mechanix:enchanting";
            }
        });

        ENCHANTING_SERIALIZER = RecipeSerializer.register("mechanix:enchanting", new EnchanterRecipeSerializer<>(EnchantingRecipe::new));

        DISENCHANTING_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:unenchanting"), new MechanixRecipeType<DisenchantingRecipe>() {
            public String toString() {
                return "mechanix:unenchanting";
            }
        });

        DISENCHANTING_SERIALIZER = RecipeSerializer.register("mechanix:unenchanting", new EnchanterRecipeSerializer<>(DisenchantingRecipe::new));

        COMPRESSING_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:compressing"), new MechanixRecipeType<ItemProcessingRecipe<Inventory>>() {
            public String toString() {
                return "mechanix:compressing";
            }
        });
        COMPRESSING_SERIALIZER = RecipeSerializer.register("mechanix:compressing", new ItemProcessingRecipeSerializer<>((id, inputs, outputs, craftTime, upgrades) -> new ItemProcessingRecipe<Inventory>(id, inputs, outputs, craftTime, upgrades) {

            @Override
            public RecipeSerializer<?> getSerializer() {
                return RecipeRegistry.COMPRESSING_SERIALIZER;
            }

            @Override
            public RecipeType<?> getType() {
                return RecipeRegistry.COMPRESSING_RECIPE;
            }
        }));


        CENTRIFUGE_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:centrifuging"), new MechanixRecipeType<ItemProcessingRecipe<Inventory>>() {
            public String toString() {
                return "mechanix:centrifuging";
            }
        });

        CENTRIFUGE_SERIALIZER = RecipeSerializer.register("mechanix:centrifuging", new ItemProcessingRecipeSerializer<>((id, inputs, outputs, craftTime, upgrades) -> new ItemProcessingRecipe<Inventory>(id, inputs, outputs, craftTime, upgrades) {

            @Override
            public RecipeSerializer<?> getSerializer() {
                return RecipeRegistry.CENTRIFUGE_SERIALIZER;
            }

            @Override
            public RecipeType<?> getType() {
                return RecipeRegistry.CENTRIFUGE_RECIPE;
            }

            @Override
            public int outputSlotStart() {
                return 1;
            }
        }));

        HYDROPONATOR_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:hydroponics"), new MechanixRecipeType<HydroponatorRecipe>() {
            public String toString() {
                return "mechanix:hydroponics";
            }
        });

        HYDROPONATOR_SERIALIZER = RecipeSerializer.register("mechanix:hydroponics", new ItemFluidProcessingRecipeSerializer<>(HydroponatorRecipe::new));

        TRANSMUTING_RECIPE_FAKE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:transmuting_fake"), new MechanixRecipeType<FakeTransmutingRecipe>() {
            public String toString() {
                return "mechanix:transmuting_fake";
            }
        });
        TRANSMUTING_SERIALIZER_FAKE = RecipeSerializer.register("mechanix:transmuting", new FakeTransmutingSerializer(FakeTransmutingRecipe::new));

        TRANSMUTING_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier("mechanix:transmuting"), new MechanixRecipeType<ItemProcessingRecipe<Inventory>>() {
            public String toString() {
                return "mechanix:transmuting";
            }
        });
        TRANSMUTING_SERIALIZER = RecipeSerializer.register("mechanix:transmuting_unused", new ItemProcessingRecipeSerializer<>((id, inputs, outputs, craftTime, upgrades) -> new ItemProcessingRecipe<Inventory>(id, inputs, outputs, craftTime, upgrades) {

            @Override
            public RecipeSerializer<?> getSerializer() {
                return RecipeRegistry.TRANSMUTING_SERIALIZER;
            }

            @Override
            public RecipeType<?> getType() {
                return RecipeRegistry.TRANSMUTING_RECIPE;
            }
        }));
    }
}
