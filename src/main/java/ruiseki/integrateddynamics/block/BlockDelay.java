package ruiseki.integrateddynamics.block;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import ruiseki.integrateddynamics.client.gui.GuiDelay;
import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integrateddynamics.inventory.container.ContainerDelay;
import ruiseki.integrateddynamics.tileentity.TileDelay;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * A block that can delay variables.
 * 
 * @author rubensworks
 */
public class BlockDelay extends BlockContainerGuiCabled {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    private static BlockDelay _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static BlockDelay getInstance() {
        return _instance;
    }

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockDelay(ExtendedConfig eConfig) {
        super(eConfig, TileDelay.class);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerDelay.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiDelay.class;
    }
}
