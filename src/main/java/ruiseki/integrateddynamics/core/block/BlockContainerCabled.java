package ruiseki.integrateddynamics.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.api.block.cable.ICableNetwork;
import ruiseki.integrateddynamics.api.network.INetworkElementProvider;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.path.ICablePathElement;
import ruiseki.integrateddynamics.core.block.cable.CableNetworkComponent;
import ruiseki.integrateddynamics.core.block.cable.NetworkElementProviderComponent;
import ruiseki.integrateddynamics.core.helper.WrenchHelpers;
import ruiseki.integrateddynamics.core.path.CablePathElement;
import ruiseki.okcore.config.configurable.ConfigurableBlockContainer;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.tileentity.TileEntityOK;

/**
 * A base block with tile entity that can connect to cables.
 *
 * @author rubensworks
 */
public abstract class BlockContainerCabled extends ConfigurableBlockContainer
    implements ICableNetwork<IPartNetwork, ICablePathElement>, INetworkElementProvider<IPartNetwork> {

    // @Delegate <- Lombok can't handle delegations with generics, so we'll have to do it manually...
    private CableNetworkComponent<BlockContainerCabled> cableNetworkComponent = new CableNetworkComponent<>(this);
    private NetworkElementProviderComponent<IPartNetwork> networkElementProviderComponent = new NetworkElementProviderComponent<>(
        this);

    /**
     * Make a new block instance.
     *
     * @param eConfig    Config for this block.
     * @param tileEntity The tile class
     */
    public BlockContainerCabled(ExtendedConfig eConfig, Class<? extends TileEntityOK> tileEntity) {
        super(eConfig, Material.anvil, tileEntity);

        setHardness(5.0F);
        setStepSound(soundTypeMetal);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (!world.isRemote && WrenchHelpers.isWrench(player, new BlockPos(x, y, z)) && player.isSneaking()) {
            destroyBlock(world, x, y, z, true);
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, side, subX, subY, subZ);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        cableNetworkComponent.addToNetwork(world, new BlockPos(x, y, z));
    }

    @Override
    protected void onPreBlockDestroyed(World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        networkElementProviderComponent.onPreBlockDestroyed(getNetwork(world, pos), world, pos, true);
        cableNetworkComponent.onPreBlockDestroyed(world, pos);
        super.onPreBlockDestroyed(world, x, y, z);
    }

    @Override
    protected void onPostBlockDestroyed(World world, int x, int y, int z) {
        super.onPostBlockDestroyed(world, x, y, z);
        cableNetworkComponent.onPostBlockDestroyed(world, new BlockPos(x, y, z));
    }

    /* --------------- Delegate to ICableNetwork<CablePathElement> --------------- */

    @Override
    public void initNetwork(World world, BlockPos pos) {
        cableNetworkComponent.initNetwork(world, pos);
    }

    @Override
    public boolean canConnect(World world, BlockPos selfPosition, ICable connector, ForgeDirection side) {
        return cableNetworkComponent.canConnect(world, selfPosition, connector, side);
    }

    @Override
    public void updateConnections(World world, BlockPos pos) {
        cableNetworkComponent.updateConnections(world, pos);
    }

    @Override
    public void triggerUpdateNeighbourConnections(World world, BlockPos pos) {
        cableNetworkComponent.triggerUpdateNeighbourConnections(world, pos);
    }

    @Override
    public boolean isConnected(World world, BlockPos pos, ForgeDirection side) {
        return cableNetworkComponent.isConnected(world, pos, side);
    }

    @Override
    public void disconnect(World world, BlockPos pos, ForgeDirection side) {
        cableNetworkComponent.disconnect(world, pos, side);
    }

    @Override
    public void reconnect(World world, BlockPos pos, ForgeDirection side) {
        cableNetworkComponent.reconnect(world, pos, side);
    }

    @Override
    public void remove(World world, BlockPos pos, EntityPlayer player) {
        cableNetworkComponent.remove(world, pos, player);
    }

    @Override
    public void resetCurrentNetwork(World world, BlockPos pos) {
        cableNetworkComponent.resetCurrentNetwork(world, pos);
    }

    @Override
    public void setNetwork(IPartNetwork network, World world, BlockPos pos) {
        cableNetworkComponent.setNetwork(network, world, pos);
    }

    @Override
    public IPartNetwork getNetwork(World world, BlockPos pos) {
        return cableNetworkComponent.getNetwork(world, pos);
    }

    @Override
    public CablePathElement createPathElement(World world, BlockPos blockPos) {
        return cableNetworkComponent.createPathElement(world, blockPos);
    }
}
