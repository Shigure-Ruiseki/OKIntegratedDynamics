package ruiseki.integratedterminals.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.part.PartStateEmpty;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalStoragePart;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.integratedterminals.core.part.PartTypeTerminal;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCrafting;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStoragePart;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemHelpers;

public class PartTypeTerminalStorage extends PartTypeTerminal<PartTypeTerminalStorage, PartTypeTerminalStorage.State> {

    public PartTypeTerminalStorage(String name) {
        super(name);
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.terminalStorageBaseConsumption;
    }

    @Override
    protected PartTypeTerminalStorage.State constructDefaultState() {
        return new PartTypeTerminalStorage.State();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Class<? extends GuiScreen> getGui() {
        return GuiTerminalStoragePart.class;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerTerminalStoragePart.class;
    }

    @Override
    protected void openGui(World world, BlockPos pos, State partState, EntityPlayer player, ItemStack heldItem,
        ForgeDirection side, float hitX, float hitY, float hitZ) {
        TerminalStorageState terminalStorageState = partState.getPlayerStorageState(player);
        getModGui().getGuiHandler()
            .setTemporaryData(
                ExtendedGuiHandler.TERMINAL_STORAGE_PART,
                Pair.of(side, Pair.of(null, terminalStorageState)));
        if (!world.isRemote && hasGui()) {
            player.openGui(getModGui().getModId(), getGuiID(), world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void registerGui() {
        if (hasGui()) {
            this.guiID = Helpers.getNewId(getModGui(), Helpers.IDType.GUI);
            getModGui().getGuiHandler()
                .registerGUI(this, ExtendedGuiHandler.TERMINAL_STORAGE_PART);
        } else {
            this.guiID = -1;
        }
    }

    @Override
    public void addDrops(PartTarget target, State state, List<ItemStack> itemStacks, boolean dropMainElement,
        boolean saveState) {
        for (Map.Entry<String, List<ItemStack>> entry : state.getNamedInventories()
            .entrySet()) {
            if (entry.getKey()
                .equals(TerminalStorageTabIngredientComponentItemStackCrafting.NAME.toString())) {
                if (!entry.getValue()
                    .isEmpty()) {
                    entry.getValue()
                        .set(0, null);
                }
            }
            for (ItemStack itemStack : entry.getValue()) {
                if (itemStack != null) {
                    itemStacks.add(itemStack);
                }
            }
        }
        state.clearNamedInventories();

        super.addDrops(target, state, itemStacks, dropMainElement, saveState);
    }

    public static class State extends PartStateEmpty<PartTypeTerminalStorage>
        implements ITerminalStorageTabCommon.IVariableInventory {

        private final Map<String, List<ItemStack>> namedInventories;
        private final Map<String, TerminalStorageState> playerStorageStates;

        public State() {
            this.namedInventories = Maps.newHashMap();
            this.playerStorageStates = Maps.newHashMap();
        }

        @Override
        public int getUpdateInterval() {
            return 1;
        }

        public void clearNamedInventories() {
            this.namedInventories.clear();
        }

        @Override
        public void setNamedInventory(String name, List<ItemStack> inventory) {
            this.namedInventories.put(name, inventory);
            this.onDirty();
        }

        public Map<String, List<ItemStack>> getNamedInventories() {
            return namedInventories;
        }

        @Override
        @Nullable
        public List<ItemStack> getNamedInventory(String name) {
            return this.namedInventories.get(name);
        }

        public TerminalStorageState getPlayerStorageState(EntityPlayer player) {
            TerminalStorageState state = playerStorageStates.get(
                player.getUniqueID()
                    .toString());
            if (state == null) {
                state = TerminalStorageState.getPlayerDefault(player, this);
                playerStorageStates.put(
                    player.getUniqueID()
                        .toString(),
                    state);
                this.onDirty();
            }
            return state;
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            NBTTagList list = new NBTTagList();
            for (Map.Entry<String, List<ItemStack>> entry : this.namedInventories.entrySet()) {
                NBTTagCompound listEntry = new NBTTagCompound();
                listEntry.setString("tabName", entry.getKey());
                listEntry.setInteger(
                    "itemCount",
                    entry.getValue()
                        .size());
                ItemHelpers.saveAllItems(listEntry, entry.getValue());
                list.appendTag(listEntry);
            }
            tag.setTag("namedInventories", list);

            NBTTagList playerStorageStatesList = new NBTTagList();
            for (Map.Entry<String, TerminalStorageState> entry : this.playerStorageStates.entrySet()) {
                NBTTagCompound stateEntry = new NBTTagCompound();
                stateEntry.setString("player", entry.getKey());
                stateEntry.setTag(
                    "value",
                    entry.getValue()
                        .getTag());
                playerStorageStatesList.appendTag(stateEntry);
            }
            tag.setTag("playerStorageStates", playerStorageStatesList);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            NBTTagList namedInventoriesList = tag.getTagList("namedInventories", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < namedInventoriesList.tagCount(); i++) {
                NBTTagCompound listEntry = namedInventoriesList.getCompoundTagAt(i);
                int itemCount = listEntry.getInteger("itemCount");

                List<ItemStack> list = new ArrayList<>(Collections.nCopies(itemCount, null));
                String tabName = listEntry.getString("tabName");

                ItemHelpers.loadAllItems(listEntry, list);
                this.namedInventories.put(tabName, list);
            }

            NBTTagList playerStorageList = tag.getTagList("playerStorageStates", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < playerStorageList.tagCount(); i++) {
                NBTTagCompound tagAt = playerStorageList.getCompoundTagAt(i);
                String playerName = tagAt.getString("player");
                TerminalStorageState state = new TerminalStorageState(tagAt.getCompoundTag("value"), this);
                this.playerStorageStates.put(playerName, state);
            }
        }
    }
}
