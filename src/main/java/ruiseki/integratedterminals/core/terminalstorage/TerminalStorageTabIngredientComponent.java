package ruiseki.integratedterminals.core.terminalstorage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTab;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabServer;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * Terminal storage tab for ingredient components.
 *
 * @author rubensworks
 */
public class TerminalStorageTabIngredientComponent<T, M> implements ITerminalStorageTab {

    private final IngredientComponent<T, M> ingredientComponent;

    public TerminalStorageTabIngredientComponent(IngredientComponent<T, M> ingredientComponent) {
        this.ingredientComponent = ingredientComponent;
    }

    @Override
    public ResourceLocation getName() {
        return ingredientComponent.getName();
    }

    @Override
    public ITerminalStorageTabClient<?> createClientTab(ContainerTerminalStorageBase container, EntityPlayer player) {
        return new TerminalStorageTabIngredientComponentClient<>(container, getName(), ingredientComponent);
    }

    @Override
    public ITerminalStorageTabServer createServerTab(ContainerTerminalStorageBase container, EntityPlayer player,
        INetwork network) {
        IPositionedAddonsNetworkIngredients<T, M> ingredientNetwork = NetworkHelpers
            .getIngredientNetwork(LazyOptional.of(() -> network), ingredientComponent)
            .orElseThrow(() -> new IllegalStateException("Could not find an ingredient network"));
        return new TerminalStorageTabIngredientComponentServer<>(
            getName(),
            network,
            ingredientComponent,
            ingredientNetwork,
            (EntityPlayerMP) player);
    }

    @Nullable
    @Override
    public ITerminalStorageTabCommon createCommonTab(ContainerTerminalStorageBase container, EntityPlayer player) {
        return new TerminalStorageTabIngredientComponentCommon<>(container, getName(), ingredientComponent);
    }
}
