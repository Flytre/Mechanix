package net.flytre.mechanix.block.furnace;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.block.generator.GeneratorBlock;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PoweredFurnaceBlockEntity extends EnergyEntity implements SidedInventory {
    protected final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final int energyPerTick;
    private final Object2IntOpenHashMap<Identifier> recipesUsed;
    protected DefaultedList<ItemStack> inventory;
    private int cookTime;
    private int cookTimeTotal;


    public PoweredFurnaceBlockEntity() {
        super(MachineRegistry.POWERED_FURNACE.getEntityType());

        setMaxEnergy(100000);
        setMaxTransferRate(100);
        panelMode = 1;
        setEnergyMode(false, false, false, false, false, false);
        setIOMode(false, false, false, false, false, false);

        inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.recipesUsed = new Object2IntOpenHashMap<>();
        this.recipeType = RecipeType.SMELTING;

        energyPerTick = 30;
    }

    private static void dropExperience(World world, Vec3d vec3d, int i, float f) {
        int j = MathHelper.floor((float) i * f);
        float g = MathHelper.fractionalPart((float) i * f);
        if (g != 0.0F && Math.random() < (double) g) {
            ++j;
        }

        while (j > 0) {
            int k = ExperienceOrbEntity.roundToOrbSize(j);
            j -= k;
            world.spawnEntity(new ExperienceOrbEntity(world, vec3d.x, vec3d.y, vec3d.z, k));
        }

    }

    @Override
    public void updateDelegate() {
        super.updateDelegate();
        getProperties().set(8, cookTime);
        getProperties().set(9, cookTimeTotal);
    }

    private boolean isBurning() {
        return this.getEnergy() >= energyPerTick && !this.inventory.get(0).isEmpty();
    }

    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);
        this.cookTime = tag.getShort("CookTime");
        this.cookTimeTotal = tag.getShort("CookTimeTotal");
        CompoundTag compoundTag = tag.getCompound("RecipesUsed");
        for (String string : compoundTag.getKeys()) {
            this.recipesUsed.put(new Identifier(string), compoundTag.getInt(string));
        }

    }

    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putShort("CookTime", (short) this.cookTime);
        tag.putShort("CookTimeTotal", (short) this.cookTimeTotal);
        Inventories.toTag(tag, this.inventory);
        CompoundTag compoundTag = new CompoundTag();
        this.recipesUsed.forEach((identifier, integer) -> {
            compoundTag.putInt(identifier.toString(), integer);
        });
        tag.put("RecipesUsed", compoundTag);
        return tag;
    }

    @Override
    public void repeatTick() {
        boolean bl = this.isBurning();
        boolean bl2 = false;

        if (this.world == null || this.world.isClient)
            return;

        boolean lit = this.world.getBlockState(this.pos).get(GeneratorBlock.LIT);
        if (lit != this.isBurning()) {
            bl2 = true;
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(GeneratorBlock.LIT, this.isBurning()), 3);
        }

        if (bl && !(this.inventory.get(0) == ItemStack.EMPTY)) {
            this.removeEnergy(energyPerTick);
        } else
            return;

        if (!this.isBurning() && this.inventory.get(0) == ItemStack.EMPTY) {
            if (!this.isBurning() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }
        } else {
            Recipe<?> recipe = this.world.getRecipeManager().getFirstMatch(this.recipeType, this, this.world).orElse(null);

            if (this.canAcceptRecipeOutput(recipe)) {
                ++this.cookTime;
                if (this.cookTime == this.cookTimeTotal) {
                    this.cookTime = 0;
                    this.cookTimeTotal = this.getCookTime();
                    this.craftRecipe(recipe);
                    bl2 = true;
                }
            } else {
                this.cookTime = 0;
            }
        }


        if (bl2) {
            this.markDirty();
        }
    }

    @Override
    public void onceTick() {
        int tierTimes = getTier() + 1;
        if (world != null && !world.isClient && !isFull()) {
            double amount = Math.min(this.getMaxTransferRate() * tierTimes, this.getMaxEnergy() - this.getEnergy());
            requestEnergy(amount);
        }

    }

    protected boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe) {
        if (!this.inventory.get(0).isEmpty() && recipe != null) {
            ItemStack recipeOutput = recipe.getOutput();
            if (recipeOutput.isEmpty()) {
                return false;
            } else {
                ItemStack currentOutputStack = this.inventory.get(1);
                if (currentOutputStack.isEmpty()) {
                    return true;
                } else if (!currentOutputStack.isItemEqualIgnoreDamage(recipeOutput)) {
                    return false;
                } else if (currentOutputStack.getCount() < this.getMaxCountPerStack() && currentOutputStack.getCount() < currentOutputStack.getMaxCount()) {
                    return true;
                } else {
                    return currentOutputStack.getCount() < recipeOutput.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    private void craftRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null && this.canAcceptRecipeOutput(recipe)) {
            ItemStack inputStack = this.inventory.get(0);
            ItemStack recipeOutput = recipe.getOutput();
            ItemStack currentOutputStack = this.inventory.get(1);
            if (currentOutputStack.isEmpty()) {
                this.inventory.set(1, recipeOutput.copy());
            } else if (currentOutputStack.getItem() == recipeOutput.getItem()) {
                currentOutputStack.increment(1);
            }

            if (!this.world.isClient) {
                this.setLastRecipe(recipe);
            }

            inputStack.decrement(1);
        }
    }

    public int[] getAvailableSlots(Direction side) {
        return new int[]{0, 1};
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        //input mode, input slot, & valid item
        return slot == 0 && !ioMode.get(dir) && this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        //output mode & output slot
        return slot == 1 && ioMode.get(dir);
    }

    public int size() {
        return this.inventory.size();
    }

    public boolean isEmpty() {
        return inventory.get(0) == ItemStack.EMPTY && inventory.get(1) == ItemStack.EMPTY;
    }

    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areTagsEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        if (slot == 0 && !bl) {
            this.cookTimeTotal = this.getCookTime();
            this.cookTime = 0;
            this.markDirty();
        }

    }

    protected int getCookTime() {
        if (this.world == null)
            return 200;
        return this.world.getRecipeManager().getFirstMatch(this.recipeType, this, this.world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    public boolean isValid(int slot, ItemStack stack) {
        return slot != 1;
    }

    public void clear() {
        this.inventory.clear();
    }

    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier identifier = recipe.getId();
            this.recipesUsed.addTo(identifier, 1);
        }

    }

    public List<Recipe<?>> method_27354(World world, Vec3d vec3d) {
        List<Recipe<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<Identifier> identifierEntry : this.recipesUsed.object2IntEntrySet()) {
            world.getRecipeManager().get(identifierEntry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                dropExperience(world, vec3d, identifierEntry.getIntValue(), ((AbstractCookingRecipe) recipe).getExperience());
            });
        }

        return list;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.furnace");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new PoweredFurnaceScreenHandler(syncId, inv, this, getProperties());
    }
}
