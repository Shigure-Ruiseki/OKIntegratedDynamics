package ruiseki.integrateddynamics.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.render.tileentity.RenderTileEntitySqueezer;
import ruiseki.integrateddynamics.tileentity.TileSqueezer;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockSqueezer}.
 *
 * @author rubensworks
 */
public class BlockSqueezerConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockSqueezerConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockSqueezerConfig() {
        super(IntegratedDynamics._instance, true, "squeezer", null, config -> new BlockSqueezer());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRegistered() {
        super.onRegistered();
        getMod().getProxy()
            .registerRenderer(TileSqueezer.class, new RenderTileEntitySqueezer());
    }
}
