package ruiseki.integrateddynamics.core.recipe.type;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.capability.energystorage.IEnergyStorageCapacity;
import ruiseki.integrateddynamics.capability.energystorage.IEnergyStorageMutable;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.SpecialRecipe;

/**
 * Recipe for combining energy batteries in a shapeless manner.
 *
 * @author rubensworks
 */
public class RecipeEnergyContainerCombination extends SpecialRecipe {

    private final Ingredient batteryItem;
    private final int maxCapacity;

    public RecipeEnergyContainerCombination(ResourceLocation id, Ingredient batteryItem, int maxCapacity) {
        super(id);
        this.batteryItem = batteryItem;
        this.maxCapacity = maxCapacity;
    }

    public Ingredient getBatteryItem() {
        return batteryItem;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public boolean matchesOK(InventoryCrafting grid, World world) {
        return assemble(grid) != null;
    }

    @Override
    public ItemStack getResultItem() {
        return this.batteryItem.getItems()[0];
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventory) {
        NonNullList<ItemStack> aitemstack = NonNullList.withSize(inventory.getSizeInventory(), null);

        for (int i = 0; i < aitemstack.size(); ++i) {
            ItemStack itemstack = inventory.getStackInSlot(i);
            if (itemstack != null && itemstack.getItem() != null) {
                aitemstack.set(
                    i,
                    itemstack.getItem()
                        .getContainerItem(itemstack));
            }
        }

        return aitemstack;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeEnergyContainerCombinationConfig._instance.getInstance();
    }

    @Override
    public ItemStack assemble(InventoryCrafting grid) {
        ItemStack output = getRecipeOutput().copy();
        IEnergyStorageCapacity energyStorage = (IEnergyStorageCapacity) CapabilityHelpers
            .getCapability(output, CapabilityEnergy.ENERGY)
            .getOrNull();

        int totalCapacity = 0;
        int totalEnergy = 0;
        int inputItems = 0;

        // Loop over the grid and count the total contents and capacity + collect energy.
        for (int j = 0; j < grid.getSizeInventory(); j++) {
            ItemStack stackInSlot = grid.getStackInSlot(j);

            if (stackInSlot != null) {
                ItemStack element = ItemStackHelpers.split(stackInSlot.copy(), 1);

                if (element != null) {
                    if (this.batteryItem.test(element)) {
                        IEnergyStorageCapacity currentEnergyStorage = (IEnergyStorageCapacity) CapabilityHelpers
                            .getCapability(element, CapabilityEnergy.ENERGY)
                            .getOrNull();

                        if (currentEnergyStorage != null) {
                            inputItems++;
                            totalEnergy = Helpers.addSafe(totalEnergy, currentEnergyStorage.getEnergyStored());
                            totalCapacity = Helpers.addSafe(totalCapacity, currentEnergyStorage.getMaxEnergyStored());
                        }
                    } else {
                        return null;
                    }
                }
            }
        }

        if (inputItems < 2 || totalCapacity > this.maxCapacity) {
            return null;
        }

        // Set capacity and fill fluid into output.
        if (energyStorage != null) {
            energyStorage.setCapacity(totalCapacity);
            ((IEnergyStorageMutable) energyStorage).setEnergy(totalEnergy);
        }

        return output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.batteryItem);
        ingredients.add(this.batteryItem);
        return ingredients;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 9;
    }
}
