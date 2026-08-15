package ruiseki.integrateddynamics.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.client.gui.GuiLabeller;
import ruiseki.integrateddynamics.inventory.container.ContainerLabeller;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.item.ItemGui;

/**
 * A labeller for variables.
 *
 * @author rubensworks
 */
public class ItemLabeller extends ItemGui {

    private static ItemLabeller _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static ItemLabeller getInstance() {
        return _instance;
    }

    /**
     * Make a new item instance.
     *
     * @param eConfig Config for this blockState.
     */
    public ItemLabeller(ItemConfig eConfig) {
        super(eConfig);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerLabeller.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiLabeller.class;
    }
}
