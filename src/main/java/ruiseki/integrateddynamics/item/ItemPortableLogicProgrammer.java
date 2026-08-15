package ruiseki.integrateddynamics.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerPortable;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerPortable;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.item.ItemGui;

/**
 * A portable logic programmer.
 *
 * @author rubensworks
 */
public class ItemPortableLogicProgrammer extends ItemGui {

    private static ItemPortableLogicProgrammer _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static ItemPortableLogicProgrammer getInstance() {
        return _instance;
    }

    /**
     * Make a new item instance.
     *
     * @param eConfig Config for this blockState.
     */
    public ItemPortableLogicProgrammer(ItemConfig eConfig) {
        super(eConfig);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerLogicProgrammerPortable.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiLogicProgrammerPortable.class;
    }
}
