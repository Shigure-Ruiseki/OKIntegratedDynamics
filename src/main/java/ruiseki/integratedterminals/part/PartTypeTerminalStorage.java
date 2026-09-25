package ruiseki.integratedterminals.part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.part.PartStateEmpty;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.core.part.PartTypeTerminal;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCrafting;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStoragePart;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

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
    public Optional<IGuiConstructor> getContainerProvider(PartPos pos) {
        return Optional.of(new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers
                    .getContainerPartConstructionData(pos);
                PartTypeTerminalStorage.State state = (PartTypeTerminalStorage.State) data.getLeft()
                    .getPartState(
                        data.getRight()
                            .getCenter()
                            .getSide());
                TerminalStorageState terminalStorageState = state.getPlayerStorageState(player);
                return new ContainerTerminalStoragePart(
                    playerInventory,
                    data.getRight(),
                    (PartTypeTerminalStorage) data.getMiddle(),
                    Optional.empty(),
                    terminalStorageState);
            }
        });
    }

    @Override
    public void writeExtraGuiData(ExtendedBuffer packetBuffer, PartPos pos, EntityPlayerMP player) {
        try {
            PacketCodec.getAction(PartPos.class)
                .encode(pos, packetBuffer);

            super.writeExtraGuiData(packetBuffer, pos, player);

            // A false to indicate that there will follow no init data object
            packetBuffer.writeBoolean(false);

            PartTypeTerminalStorage.State state = (PartTypeTerminalStorage.State) PartHelpers
                .getPartContainerChecked(pos)
                .getPartState(pos.getSide());
            TerminalStorageState terminalStorageState = state.getPlayerStorageState(player);
            terminalStorageState.writeToPacketBuffer(packetBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
