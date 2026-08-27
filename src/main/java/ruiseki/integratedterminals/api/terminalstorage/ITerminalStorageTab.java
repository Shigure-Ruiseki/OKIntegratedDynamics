package ruiseki.integratedterminals.api.terminalstorage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;

/**
 * A terminal storage tab.
 * 
 * @author rubensworks
 */
public interface ITerminalStorageTab {

    /**
     * @return The unique name of this tab.
     */
    public ResourceLocation getName();

    /**
     * @param container The container in which the tab is about to be created.
     * @param player    The player opening the container.
     * @return A new client-side tab.
     */
    public ITerminalStorageTabClient<?> createClientTab(ContainerTerminalStorageBase container, EntityPlayer player);

    /**
     * @param container The container in which the tab is about to be created.
     * @param player    The player opening the container.
     * @param network   The network of the storage terminal.
     * @return A new server-side tab.
     */
    public ITerminalStorageTabServer createServerTab(ContainerTerminalStorageBase container, EntityPlayer player,
        INetwork network);

    /**
     * @param container The container in which the tab is about to be created.
     * @param player    The player opening the container.
     * @return A new common tab.
     */
    @Nullable
    public ITerminalStorageTabCommon createCommonTab(ContainerTerminalStorageBase container, EntityPlayer player);

}
