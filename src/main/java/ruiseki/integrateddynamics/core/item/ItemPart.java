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
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.item.ItemBlockCable;
import ruiseki.okcore.config.configurable.ConfigurableItem;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

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
        BlockPos pos = new BlockPos(x, y, z);
        ForgeDirection side = ForgeDirection.getOrientation(sideInt);
        IPartContainer partContainerFirst = PartHelpers.getPartContainer(world, pos, side);
        if (partContainerFirst != null) {
            // Add part to existing cable
            if (PartHelpers.addPart(world, pos, side, getPart(), itemStack)) {
                if (world.isRemote) {
                    ItemBlockCable.playPlaceSound(world, pos);
                }
                if (!playerIn.capabilities.isCreativeMode) {
                    itemStack.stackSize--;
                }
            }
            return true;
        } else {
            // Place part at a new position with an unreal cable
            BlockPos target = pos.offset(side);
            ForgeDirection targetSide = side.getOpposite();
            if (target.getBlock(world)
                .isReplaceable(world, target.getX(), target.getY(), target.getZ())) {
                ItemBlockCable itemBlockCable = (ItemBlockCable) Item.getItemFromBlock(BlockCable.getInstance());
                itemStack.stackSize++; // Temporarily grow, because ItemBlock will shrink it.
                if (itemBlockCable.onItemUse(
                    itemStack,
                    playerIn,
                    world,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    ForgeDirection.getOrientation(sideInt)
                        .getOpposite()
                        .ordinal(),
                    hitX,
                    hitY,
                    hitZ)) {
                    IPartContainer partContainer = PartHelpers.getPartContainer(world, target, targetSide);
                    if (partContainer != null) {
                        ICableFakeable cableFakeable = CableHelpers.getCableFakeable(world, target, targetSide);
                        if (!world.isRemote) {
                            PartHelpers.addPart(world, target, side.getOpposite(), getPart(), itemStack);
                            if (cableFakeable != null) {
                                CableHelpers.onCableRemoving(world, target, false, false);
                                cableFakeable.setRealCable(false);
                                CableHelpers.onCableRemoved(world, target, CableHelpers.ALL_SIDES);
                            } else {
                                IntegratedDynamics.clog(
                                    Level.WARN,
                                    String.format(
                                        "Tried to set a fake cable at a block that is not fakeable at %s",
                                        target));
                            }
                        } else {
                            cableFakeable.setRealCable(false);
                        }
                        itemStack.stackSize--;
                        return true;
                    }
                }
                itemStack.stackSize--; // Shrink manually if failed
            } else {
                IPartContainer partContainer = PartHelpers.getPartContainer(world, target, targetSide);
                if (partContainer != null) {
                    // Add part to existing cable
                    if (PartHelpers.addPart(world, target, side.getOpposite(), getPart(), itemStack)) {
                        if (world.isRemote) {
                            ItemBlockCable.playPlaceSound(world, target);
                        }
                        if (!playerIn.capabilities.isCreativeMode) {
                            itemStack.stackSize--;
                        }
                    }
                    return true;
                }
            }

            // Check third party actions if all else fails
            for (IUseAction useAction : USE_ACTIONS) {
                if (useAction.attempItemUseTarget(this, itemStack, world, pos, side)) {
                    return true;
                }
            }
        }
        return super.onItemUse(itemStack, playerIn, world, x, y, z, sideInt, hitX, hitY, hitZ);
    }

    @SuppressWarnings("rawtypes")
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        if (itemStack.getTagCompound() != null && itemStack.getTagCompound()
            .hasKey("id", MinecraftHelpers.NBTTag_Types.NBTTagInt.ordinal())) {
            int id = itemStack.getTagCompound()
                .getInteger("id");
            list.add(LangHelpers.localize(L10NValues.GENERAL_ITEM_ID, id));
        }
        getPart().loadTooltip(itemStack, list);
        LangHelpers.addOptionalInfo(list, getPart().getUnlocalizedNameBase());
        super.addInformation(itemStack, entityPlayer, list, par4);
    }

    public static interface IUseAction {

        public boolean attempItemUseTarget(ItemPart itemPart, ItemStack itemStack, World world, BlockPos pos,
            ForgeDirection sideHit);

    }
}
