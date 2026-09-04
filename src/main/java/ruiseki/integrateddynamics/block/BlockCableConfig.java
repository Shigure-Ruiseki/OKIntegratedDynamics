package ruiseki.integrateddynamics.block;

import net.minecraft.item.ItemBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.client.render.tileentity.RenderCable;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.integrateddynamics.item.ItemBlockCable;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Config for {@link ruiseki.integrateddynamics.core.block.BlockMultipartTicking}.
 *
 * @author rubensworks
 */
public class BlockCableConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockCableConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockCableConfig() {
        super(IntegratedDynamics._instance, true, "cable", null, config -> new BlockCable());
    }

    @Override
    public void onRegistered() {
        super.onRegistered();
        if (MinecraftHelpers.isClientSide()) {
            registerClientSide();
        }
    }

    @SideOnly(Side.CLIENT)
    private void registerClientSide() {
        IntegratedDynamics._instance.proxy.registerRenderer(TileMultipartTicking.class, new RenderCable());
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockCable.class;
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
