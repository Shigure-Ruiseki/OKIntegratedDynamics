package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import ruiseki.integrateddynamics.client.gui.GuiProxy;
import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integrateddynamics.inventory.container.ContainerProxy;
import ruiseki.integrateddynamics.tileentity.TileProxy;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * A block that can expose variables.
 *
 * @author rubensworks
 */
public class BlockProxy extends BlockContainerGuiCabled {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    /**
     * Make a new block instance.
     */
    public BlockProxy(ExtendedConfig<BlockConfig, Block> eConfig) {
        super(eConfig, TileProxy.class);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerProxy.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiProxy.class;
    }
}
