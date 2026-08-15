package ruiseki.integratedterminals.core.terminalstorage;

import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
    public ITerminalStorageTabClient<?> createClientTab(ContainerTerminalStorage container, EntityPlayer player,
        PartTarget target) {
        return new TerminalStorageTabIngredientComponentClient<>(container, getName(), ingredientComponent);
    }

    @Override
    public ITerminalStorageTabServer createServerTab(ContainerTerminalStorage container, EntityPlayer player,
        PartTarget target) {
        INetwork network = Objects.requireNonNull(NetworkHelpers.getNetwork(target.getCenter()));
        IPositionedAddonsNetworkIngredients<T, M> ingredientNetwork = Objects
            .requireNonNull(NetworkHelpers.getIngredientNetwork(network, ingredientComponent));
        return new TerminalStorageTabIngredientComponentServer<>(
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
        return new TerminalStorageTabIngredientComponentCommon<>(container, getName(), ingredientComponent);
    }
}
