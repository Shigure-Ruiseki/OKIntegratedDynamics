package ruiseki.integratedterminals.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.part.PartStateEmpty;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalStorage;
import ruiseki.integratedterminals.core.part.PartTypeTerminal;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCrafting;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorage;
import ruiseki.okcore.helper.ItemStackHelpers;

/**
 * A part that exposes a gui using which players can access storage indexes in the network.
 * 
 * @author rubensworks
 */
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
        return GuiTerminalStorage.class;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerTerminalStorage.class;
    }

    @Override
    public void addDrops(PartTarget target, State state, List<ItemStack> itemStacks, boolean dropMainElement,
        boolean saveState) {
        for (Map.Entry<String, List<ItemStack>> entry : state.getNamedInventories()
            .entrySet()) {
            // TODO: for now hardcoded on crafting tab
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

    public static class State extends PartStateEmpty<PartTypeTerminalStorage> {

        private final Map<String, List<ItemStack>> namedInventories;

        public State() {
            this.namedInventories = Maps.newHashMap();
        }

        public void clearNamedInventories() {
            this.namedInventories.clear();
        }

        public void setNamedInventory(String name, List<ItemStack> inventory) {
            this.namedInventories.put(name, inventory);
            this.onDirty();
        }

        public Map<String, List<ItemStack>> getNamedInventories() {
            return namedInventories;
        }

        @Nullable
        public List<ItemStack> getNamedInventory(String name) {
            return this.namedInventories.get(name);
        }

        public void loadNamedInventory(String name, IInventory inventory) {
            List<ItemStack> tabItems = this.getNamedInventory(name);
            if (tabItems != null) {
                for (int i = 0; i < tabItems.size(); i++) {
                    inventory.setInventorySlotContents(i, tabItems.get(i));
                }
            }
        }

        public void saveNamedInventory(String name, IInventory inventory) {
            List<ItemStack> latestItems = new ArrayList<>();
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                latestItems.add(inventory.getStackInSlot(i));
            }
            this.setNamedInventory(name, latestItems);
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
                ItemStackHelpers.saveAllItems(listEntry, entry.getValue());
                list.appendTag(listEntry);
            }
            tag.setTag("namedInventories", list);
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

                ItemStackHelpers.loadAllItems(listEntry, list);
                this.namedInventories.put(tabName, list);
            }
        }
    }
}
