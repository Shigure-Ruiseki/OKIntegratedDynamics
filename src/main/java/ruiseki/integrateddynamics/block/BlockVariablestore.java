package ruiseki.integrateddynamics.block;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import ruiseki.integrateddynamics.client.gui.GuiVariablestore;
import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integrateddynamics.inventory.container.ContainerVariablestore;
import ruiseki.integrateddynamics.tileentity.TileVariablestore;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * A block that can hold defined variables so that they can be referred to elsewhere in the network.
 *
 * @author rubensworks
 */
public class BlockVariablestore extends BlockContainerGuiCabled {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    private static BlockVariablestore _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static BlockVariablestore getInstance() {
        return _instance;
    }

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockVariablestore(ExtendedConfig eConfig) {
        super(eConfig, TileVariablestore.class);
    }

    @Override
    public boolean saveNBTToDroppedItem() {
        return false;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerVariablestore.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiVariablestore.class;
    }
}
