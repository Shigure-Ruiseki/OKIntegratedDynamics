package ruiseki.integratedterminals.core.terminalstorage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTab;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabServer;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorage;

/**
 * Terminal storage tab for the item crafting grid.
 *
 * @author rubensworks
 */
public class TerminalStorageTabIngredientComponentItemStackCrafting implements ITerminalStorageTab {

    public static ResourceLocation NAME;

    private final IngredientComponent<ItemStack, Integer> ingredientComponent;
    private final ResourceLocation name;

    public TerminalStorageTabIngredientComponentItemStackCrafting(
        IngredientComponent<ItemStack, Integer> ingredientComponent) {
        this.ingredientComponent = ingredientComponent;
        this.name = new ResourceLocation(
            ingredientComponent.getName()
                .getResourceDomain(),
            ingredientComponent.getName()
                .getResourcePath() + "_crafting");
        TerminalStorageTabIngredientComponentItemStackCrafting.NAME = this.name;
    }

    @Override
    public ResourceLocation getName() {
        return this.name;
    }

    @Override
    public ITerminalStorageTabClient<?> createClientTab(ContainerTerminalStorage container, EntityPlayer player,
        PartTarget target) {
        return new TerminalStorageTabIngredientComponentItemStackCraftingClient(
            container,
            getName(),
            ingredientComponent);
    }

    @Override
    public ITerminalStorageTabServer createServerTab(ContainerTerminalStorage container, EntityPlayer player,
        PartTarget target) {
        INetwork network = NetworkHelpers.getNetwork(target.getCenter());
        IPositionedAddonsNetworkIngredients<ItemStack, Integer> ingredientNetwork = NetworkHelpers
            .getIngredientNetwork(network, ingredientComponent);
        return new TerminalStorageTabIngredientComponentItemStackCraftingServer(
            getName(),
            network,
            ingredientComponent,
            ingredientNetwork,
            target.getCenter(),
            (EntityPlayerMP) player);
    }

    @Nullable
    @Override
    public ITerminalStorageTabCommon createCommonTab(ContainerTerminalStorage container, EntityPlayer player,
        PartTarget target) {
        return new TerminalStorageTabIngredientComponentItemStackCraftingCommon(
            container,
            getName(),
            IngredientComponent.ITEMSTACK);
    }
}
