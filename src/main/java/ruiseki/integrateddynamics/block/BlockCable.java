package ruiseki.integrateddynamics.block;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.gtnewhorizon.gtnhlib.api.BlockModelInfo;
import com.gtnewhorizon.gtnhlib.api.IBlockModelProvider;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.client.model.BakedModelQuadContext;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Setter;
import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.block.IDynamicLightBlock;
import ruiseki.integrateddynamics.api.block.IDynamicRedstoneBlock;
import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.api.block.cable.ICableFacadeable;
import ruiseki.integrateddynamics.api.block.cable.ICableFakeable;
import ruiseki.integrateddynamics.api.block.cable.ICableNetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.INetworkElementProvider;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartContainerFacade;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.path.ICablePathElement;
import ruiseki.integrateddynamics.api.tileentity.ITileCableNetwork;
import ruiseki.integrateddynamics.client.model.CableModel;
import ruiseki.integrateddynamics.core.block.cable.CableNetworkFacadeableComponent;
import ruiseki.integrateddynamics.core.block.cable.NetworkElementProviderComponent;
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.helper.WrenchHelpers;
import ruiseki.integrateddynamics.core.path.CablePathElement;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.integrateddynamics.item.ItemBlockCable;
import ruiseki.integrateddynamics.item.ItemFacade;
import ruiseki.okcore.block.collidable.CollidableComponent;
import ruiseki.okcore.block.collidable.ICollidable;
import ruiseki.okcore.block.collidable.ICollidableParent;
import ruiseki.okcore.client.icon.Icon;
import ruiseki.okcore.config.configurable.ConfigurableBlockContainer;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.EnumFacingMap;
import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.RenderHelpers;
import ruiseki.okcore.helper.TileHelpers;

public class BlockCable extends ConfigurableBlockContainer
    implements ICableNetwork<IPartNetwork, ICablePathElement>, ICableFakeable<ICablePathElement>,
    ICableFacadeable<ICablePathElement>, INetworkElementProvider, IPartContainerFacade, ICollidable<ForgeDirection>,
    ICollidableParent, IDynamicRedstoneBlock, IDynamicLightBlock, IBlockModelProvider, BlockModelInfo {

    public static final float BLOCK_HARDNESS = 3.0F;
    public static final Material BLOCK_MATERIAL = Material.glass;

    // Collision boxes
    private final static AxisAlignedBB CABLE_CENTER_BOUNDINGBOX = AxisAlignedBB
        .getBoundingBox(CableModel.MIN, CableModel.MIN, CableModel.MIN, CableModel.MAX, CableModel.MAX, CableModel.MAX);
    private final static EnumFacingMap<AxisAlignedBB> CABLE_SIDE_BOUNDINGBOXES = EnumFacingMap.forAllValues(
        AxisAlignedBB.getBoundingBox(CableModel.MIN, 0, CableModel.MIN, CableModel.MAX, CableModel.MIN, CableModel.MAX), // DOWN
        AxisAlignedBB.getBoundingBox(CableModel.MIN, CableModel.MAX, CableModel.MIN, CableModel.MAX, 1, CableModel.MAX), // UP
        AxisAlignedBB.getBoundingBox(CableModel.MIN, CableModel.MIN, 0, CableModel.MAX, CableModel.MAX, CableModel.MIN), // NORTH
        AxisAlignedBB.getBoundingBox(CableModel.MIN, CableModel.MAX, CableModel.MAX, CableModel.MAX, CableModel.MIN, 1), // SOUTH
        AxisAlignedBB.getBoundingBox(0, CableModel.MIN, CableModel.MIN, CableModel.MIN, CableModel.MAX, CableModel.MAX), // WEST
        AxisAlignedBB.getBoundingBox(CableModel.MAX, CableModel.MIN, CableModel.MIN, 1, CableModel.MAX, CableModel.MAX) // EAST
    );

    // Collision components
    private static final List<IComponent<ForgeDirection, BlockCable>> COLLIDABLE_COMPONENTS = Lists.newLinkedList();
    private static final IComponent<ForgeDirection, BlockCable> CENTER_COMPONENT = new IComponent<ForgeDirection, BlockCable>() {

        @Override
        public Collection<ForgeDirection> getPossiblePositions() {
            return Arrays.asList(new ForgeDirection[] { null });
        }

        @Override
        public int getBoundsCount(ForgeDirection position) {
            return 1;
        }

        @Override
        public boolean isActive(BlockCable block, World world, int x, int y, int z, ForgeDirection forgeDirection) {
            return block.isRealCable(world, new BlockPos(x, y, z));
        }

        @Override
        public List<AxisAlignedBB> getBounds(BlockCable block, World world, int x, int y, int z,
            ForgeDirection forgeDirection) {
            return Collections.singletonList(block.getCableBoundingBox(null));
        }

        @Override
        public ItemStack getPickBlock(World world, int x, int y, int z, ForgeDirection forgeDirection) {
            return new ItemStack(BlockCable.getInstance());
        }

        @Override
        public boolean destroy(World world, int x, int y, int z, ForgeDirection forgeDirection, EntityPlayer player) {
            if (!world.isRemote) {
                Block block = world.getBlock(x, y, z);
                if (block instanceof BlockCable cable) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (cable.getPartContainer(world, pos)
                        .hasParts()) {
                        cable.setRealCable(world, pos, false);
                        ItemStackHelpers.spawnItemStackToPlayer(world, pos, new ItemStack(block), player);
                        return false;
                    } else {
                        cable.remove(world, pos, player);
                        return true;
                    }
                }
            }
            return false;
        }
    };
    private static final IComponent<ForgeDirection, BlockCable> CABLECONNECTIONS_COMPONENT = new IComponent<ForgeDirection, BlockCable>() {

        @Override
        public Collection<ForgeDirection> getPossiblePositions() {
            return Arrays.asList(ForgeDirection.VALID_DIRECTIONS);
        }

        @Override
        public int getBoundsCount(ForgeDirection position) {
            return 1;
        }

        @Override
        public boolean isActive(BlockCable block, World world, int x, int y, int z, ForgeDirection position) {
            BlockPos pos = new BlockPos(x, y, z);
            return CENTER_COMPONENT.isActive(block, world, x, y, z, position)
                && (block.isConnected(world, pos, position) || block.hasPart(world, pos, position));
        }

        @Override
        public List<AxisAlignedBB> getBounds(BlockCable block, World world, int x, int y, int z,
            ForgeDirection position) {
            BlockPos pos = new BlockPos(x, y, z);
            return Collections.singletonList(
                block.isConnected(world, pos, position) ? block.getCableBoundingBox(position)
                    : block.getCableBoundingBoxWithPart(world, pos, position));
        }

        @Override
        public ItemStack getPickBlock(World world, int x, int y, int z, ForgeDirection position) {
            return new ItemStack(BlockCable.getInstance());
        }

        @Override
        public boolean destroy(World world, int x, int y, int z, ForgeDirection position, EntityPlayer player) {
            return CENTER_COMPONENT.destroy(world, x, y, z, position, player);
        }
    };
    private static final IComponent<ForgeDirection, BlockCable> PARTS_COMPONENT = new IComponent<ForgeDirection, BlockCable>() {

        @Override
        public Collection<ForgeDirection> getPossiblePositions() {
            return Arrays.asList(ForgeDirection.VALID_DIRECTIONS);
        }

        @Override
        public int getBoundsCount(ForgeDirection position) {
            return 1;
        }

        @Override
        public boolean isActive(BlockCable block, World world, int x, int y, int z, ForgeDirection position) {
            return block.hasPart(world, new BlockPos(x, y, z), position);
        }

        @Override
        public List<AxisAlignedBB> getBounds(BlockCable block, World world, int x, int y, int z,
            ForgeDirection position) {
            return Collections.singletonList(block.getPartBoundingBox(world, new BlockPos(x, y, z), position));
        }

        @Override
        public ItemStack getPickBlock(World world, int x, int y, int z, ForgeDirection position) {
            BlockPos pos = new BlockPos(x, y, z);
            if (!(pos.getBlock(world) instanceof BlockCable cable)) return null;
            IPartContainer partContainer = cable.getPartContainer(world, pos);
            return partContainer.getPart(position)
                .getPickBlock(world, pos, partContainer.getPartState(position));
        }

        @Override
        public boolean destroy(World world, int x, int y, int z, ForgeDirection position, EntityPlayer player) {
            if (!world.isRemote) {
                return PartHelpers.removePart(world, new BlockPos(x, y, z), position, player, true);
            }
            return false;
        }
    };
    private static final IComponent<ForgeDirection, BlockCable> FACADE_COMPONENT = new IComponent<ForgeDirection, BlockCable>() {

        private final AxisAlignedBB BOUNDS = AxisAlignedBB.getBoundingBox(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);

        @Override
        public Collection<ForgeDirection> getPossiblePositions() {
            return Arrays.asList(new ForgeDirection[] { null });
        }

        @Override
        public int getBoundsCount(ForgeDirection position) {
            return 1;
        }

        @Override
        public boolean isActive(BlockCable cable, World world, int x, int y, int z, ForgeDirection position) {
            return cable.hasFacade(world, new BlockPos(x, y, z));
        }

        @Override
        public List<AxisAlignedBB> getBounds(BlockCable cable, World world, int x, int y, int z,
            ForgeDirection forgeDirection) {
            return Collections.singletonList(BOUNDS);
        }

        @Override
        public ItemStack getPickBlock(World world, int x, int y, int z, ForgeDirection position) {
            ItemStack itemStack = new ItemStack(ItemFacade.getInstance());
            ItemFacade.getInstance()
                .writeFacadeBlock(
                    itemStack,
                    BlockCable.getInstance()
                        .getFacade(world, new BlockPos(x, y, z)));
            return itemStack;
        }

        @Override
        public boolean destroy(World world, int x, int y, int z, ForgeDirection forgeDirection, EntityPlayer player) {
            BlockPos pos = new BlockPos(x, y, z);
            if (!(pos.getBlock(world) instanceof BlockCable cable)) return false;
            if (!world.isRemote) {
                BlockState blockState = cable.getFacade(world, pos);
                ItemStack itemStack = new ItemStack(ItemFacade.getInstance());
                ItemFacade.getInstance()
                    .writeFacadeBlock(itemStack, blockState);
                BlockCable.getInstance()
                    .setFacade(world, pos, null);
                ItemStackHelpers.spawnItemStackToPlayer(world, pos, itemStack, player);
            }
            return false;
        }
    };
    static {
        COLLIDABLE_COMPONENTS.add(FACADE_COMPONENT);
        COLLIDABLE_COMPONENTS.add(CENTER_COMPONENT);
        COLLIDABLE_COMPONENTS.add(CABLECONNECTIONS_COMPONENT);
        COLLIDABLE_COMPONENTS.add(PARTS_COMPONENT);
    }

    @Delegate
    private ICollidable collidableComponent = new CollidableComponent(this, COLLIDABLE_COMPONENTS);
    // @Delegate// <- Lombok can't handle delegations with generics, so we'll have to do it manually...
    private CableNetworkFacadeableComponent<BlockCable> cableNetworkComponent = new CableNetworkFacadeableComponent<>(
        this);
    private NetworkElementProviderComponent<IPartNetwork> networkElementProviderComponent = new NetworkElementProviderComponent<>(
        this);

    private static BlockCable _instance = null;

    public static boolean IS_MCMP_CONVERTING = false;

    @SideOnly(Side.CLIENT)
    @Icon(location = "blocks/cable")
    public IIcon texture;
    @Setter
    private boolean disableCollisionBox = false;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static BlockCable getInstance() {
        return _instance;
    }

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockCable(ExtendedConfig eConfig) {
        super(eConfig, BLOCK_MATERIAL, TileMultipartTicking.class);

        setHardness(BLOCK_HARDNESS);
        setStepSound(soundTypeMetal);
        if (MinecraftHelpers.isClientSide()) {
            eConfig.getMod()
                .getIconProvider()
                .registerIconHolderObject(this);
        }
    }

    protected boolean hasPart(IBlockAccess world, BlockPos pos, ForgeDirection side) {
        if (world == null) return false;
        TileEntity tile = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (tile instanceof TileMultipartTicking tileMultipart) {
            return tileMultipart.hasPart(side);
        }
        return false;
    }

    @Override
    public boolean isRealCable(World world, BlockPos pos) {
        if (world == null || pos == null) return true;
        TileMultipartTicking tile = TileHelpers.getSafeTile(world, pos, TileMultipartTicking.class);
        return tile != null && tile.isRealCable();
    }

    @Override
    public void setRealCable(World world, BlockPos pos, boolean realCable) {
        TileMultipartTicking tile = TileHelpers.getSafeTile(world, pos, TileMultipartTicking.class);
        if (tile != null) {
            tile.setRealCable(realCable);
            if (realCable) {
                cableNetworkComponent.addToNetwork(world, pos);
            } else {
                networkElementProviderComponent.onPreBlockDestroyed(getNetwork(world, pos), world, pos, false);
                if (!cableNetworkComponent.removeCableFromNetwork(world, pos)) {
                    tile.setRealCable(!realCable);
                    IntegratedDynamics.clog(
                        Level.WARN,
                        "Tried to set a fake cable, but the original network element was not present");
                }
            }
        }
    }

    @Override
    protected void onPreBlockDestroyed(World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (isRealCable(world, pos)) {
            networkElementProviderComponent.onPreBlockDestroyed(getNetwork(world, pos), world, pos, true);
            cableNetworkComponent.onPreBlockDestroyed(world, pos);
        }
        super.onPreBlockDestroyed(world, x, y, z);
    }

    @Override
    protected void onPostBlockDestroyed(World world, int x, int y, int z) {
        super.onPostBlockDestroyed(world, x, y, z);
        if (!IS_MCMP_CONVERTING) { // Yes, this is a hack, we don't want this to be called after a MCMP block conversion
            IS_MCMP_CONVERTING = false;
            cableNetworkComponent.onPostBlockDestroyed(world, new BlockPos(x, y, z));
        }
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        RayTraceResult<ForgeDirection> rayTraceResult = doRayTrace(world, x, y, z, player);
        if (rayTraceResult != null && rayTraceResult.getCollisionType() != null) {
            return rayTraceResult.getCollisionType()
                .destroy(world, x, y, z, rayTraceResult.getPositionHit(), player);
        }
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideInt, float hitX,
        float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem();
        BlockPos pos = new BlockPos(x, y, z);
        ForgeDirection side = ForgeDirection.getOrientation(sideInt);
        TileMultipartTicking tile = TileHelpers.getSafeTile(world, pos, TileMultipartTicking.class);
        if (tile != null) {
            RayTraceResult<ForgeDirection> rayTraceResult = doRayTrace(world, x, y, z, player);
            if (rayTraceResult != null) {
                ForgeDirection positionHit = rayTraceResult.getPositionHit();
                if (rayTraceResult.getCollisionType() == FACADE_COMPONENT) {
                    if (!world.isRemote && WrenchHelpers.isWrench(player, heldItem, pos) && player.isSneaking()) {
                        FACADE_COMPONENT.destroy(world, x, y, z, side, player);
                        world.notifyBlocksOfNeighborChange(x, y, z, this);
                        return true;
                    }
                    return false;
                } else if (rayTraceResult.getCollisionType() == PARTS_COMPONENT) {
                    if (!world.isRemote && WrenchHelpers.isWrench(player, heldItem, pos)) {
                        // Remove part from cable
                        if (player.isSneaking()) {
                            PARTS_COMPONENT.destroy(world, x, y, z, rayTraceResult.getPositionHit(), player);
                            ItemBlockCable.playBreakSound(world, pos);
                        }
                        return true;
                    } else if (isRealCable(world, pos)) {
                        // Delegate activated call to part
                        return getPartContainer(world, pos).getPart(positionHit)
                            .onPartActivated(
                                world,
                                pos,
                                getPartContainer(world, pos).getPartState(positionHit),
                                player,
                                heldItem,
                                positionHit,
                                hitX,
                                hitY,
                                hitZ);
                    }
                } else if (!world.isRemote && (rayTraceResult.getCollisionType() == CABLECONNECTIONS_COMPONENT
                    || rayTraceResult.getCollisionType() == CENTER_COMPONENT)) {
                        if (onCableActivated(
                            world,
                            pos,
                            player,
                            heldItem,
                            side,
                            rayTraceResult.getCollisionType() == CENTER_COMPONENT ? null
                                : rayTraceResult.getPositionHit())) {
                            return true;
                        }
                    }
            }
        }
        return super.onBlockActivated(world, x, y, z, player, sideInt, hitX, hitY, hitZ);
    }

    public static boolean onCableActivated(World world, BlockPos pos, EntityPlayer player, ItemStack heldItem,
        ForgeDirection side, ForgeDirection cableConnectionHit) {
        ICableNetwork<?, ?> cable = CableHelpers.getInterface(world, pos, ICableNetwork.class);
        if (WrenchHelpers.isWrench(player, heldItem, pos)) {
            if (player.isSneaking()) {
                if (!(cable instanceof IPartContainerFacade)
                    || !((IPartContainerFacade) cable).getPartContainer(world, pos)
                        .hasParts()
                    || !(cable instanceof ICableFakeable)) {
                    // Remove full cable
                    cable.remove(world, pos, player);
                    ItemBlockCable.playBreakSound(world, pos);
                } else {
                    // Mark cable as unavailable.
                    ((ICableFakeable) cable).setRealCable(world, pos, false);
                    ItemBlockCable.playBreakSound(world, pos);
                    ItemStackHelpers
                        .spawnItemStackToPlayer(world, pos, new ItemStack(BlockCable.getInstance()), player);
                }
            } else if (cableConnectionHit != null) {
                // Disconnect cable side

                // Store the disconnection in the tile entity
                cable.disconnect(world, pos, cableConnectionHit);

                // Signal changes
                cable.updateConnections(world, pos);
                cable.triggerUpdateNeighbourConnections(world, pos);

                // Reinit the networks for this block and the disconnected neighbour.
                cable.initNetwork(world, pos);
                BlockPos neighbourPos = pos.offset(cableConnectionHit);
                ICableNetwork neighbourCable = CableHelpers.getInterface(world, neighbourPos, ICableNetwork.class);
                if (neighbourCable != null) {
                    neighbourCable.initNetwork(world, neighbourPos);
                }
                return true;
            } else if (cableConnectionHit == null) {
                // Reconnect cable side
                BlockPos neighbourPos = pos.offset(side);
                ICable neighbourCable = CableHelpers.getInterface(world, neighbourPos, ICable.class);
                if (neighbourCable != null && !cable.isConnected(world, pos, side)
                    && (cable.canConnect(world, pos, neighbourCable, side)
                        || neighbourCable.canConnect(world, neighbourPos, cable, side.getOpposite()))) {
                    // Notify the reconnection in the tile entity of this and the neighbour block,
                    // since we don't know in which one the disconnection was made.
                    cable.reconnect(world, pos, side);
                    neighbourCable.reconnect(world, neighbourPos, side.getOpposite());

                    // Signal changes
                    cable.updateConnections(world, pos);
                    cable.triggerUpdateNeighbourConnections(world, pos);

                    // Reinit the networks for this block and the connected neighbour.
                    cable.initNetwork(world, pos);
                    if (neighbourCable instanceof ICableNetwork) {
                        ((ICableNetwork<IPartNetwork, ICablePathElement>) neighbourCable)
                            .initNetwork(world, neighbourPos);
                    }
                }
                return true;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        cableNetworkComponent.addToNetwork(world, new BlockPos(x, y, z));
    }

    @Override
    public boolean saveNBTToDroppedItem() {
        return false;
    }

    @Override
    public boolean isDropBlockItem(IBlockAccess world, int x, int y, int z, int fortune) {
        if (world == null) return true;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileMultipartTicking tileMultipart) {
            return tileMultipart.isRealCable();
        }
        return true;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z,
        @Nullable EntityPlayer player) {
        RayTraceResult<ForgeDirection> rayTraceResult = doRayTrace(world, x, y, z, player);
        if (rayTraceResult != null) {
            ForgeDirection positionHit = rayTraceResult.getPositionHit();
            return rayTraceResult.getCollisionType()
                .getPickBlock(world, x, y, z, positionHit);
        }
        return new ItemStack(getItem(world, x, y, z), 1, getDamageValue(world, x, y, z));
    }

    @Override
    public Collection<INetworkElement> createNetworkElements(World world, BlockPos blockPos) {
        Set<INetworkElement> sidedElements = Sets.newHashSet();
        for (Map.Entry<ForgeDirection, IPartType<?, ?>> entry : getPartContainer(world, blockPos).getParts()
            .entrySet()) {
            sidedElements.add(
                entry.getValue()
                    .createNetworkElement(this, DimPos.of(world, blockPos), entry.getKey()));
        }
        return sidedElements;
    }

    @Override
    public IPartContainer getPartContainer(IBlockAccess world, BlockPos pos) {
        return TileHelpers.getSafeTile(world, pos, IPartContainer.class);
    }

    @Nullable
    @Override
    public ForgeDirection getWatchingSide(World world, BlockPos pos, EntityPlayer player) {
        ICollidable.RayTraceResult<ForgeDirection> rayTraceResult = doRayTrace(
            world,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            player);
        if (rayTraceResult != null) {
            return rayTraceResult.getPositionHit();
        }
        return null;
    }

    /* --------------- Start ICollidable and rendering --------------- */

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return hasFacade(world, new BlockPos(x, y, z)) ? 255 : 0;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return super.shouldSideBeRendered(world, x, y, z, side) || hasFacade(world, new BlockPos(x, y, z));
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
        BlockPos blockPos = new BlockPos(target.blockX, target.blockY, target.blockZ);
        if (hasFacade(world, blockPos)) {
            RenderHelpers.addBlockHitEffects(
                world,
                this,
                blockPos.getBlockMetadata(world),
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ(),
                ForgeDirection.getOrientation(target.sideHit));
            return true;
        } else {
            return super.addHitEffects(world, target, effectRenderer);
        }
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        BlockPos pos = new BlockPos(x, y, z);
        if (hasFacade(world, pos)) {
            return true;
        }
        if (hasPart(world, pos, side)) {
            IPartContainer partContainer = getPartContainer(world, pos);
            if (partContainer != null) {
                IPartType partType = partContainer.getPart(side);
                if (partType != null) {
                    return partType.isSolid(partContainer.getPartState(side));
                }
            }
        }
        return super.isSideSolid(world, x, y, z, side);
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return true;
    }

    public AxisAlignedBB getCableBoundingBox(ForgeDirection side) {
        if (side == null) {
            return CABLE_CENTER_BOUNDINGBOX;
        } else {
            return CABLE_SIDE_BOUNDINGBOXES.get(side);
        }
    }

    protected IPartType.RenderPosition getPartRenderPosition(IBlockAccess world, BlockPos pos, ForgeDirection side) {
        if (world == null || pos == null || side == null || side == ForgeDirection.UNKNOWN) {
            return IPartType.RenderPosition.NONE;
        }

        TileEntity tile = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (tile instanceof TileMultipartTicking tileMultipart) {
            if (tileMultipart.hasPart(side)) {
                IPartType part = tileMultipart.getPart(side);
                if (part != null) {
                    IPartType.RenderPosition posType = part.getRenderPosition();
                    return posType != null ? posType : IPartType.RenderPosition.NONE;
                }
            }
        }

        return IPartType.RenderPosition.NONE;
    }

    private AxisAlignedBB getCableBoundingBoxWithPart(World world, BlockPos pos, ForgeDirection side) {
        if (side == null) {
            return CABLE_CENTER_BOUNDINGBOX;
        }

        IPartType.RenderPosition renderPosition = getPartRenderPosition(world, pos, side);
        if (renderPosition == null) {
            return getCableBoundingBox(side);
        }

        return renderPosition.getSidedCableBoundingBox(side);
    }

    private AxisAlignedBB getPartBoundingBox(World world, BlockPos pos, ForgeDirection side) {
        if (side == null) return null;

        IPartType.RenderPosition renderPosition = null;
        try {
            renderPosition = getPartRenderPosition(world, pos, side);
        } catch (Throwable t) {}

        if (renderPosition != null) {
            AxisAlignedBB box = renderPosition.getBoundingBox(side);
            if (box != null) {
                return box;
            }
        }

        return getFallbackPartBoundingBox(side);
    }

    private AxisAlignedBB getFallbackPartBoundingBox(ForgeDirection side) {
        if (side == null) return null;

        float depth = 0.125f; // Độ dày part (1/8 block)
        float min = 0.375f; // Lề trái/dưới (3/8 block)
        float max = 0.625f; // Lề phải/trên (5/8 block)

        switch (side) {
            case DOWN:
                return AxisAlignedBB.getBoundingBox(min, 0.0F, min, max, depth, max);
            case UP:
                return AxisAlignedBB.getBoundingBox(min, 1.0F - depth, min, max, 1.0F, max);
            case NORTH:
                return AxisAlignedBB.getBoundingBox(min, min, 0.0F, max, max, depth);
            case SOUTH:
                return AxisAlignedBB.getBoundingBox(min, min, 1.0F - depth, max, max, 1.0F);
            case WEST:
                return AxisAlignedBB.getBoundingBox(0.0F, min, min, depth, max, max);
            case EAST:
                return AxisAlignedBB.getBoundingBox(1.0F - depth, min, min, 1.0F, max, max);
            default:
                return AxisAlignedBB.getBoundingBox(min, min, min, max, max, max);
        }
    }

    @Override
    public void addCollisionBoxesToListParent(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collidingEntity) {
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collidingEntity);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPoolParent(World worldIn, int x, int y, int z) {
        return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public MovingObjectPosition collisionRayTraceParent(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
        return super.collisionRayTrace(world, x, y, z, origin, direction);
    }

    /* --------------- Start IDynamicRedstoneBlock --------------- */

    @Override
    public void disableRedstoneAt(IBlockAccess world, BlockPos pos, ForgeDirection side) {
        TileMultipartTicking tile = TileHelpers.getSafeTile(world, pos, TileMultipartTicking.class);
        if (tile != null) {
            tile.disableRedstoneLevel(side);
        }
    }

    @Override
    public void setRedstoneLevel(IBlockAccess world, BlockPos pos, ForgeDirection side, int level) {
        TileMultipartTicking tile = TileHelpers.getSafeTile(world, pos, TileMultipartTicking.class);
        if (tile != null) {
            tile.setRedstoneLevel(side, level);
        }
    }

    @Override
    public int getRedstoneLevel(IBlockAccess world, BlockPos pos, ForgeDirection side) {
        TileMultipartTicking tile = TileHelpers.getSafeTile(world, pos, TileMultipartTicking.class);
        if (tile != null) {
            return tile.getRedstoneLevel(side);
        }
        return -1;
    }

    @Override
    public void setAllowRedstoneInput(IBlockAccess world, BlockPos pos, ForgeDirection side, boolean allow) {
        TileMultipartTicking tile = TileHelpers.getSafeTile(world, pos, TileMultipartTicking.class);
        if (tile != null) {
            tile.setAllowRedstoneInput(side, allow);
        }
    }

    @Override
    public boolean isAllowRedstoneInput(IBlockAccess world, BlockPos pos, ForgeDirection side) {
        TileMultipartTicking tile = TileHelpers.getSafeTile(world, pos, TileMultipartTicking.class);
        if (tile != null) {
            return tile.isAllowRedstoneInput(side);
        }
        return false;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int sideInt) {
        if (sideInt < 0) return false;
        ForgeDirection side = ForgeDirection.getOrientation(sideInt);
        BlockPos pos = new BlockPos(x, y, z);
        if (side == ForgeDirection.UNKNOWN) {
            for (ForgeDirection validSide : ForgeDirection.VALID_DIRECTIONS) {
                if (isAllowRedstoneInput(world, pos, validSide)) return true;
            }
            return false;
        }
        return isAllowRedstoneInput(world, pos, side);
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return 0;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return getRedstoneLevel(
            world,
            new BlockPos(x, y, z),
            ForgeDirection.getOrientation(side)
                .getOpposite());
    }

    /* --------------- Start IDynamicLightBlock --------------- */

    @Override
    public void setLightLevel(IBlockAccess world, BlockPos pos, ForgeDirection side, int level) {
        TileMultipartTicking tile = TileHelpers.getSafeTile(world, pos, TileMultipartTicking.class);
        if (tile != null) {
            tile.setLightLevel(side, level);
        }
    }

    @Override
    public int getLightLevel(IBlockAccess world, BlockPos pos, ForgeDirection side) {
        TileMultipartTicking tile = TileHelpers.getSafeTile(world, pos, TileMultipartTicking.class);
        if (tile != null) {
            return tile.getLightLevel(side);
        }
        return 0;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        int light = 0;
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            light = Math.max(light, getLightLevel(world, pos, side));
        }
        return light;
    }

    /* --------------- Delegate to ICableNetwork<CablePathElement> --------------- */

    @Override
    public void initNetwork(World world, BlockPos pos) {
        if (isRealCable(world, pos)) {
            cableNetworkComponent.initNetwork(world, pos);
        }
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
        TileEntity tile = pos.getTileEntity(world);
        if (tile instanceof ITileCableNetwork) {
            return ((ITileCableNetwork) tile).isConnected(side);
        }
        return false;
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
        // PRE
        networkElementProviderComponent.onPreBlockDestroyed(getNetwork(world, pos), world, pos, true);
        cableNetworkComponent.onPreBlockDestroyed(world, pos);
        // POST
        cableNetworkComponent.remove(world, pos, player);
    }

    @Override
    public boolean hasFacade(IBlockAccess world, BlockPos pos) {
        return cableNetworkComponent.hasFacade(world, pos);
    }

    @Override
    public BlockState getFacade(World world, BlockPos pos) {
        return cableNetworkComponent.getFacade(world, pos);
    }

    @Override
    public void setFacade(World world, BlockPos pos, @Nullable BlockState blockState) {
        cableNetworkComponent.setFacade(world, pos, blockState);
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

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        BlockPos pos = new BlockPos(x, y, z);
        super.onNeighborBlockChange(world, x, y, z, neighbor);
        cableNetworkComponent.updateConnections(world, pos);
        networkElementProviderComponent.onBlockNeighborChange(getNetwork(world, pos), world, pos, neighbor);
    }

    @Override
    public BakedModel getModel(BakedModelQuadContext context) {
        return new CableModel();
    }

    @Override
    public boolean nhlib$isModeled() {
        return true;
    }

    @Override
    public void nhlib$setModeled(boolean modeled) {

    }
}
