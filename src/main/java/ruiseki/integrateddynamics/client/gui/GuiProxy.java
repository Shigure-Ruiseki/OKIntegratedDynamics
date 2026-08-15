package ruiseki.integrateddynamics.client.gui;

import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.integrateddynamics.core.client.gui.GuiActiveVariableBase;
import ruiseki.integrateddynamics.inventory.container.ContainerProxy;
import ruiseki.integrateddynamics.tileentity.TileProxy;

/**
 * Gui for the proxy.
 * 
 * @author rubensworks
 */
public class GuiProxy extends GuiActiveVariableBase<ContainerProxy, TileProxy> {

    private static final int ERROR_X = 110;
    private static final int ERROR_Y = 26;

    /**
     * Make a new instance.
     * 
     * @param inventory The player inventory.
     * @param tile      The part.
     */
    public GuiProxy(InventoryPlayer inventory, TileProxy tile) {
        super(new ContainerProxy(inventory, tile));
    }

    @Override
    protected int getBaseYSize() {
        return 189;
    }

    @Override
    protected int getErrorX() {
        return ERROR_X;
    }

    @Override
    protected int getErrorY() {
        return ERROR_Y;
    }
}
