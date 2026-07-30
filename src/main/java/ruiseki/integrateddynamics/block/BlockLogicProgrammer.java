package ruiseki.integrateddynamics.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammer;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammer;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;
import ruiseki.okcore.config.configurable.ConfigurableBlockGui;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * A block that can hold defined variables so that they can be referred to elsewhere in the network.
 *
 * @author rubensworks
 */
public class BlockLogicProgrammer extends ConfigurableBlockGui {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    private static BlockLogicProgrammer _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static BlockLogicProgrammer getInstance() {
        return _instance;
    }

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockLogicProgrammer(ExtendedConfig eConfig) {
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
