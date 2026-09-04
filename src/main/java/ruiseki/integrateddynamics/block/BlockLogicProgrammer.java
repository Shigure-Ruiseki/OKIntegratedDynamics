package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammer;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammer;
import ruiseki.okcore.block.BlockGui;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * A block that can hold defined variables so that they can be referred to elsewhere in the network.
 *
 * @author rubensworks
 */
public class BlockLogicProgrammer extends BlockGui {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    /**
     * Make a new block instance.
     */
    public BlockLogicProgrammer(ExtendedConfig<BlockConfig, Block> eConfig) {
        super(eConfig, Material.glass);
        setHardness(3.0F);
        setStepSound(soundTypeMetal);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerLogicProgrammer.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Class<? extends GuiScreen> getGui() {
        return GuiLogicProgrammer.class;
    }
}
