package ruiseki.integrateddynamics.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.gtnewhorizon.gtnhlib.api.BlockModelInfo;
import com.gtnewhorizon.gtnhlib.api.IBlockModelProvider;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.client.model.BakedModelQuadContext;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Setter;
import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.api.block.IDynamicLight;
import ruiseki.integrateddynamics.api.block.IDynamicRedstone;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.block.collidable.CollidableComponentCableCenter;
import ruiseki.integrateddynamics.block.collidable.CollidableComponentCableConnections;
import ruiseki.integrateddynamics.block.collidable.CollidableComponentFacade;
import ruiseki.integrateddynamics.block.collidable.CollidableComponentParts;
import ruiseki.integrateddynamics.capability.dynamiclight.DynamicLightConfig;
import ruiseki.integrateddynamics.capability.dynamicredstone.DynamicRedstoneConfig;
import ruiseki.integrateddynamics.capability.facadeable.FacadeableConfig;
import ruiseki.integrateddynamics.client.model.CableModel;
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.helper.WrenchHelpers;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.integrateddynamics.item.ItemBlockCable;
import ruiseki.okcore.block.collidable.CollidableComponent;
import ruiseki.okcore.block.collidable.ICollidable;
import ruiseki.okcore.block.collidable.ICollidableParent;
import ruiseki.okcore.block.collidable.ImmutableAxisAlignedBB;
import ruiseki.okcore.client.icon.Icon;
import ruiseki.okcore.config.configurable.ConfigurableBlockContainer;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.EnumFacingMap;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.RenderHelpers;
import ruiseki.okcore.helper.TileHelpers;

public class BlockCable extends ConfigurableBlockContainer
    implements ICollidable<ForgeDirection>, ICollidableParent, IBlockModelProvider, BlockModelInfo {

    public static final float BLOCK_HARDNESS = 3.0F;
    public static final Material BLOCK_MATERIAL = Material.glass;

    // Collision boxes
    public final static ImmutableAxisAlignedBB CABLE_CENTER_BOUNDINGBOX = ImmutableAxisAlignedBB
        .fromBounds(CableModel.MIN, CableModel.MIN, CableModel.MIN, CableModel.MAX, CableModel.MAX, CableModel.MAX);
    private final static EnumFacingMap<ImmutableAxisAlignedBB> CABLE_SIDE_BOUNDINGBOXES = EnumFacingMap.forAllValues(
        ImmutableAxisAlignedBB
            .fromBounds(CableModel.MIN, 0, CableModel.MIN, CableModel.MAX, CableModel.MIN, CableModel.MAX), // DOWN
        ImmutableAxisAlignedBB
            .fromBounds(CableModel.MIN, CableModel.MAX, CableModel.MIN, CableModel.MAX, 1, CableModel.MAX), // UP
        ImmutableAxisAlignedBB
            .fromBounds(CableModel.MIN, CableModel.MIN, 0, CableModel.MAX, CableModel.MAX, CableModel.MIN), // NORTH
        ImmutableAxisAlignedBB
            .fromBounds(CableModel.MIN, CableModel.MAX, CableModel.MAX, CableModel.MAX, CableModel.MIN, 1), // SOUTH
        ImmutableAxisAlignedBB
            .fromBounds(0, CableModel.MIN, CableModel.MIN, CableModel.MIN, CableModel.MAX, CableModel.MAX), // WEST
        ImmutableAxisAlignedBB
            .fromBounds(CableModel.MAX, CableModel.MIN, CableModel.MIN, 1, CableModel.MAX, CableModel.MAX) // EAST
    );

    // Collision components
    public static final List<IComponent<ForgeDirection, BlockCable>> COLLIDABLE_COMPONENTS = Lists.newArrayList();
    public static final IComponent<ForgeDirection, BlockCable> CABLECENTER_COMPONENT = new CollidableComponentCableCenter();
    public static final IComponent<ForgeDirection, BlockCable> CABLECONNECTIONS_COMPONENT = new CollidableComponentCableConnections();
    public static final IComponent<ForgeDirection, BlockCable> PARTS_COMPONENT = new CollidableComponentParts();
    public static final IComponent<ForgeDirection, BlockCable> FACADE_COMPONENT = new CollidableComponentFacade();
    static {
        COLLIDABLE_COMPONENTS.add(FACADE_COMPONENT);
        COLLIDABLE_COMPONENTS.add(CABLECENTER_COMPONENT);
        COLLIDABLE_COMPONENTS.add(CABLECONNECTIONS_COMPONENT);
        COLLIDABLE_COMPONENTS.add(PARTS_COMPONENT);
    }

    @Delegate
    private ICollidable<ForgeDirection> collidableComponent = new CollidableComponent<>(this, COLLIDABLE_COMPONENTS);

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

    @Override
    protected void onPreBlockDestroyed(World world, int x, int y, int z) {
        CableHelpers.onCableRemoving(world, new BlockPos(x, y, z), true);
        super.onPreBlockDestroyed(world, x, y, z);
    }

    @Override
    protected void onPostBlockDestroyed(World world, int x, int y, int z) {
        super.onPostBlockDestroyed(world, x, y, z);
        if (!IS_MCMP_CONVERTING) { // Yes, this is a hack, we don't want this to be called after a MCMP block conversion
            CableHelpers.onCableRemoved(world, new BlockPos(x, y, z));
        }
        IS_MCMP_CONVERTING = false;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        BlockPos pos = new BlockPos(x, y, z);
        RayTraceResult<ForgeDirection> rayTraceResult = doRayTrace(world, pos, player);
        if (rayTraceResult != null && rayTraceResult.getCollisionType() != null) {
            return rayTraceResult.getCollisionType()
                .destroy(world, pos, rayTraceResult.getPositionHit(), player, willHarvest);
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
            RayTraceResult<ForgeDirection> rayTraceResult = doRayTrace(world, pos, player);
            if (rayTraceResult != null) {
                ForgeDirection positionHit = rayTraceResult.getPositionHit();
                if (rayTraceResult.getCollisionType() == FACADE_COMPONENT) {
                    if (!world.isRemote && WrenchHelpers.isWrench(player, heldItem, world, pos, side)
                        && player.isSneaking()) {
                        FACADE_COMPONENT.destroy(world, pos, side, player, true);
                        world.notifyBlocksOfNeighborChange(x, y, z, this);
                        return true;
                    }
                    return false;
                } else if (rayTraceResult.getCollisionType() == PARTS_COMPONENT) {
                    if (!world.isRemote && WrenchHelpers.isWrench(player, heldItem, world, pos, side)
                        && player.isSneaking()) {
                        // Remove part from cable
                        PARTS_COMPONENT.destroy(world, pos, rayTraceResult.getPositionHit(), player, true);
                        ItemBlockCable.playBreakSound(world, pos);
                        return true;
                    } else if (CableHelpers.isNoFakeCable(world, pos)) {
                        // Delegate activated call to part
                        IPartContainer partContainer = PartHelpers.getPartContainer(world, pos);
                        return partContainer.getPart(positionHit)
                            .onPartActivated(
                                world,
                                pos,
                                partContainer.getPartState(positionHit),
                                player,
                                heldItem,
                                positionHit,
                                hitX,
                                hitY,
                                hitZ);
                    }
                } else if (!world.isRemote && (rayTraceResult.getCollisionType() == CABLECONNECTIONS_COMPONENT
                    || rayTraceResult.getCollisionType() == CABLECENTER_COMPONENT)) {
                        if (CableHelpers.onCableActivated(
                            world,
                            pos,
                            player,
                            heldItem,
                            side,
                            rayTraceResult.getCollisionType() == CABLECENTER_COMPONENT ? null
                                : rayTraceResult.getPositionHit())) {
                            return true;
                        }
                    }
            }
        }
        return super.onBlockActivated(world, x, y, z, player, sideInt, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        CableHelpers.onCableAdded(world, new BlockPos(x, y, z));
    }

    @Override
    public boolean saveNBTToDroppedItem() {
        return false;
    }

    @Override
    public boolean isDropBlockItem(IBlockAccess world, int x, int y, int z, int fortune) {
        return CableHelpers.isNoFakeCable(world, new BlockPos(x, y, z));
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z,
        @Nullable EntityPlayer player) {
        BlockPos pos = new BlockPos(x, y, z);
        RayTraceResult<ForgeDirection> rayTraceResult = doRayTrace(world, pos, player);
        if (rayTraceResult != null) {
            ForgeDirection positionHit = rayTraceResult.getPositionHit();
            return rayTraceResult.getCollisionType()
                .getPickBlock(world, pos, positionHit);
        }
        return new ItemStack(getItem(world, x, y, z), 1, getDamageValue(world, x, y, z));
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
        BlockPos pos = new BlockPos(x, y, z);
        CableHelpers.updateConnectionsNeighbours(world, pos); // TODO: do we need this here? I think we only have to
                                                              // update our own connections...
        NetworkHelpers.onElementProviderBlockNeighborChange(world, pos, neighborBlock);
    }

    /* --------------- Start ICollidable and rendering --------------- */

    public ImmutableAxisAlignedBB getCableBoundingBox(ForgeDirection side) {
        if (side == null) {
            return CABLE_CENTER_BOUNDINGBOX;
        } else {
            return CABLE_SIDE_BOUNDINGBOXES.get(side);
        }
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return FacadeableConfig.hasFacade(world, new BlockPos(x, y, z)) ? 255 : 0;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return super.shouldSideBeRendered(world, x, y, z, side)
            || FacadeableConfig.hasFacade(world, new BlockPos(x, y, z));
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
        BlockPos blockPos = new BlockPos(target.blockX, target.blockY, target.blockZ);
        if (FacadeableConfig.hasFacade(world, blockPos)) {
            BlockState facadeState = FacadeableConfig.getFacade(world, blockPos);
            RenderHelpers.addBlockHitEffects(
                world,
                facadeState.getBlock(),
                facadeState.getBlockMeta(0),
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
        if (FacadeableConfig.hasFacade(world, pos)) {
            return true;
        }
        IPartContainer partContainer = PartHelpers.getPartContainer(world, pos);
        if (partContainer != null && partContainer.hasPart(side)) {
            IPartType partType = partContainer.getPart(side);
            return partType.isSolid(partContainer.getPartState(side));
        }
        return super.isSideSolid(world, x, y, z, side);
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return true;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPoolParent(World worldIn, int x, int y, int z) {
        return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public MovingObjectPosition rayTraceParent(BlockPos pos, Vec3 start, Vec3 end, AxisAlignedBB boundingBox) {
        Vec3 vecStart = start.addVector(-pos.getX(), -pos.getY(), -pos.getZ());
        Vec3 vecEnd = end.addVector(-pos.getX(), -pos.getY(), -pos.getZ());
        MovingObjectPosition intercept = boundingBox.calculateIntercept(vecStart, vecEnd);
        return intercept != null
            ? new MovingObjectPosition(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                intercept.sideHit,
                intercept.hitVec.addVector(pos.getX(), pos.getY(), pos.getZ()))
            : null;
    }

    /* --------------- Start IDynamicRedstone --------------- */

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int sideInt) {
        if (sideInt < 0) return false;
        ForgeDirection side = ForgeDirection.getOrientation(sideInt);
        BlockPos pos = new BlockPos(x, y, z);
        if (side == null) {
            for (ForgeDirection dummySide : ForgeDirection.VALID_DIRECTIONS) {
                IDynamicRedstone dynamicRedstone = CapabilityHelpers
                    .getCapability(world, pos, DynamicRedstoneConfig.CAPABILITY, dummySide)
                    .getOrNull();
                if (dynamicRedstone != null
                    && (dynamicRedstone.getRedstoneLevel() >= 0 || dynamicRedstone.isAllowRedstoneInput())) {
                    return true;
                }
            }
            return false;
        }
        IDynamicRedstone dynamicRedstone = CapabilityHelpers
            .getCapability(world, pos, DynamicRedstoneConfig.CAPABILITY, side.getOpposite())
            .getOrNull();
        return dynamicRedstone != null
            && (dynamicRedstone.getRedstoneLevel() >= 0 || dynamicRedstone.isAllowRedstoneInput());
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return 0;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        BlockPos pos = new BlockPos(x, y, z);
        IDynamicRedstone dynamicRedstone = CapabilityHelpers
            .getCapability(
                world,
                pos,
                DynamicRedstoneConfig.CAPABILITY,
                ForgeDirection.getOrientation(side)
                    .getOpposite())
            .getOrNull();
        return dynamicRedstone != null ? dynamicRedstone.getRedstoneLevel() : 0;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        int light = 0;
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IDynamicLight dynamicLight = CapabilityHelpers
                .getCapability((World) world, x, y, z, DynamicLightConfig.CAPABILITY, side)
                .getOrNull();
            if (dynamicLight != null) {
                light = Math.max(light, dynamicLight.getLightLevel());
            }
        }
        return light;
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
