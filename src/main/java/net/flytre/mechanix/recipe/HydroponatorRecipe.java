package net.flytre.mechanix.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.inventory.DoubleInventory;
import net.flytre.mechanix.api.recipe.OutputProvider;
import net.flytre.mechanix.api.recipe.QuantifiedIngredient;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.RecipeRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class HydroponatorRecipe extends ItemSeparationRecipe<DoubleInventory> {

    private final QuantifiedIngredient fertilizer;

    public HydroponatorRecipe(Identifier id, QuantifiedIngredient input, QuantifiedIngredient fertilizer, OutputProvider[] outputs, int craftTime) {
        super(id,input,outputs,craftTime);
        this.fertilizer = fertilizer;
    }


    public FluidStack getFluidRequired() {
        return new FluidStack(Fluids.WATER,500);
    }


    public QuantifiedIngredient getFertilizer() {
        return fertilizer;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(MachineRegistry.HYDROPONATOR.getBlock());
    }

    @Override
    public boolean matches(DoubleInventory inv, World world) {
        if(!super.matches(inv,world))
            return false;

        return fertilizer.test(inv.getStack(1)) && inv.getFluidStack(0).getFluid() == Fluids.WATER && inv.getFluidStack(0).getAmount() >= 500;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.HYDROPONATOR_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.HYDROPONATOR_RECIPE;
    }

    @Override
    public boolean cancelLoad() {
        return super.cancelLoad() || this.fertilizer.isEmpty();
    }




}
