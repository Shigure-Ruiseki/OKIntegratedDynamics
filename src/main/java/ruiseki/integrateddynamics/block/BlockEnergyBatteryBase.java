package ruiseki.integrateddynamics.block;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.core.block.BlockContainerCabled;
import ruiseki.integrateddynamics.tileentity.TileEnergyBattery;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.TileHelpers;

/**
 * A block that holds energy.
 *
 * @author rubensworks
 */
public abstract class BlockEnergyBatteryBase extends BlockContainerCabled implements IEnergyContainerBlock {

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockEnergyBatteryBase(ExtendedConfig eConfig) {
        super(eConfig, TileEnergyBattery.class);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        TileHelpers.getSafeTile(world, x, y, z, TileEnergyBattery.class)
            .updateBlockState();
    }

    @Override
    public String getEneryContainerNBTName() {
        return "energy";
    }

    public abstract boolean isCreative();
}
