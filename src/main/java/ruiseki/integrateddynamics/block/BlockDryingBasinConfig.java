package ruiseki.integrateddynamics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.render.tileentity.RenderTileEntityDryingBasin;
import ruiseki.integrateddynamics.tileentity.TileDryingBasin;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockDryingBasin}.
 *
 * @author rubensworks
 */
public class BlockDryingBasinConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockDryingBasinConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockDryingBasinConfig() {
        super(IntegratedDynamics._instance, true, "drying_basin", null, config -> new BlockDryingBasin());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRegistered() {
        super.onRegistered();
        getMod().getProxy()
            .registerRenderer(TileDryingBasin.class, new RenderTileEntityDryingBasin());
    }
}
