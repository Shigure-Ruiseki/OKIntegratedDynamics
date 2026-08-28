package ruiseki.integratedterminals.item;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.part.PartTypes;
import ruiseki.integrateddynamics.part.PartTypeConnectorOmniDirectional;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalStorageItem;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageItem;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
import ruiseki.okcore.Reference;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.helper.ItemNBTHelpers;
import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.ItemGui;

/**
 * A portable storage terminal.
 *
 * @author rubensworks
 */
public class ItemTerminalStoragePortable extends ItemGui {

    public static String NBT_KEY_GROUP = Reference.MOD_ID + ":groupKey";
    public static String NBT_KEY_NAMED_INVENTORIES = Reference.MOD_ID + ":namedInventories";
    public static String NBT_KEY_STATES = Reference.MOD_ID + ":terminalStorageStates";

    public ItemTerminalStoragePortable(ExtendedConfig<ItemConfig, Item> eConfig) {
        super(eConfig);
    }

    @Override
    public void openGuiForItemIndex(World world, EntityPlayer player, int itemIndex) {
        if (world.isRemote) {
            super.openGuiForItemIndex(world, player, itemIndex);
        } else {
            ItemStack itemStack = InventoryHelpers.getItemFromIndex(player, itemIndex);
            int groupId = getGroupId(itemStack);
            if (groupId >= 0) {
                INetwork network = ContainerTerminalStorageItem.getNetworkFromItem(itemStack);
                if (network != null) {
                    super.openGuiForItemIndex(world, player, itemIndex);
                } else {
                    player.addChatComponentMessage(
                        new ChatComponentTranslation(
                            "item.items.integratedterminals.terminal_storage_portable.status.invalid_network"));
                }
            } else {
                player.addChatComponentMessage(
                    new ChatComponentTranslation(
                        "item.items.integratedterminals.terminal_storage_portable.status.no_network"));
            }
        }
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int sideInt,
        float hitX, float hitY, float hitZ) {
        ForgeDirection side = ForgeDirection.getOrientation(sideInt);
        if (!world.isRemote) {
            PartPos partPos = PartPos.of(world, new BlockPos(x, y, z), side);
            PartHelpers.PartStateHolder<?, ?> partStateHolder = PartHelpers.getPart(partPos);

            if (partStateHolder != null && partStateHolder.getPart() == PartTypes.CONNECTOR_OMNI) {
                PartTypeConnectorOmniDirectional.State state = (PartTypeConnectorOmniDirectional.State) partStateHolder
                    .getState();
                setGroupId(stack, state.getGroupId());

                player.addChatComponentMessage(
                    new ChatComponentTranslation(
                        "item.items.integratedterminals.terminal_storage_portable.status.linked"));

                return true;
            }
        }
        return false;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerTerminalStorageItem.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiTerminalStorageItem.class;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer entityPlayer, List<String> tooltip, boolean flag) {
        super.addInformation(stack, entityPlayer, tooltip, flag);
        int groupId = getGroupId(stack);
        if (groupId >= 0) {
            tooltip.add(LangHelpers.localize(L10NValues.PART_TOOLTIP_MONODIRECTIONALCONNECTOR_GROUP, groupId));
        }
    }

    public static int getGroupId(ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null || !tag.hasKey(NBT_KEY_GROUP, Constants.NBT.TAG_INT)) {
            return -1;
        } else {
            return tag.getInteger(NBT_KEY_GROUP);
        }
    }

    public static void setGroupId(ItemStack itemStack, int groupId) {
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        tag.setInteger(NBT_KEY_GROUP, groupId);
    }

    public static ITerminalStorageTabCommon.IVariableInventory getVariableInventory(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        // Navigate to relevant tag in item
        NBTTagCompound tagRoot = ItemNBTHelpers.getNBT(itemStack);
        if (!tagRoot.hasKey(NBT_KEY_NAMED_INVENTORIES, Constants.NBT.TAG_COMPOUND)) {
            tagRoot.setTag(NBT_KEY_NAMED_INVENTORIES, new NBTTagCompound());
        }
        NBTTagCompound tagInventories = tagRoot.getCompoundTag(NBT_KEY_NAMED_INVENTORIES);

        return new ITerminalStorageTabCommon.IVariableInventory() {

            @Override
            public List<ItemStack> getNamedInventory(String name) {
                NBTTagCompound tag = tagInventories.getCompoundTag(name);
                NonNullList<ItemStack> list = NonNullList.withSize(tag.getInteger("itemCount"), null);
                ItemStackHelpers.loadAllItems(tag, list);
                return list;
            }

            @Override
            public void setNamedInventory(String name, List<ItemStack> inventory) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("tabName", name);
                tag.setInteger("itemCount", inventory.size());
                ItemStackHelpers.saveAllItems(tag, inventory);
                tagInventories.setTag(name, tag);
            }
        };
    }

    public static TerminalStorageState getTerminalStorageState(ItemStack itemStack, EntityPlayer player, int slot) {
        // Navigate to relevant tag in item
        NBTTagCompound tagRoot = ItemNBTHelpers.getNBT(itemStack);
        if (!tagRoot.hasKey(NBT_KEY_STATES, Constants.NBT.TAG_COMPOUND)) {
            tagRoot.setTag(NBT_KEY_STATES, new NBTTagCompound());
        }
        NBTTagCompound tagStates = tagRoot.getCompoundTag(NBT_KEY_STATES);
        String playerKey = player.getUniqueID()
            .toString();

        // Instantiate storage state from NBT
        if (!tagStates.hasKey(playerKey, Constants.NBT.TAG_COMPOUND)) {
            TerminalStorageState state = TerminalStorageState.getPlayerDefault(player);
            tagStates.setTag(playerKey, state.getTag());
            return state;
        } else {
            return new TerminalStorageState(tagStates.getCompoundTag(playerKey));
        }
    }
}
