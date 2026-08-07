package ruiseki.integrateddynamics.core.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.block.cable.ICableFakeable;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.block.BlockCable;
import ruiseki.integrateddynamics.capability.partcontainer.PartContainerConfig;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.item.ItemBlockCable;
import ruiseki.okcore.config.configurable.ConfigurableItem;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An item that can place parts.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ItemPart<P extends IPartType<P, S>, S extends IPartState<P>> extends ConfigurableItem {

    private static final List<IUseAction> USE_ACTIONS = Lists.newArrayList();

    private final IPartType<P, S> part;

    /**
     * Make a new item instance.
     *
     * @param eConfig Config for this blockState.
     * @param part    The part this item will place.
     */
    public ItemPart(ExtendedConfig eConfig, IPartType<P, S> part) {
        super(eConfig);
        this.part = part;
    }

    /**
     * Register a use action for the cable item.
     *
     * @param useAction The use action.
     */
    public static void addUseAction(IUseAction useAction) {
        USE_ACTIONS.add(useAction);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return LangHelpers.localize(part.getUnlocalizedName());
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer playerIn, World world, int x, int y, int z, int sideInt,
        float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            ForgeDirection side = ForgeDirection.getOrientation(sideInt);
            BlockPos pos = new BlockPos(x, y, z);
            IPartContainer partContainerFirst = PartContainerConfig.get(world, pos);
            if (partContainerFirst != null) {
                // Add part to existing cable
                if (addPart(world, pos, side, partContainerFirst, itemStack)) {
                    itemStack.stackSize--;
                }
                return true;
            } else {
                // Check all third party actions
                for (IUseAction useAction : USE_ACTIONS) {
                    if (useAction.attempItemUseTarget(this, itemStack, world, pos, side)) {
                        return true;
                    }
                }

                // Place part at a new position with an unreal cable
                BlockPos target = pos.offset(side);
                if (target.getBlock(world)
                    .isReplaceable(world, target.getX(), target.getY(), target.getZ())) {
                    ItemBlockCable itemBlockCable = (ItemBlockCable) Item.getItemFromBlock(BlockCable.getInstance());
                    if (itemBlockCable.onItemUse(
                        itemStack,
                        playerIn,
                        world,
                        target.getX(),
                        target.getY(),
                        target.getZ(),
                        sideInt,
                        hitX,
                        hitY,
                        hitZ)) {
                        IPartContainer partContainer = PartContainerConfig.get(world, pos);
                        if (partContainer != null) {
                            addPart(world, pos, side.getOpposite(), partContainer, itemStack);
                            if (target.getBlock(world) instanceof ICableFakeable) {
                                BlockCable.getInstance()
                                    .setRealCable(world, target, false);
                            } else {
                                IntegratedDynamics.clog(
                                    Level.WARN,
                                    String.format(
                                        "Tried to set a fake cable at a block that is not fakeable, got %s",
                                        target.getBlock(world)));
                            }
                            return true;
                        }
                    }
                } else {
                    IPartContainer partContainer = PartContainerConfig.get(world, pos);
                    if (partContainer != null) {
                        if (!world.isRemote && addPart(world, pos, side.getOpposite(), partContainer, itemStack)
                            && !playerIn.capabilities.isCreativeMode) {
                            itemStack.stackSize--;
                        }
                        return true;
                    }
                }
            }
        }
        return super.onItemUse(itemStack, playerIn, world, x, y, z, sideInt, hitX, hitY, hitZ);
    }

    protected boolean addPart(World world, BlockPos pos, ForgeDirection side, IPartContainer partContainer,
        ItemStack itemStack) {
        IPartType partType = getPart();
        if (partContainer.canAddPart(side, partType)) {
            partContainer.setPart(side, getPart(), partType.getState(itemStack));
            ItemBlockCable.playPlaceSound(world, pos);
            return true;
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        if (itemStack.getTagCompound() != null) {
            int id = itemStack.getTagCompound()
                .getInteger("id");
            list.add(LangHelpers.localize(L10NValues.GENERAL_ITEM_ID, id));
        }
        LangHelpers.addOptionalInfo(list, getPart().getUnlocalizedNameBase());
        super.addInformation(itemStack, entityPlayer, list, par4);
    }

    public static interface IUseAction {

        /**
         * Attempt to use the given item.
         *
         * @param itemPart  The part item instance.
         * @param itemStack The item stack that is being used.
         * @param world     The world.
         * @param pos       The position.
         * @param sideHit   The side that is being hit.
         * @return If the use action was applied.
         */
        public boolean attempItemUseTarget(ItemPart itemPart, ItemStack itemStack, World world, BlockPos pos,
            ForgeDirection sideHit);

    }

}
