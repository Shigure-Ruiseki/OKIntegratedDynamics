package ruiseki.integrateddynamics.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.core.persist.world.LabelsWorldStorage;
import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.SpecialRecipe;

public class ItemVariableCopyRecipe extends SpecialRecipe {

    public ItemVariableCopyRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matchesOK(InventoryCrafting inv, World worldIn) {
        return assemble(inv) != null;
    }

    @Override
    public ItemStack assemble(InventoryCrafting inv) {
        ItemStack withData = null;
        ItemStack withoutData = null;
        IVariableFacade facade;
        int count = 0;

        for (int j = 0; j < inv.getSizeInventory(); j++) {
            ItemStack element = inv.getStackInSlot(j);
            if (element != null && element.getItem() instanceof ItemVariable) {
                count++;
                facade = ((ItemVariable) ItemVariableConfig._instance.getInstance()).getVariableFacade(element);
                if (!facade.isValid() && withoutData == null) {
                    withoutData = element;
                }
                if (facade.isValid() && withData == null && element.stackSize == 1) {
                    withData = element;
                }
            }
        }

        if (count == 2 && withoutData != null && withData != null) {
            return IntegratedDynamics._instance.getRegistryManager()
                .getRegistry(IVariableFacadeHandlerRegistry.class)
                .copy(!MinecraftHelpers.isClientSide(), withData);
        }
        return null;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemHelpers.EMPTY);
        for (int j = 0; j < inv.getSizeInventory(); j++) {
            ItemStack element = inv.getStackInSlot(j);
            if (!ItemHelpers.isEmpty(element) && element.getItem() instanceof ItemVariable itemVariable) {
                IVariableFacade facade = itemVariable.getVariableFacade(element);
                if (facade.isValid()) {
                    // Create a copy with a new id.
                    ItemStack copy = IntegratedDynamics._instance.getRegistryManager()
                        .getRegistry(IVariableFacadeHandlerRegistry.class)
                        .copy(!MinecraftHelpers.isClientSide(), element);

                    // If the input had a label, also copy the label
                    String label = LabelsWorldStorage.getInstance(IntegratedDynamics._instance)
                        .getLabel(facade.getId());
                    if (label != null) {
                        IVariableFacade facadeCopy = ((ItemVariable) ItemVariableConfig._instance.getInstance())
                            .getVariableFacade(copy);
                        if (facadeCopy != null) {
                            LabelsWorldStorage.getInstance(IntegratedDynamics._instance)
                                .put(facadeCopy.getId(), label);
                        }
                    }

                    ret.set(j, copy);
                }
            }
        }
        return ret;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public int getRecipeSize() {
        return getIngredients().size();
    }

    @Override
    public ItemStack getResultItem() {
        return new ItemStack(ItemVariableConfig._instance.getInstance(), 1);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.from(Ingredient.EMPTY, Ingredient.of(getResultItem()), Ingredient.of(getResultItem()));
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ItemVariableCopyRecipeConfig._instance.getInstance();
    }
}
