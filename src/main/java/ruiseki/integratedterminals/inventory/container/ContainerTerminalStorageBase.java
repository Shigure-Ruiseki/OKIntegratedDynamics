package ruiseki.integratedterminals.inventory.container;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTab;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabServer;
import ruiseki.integratedterminals.api.terminalstorage.event.TerminalStorageTabCommonLoadSlotsEvent;
import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocation;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabs;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientOpenCraftingJobAmountGuiPacket;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientOpenCraftingPlanGuiPacket;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * @author rubensworks
 */
public abstract class ContainerTerminalStorageBase<L> extends ExtendedInventoryContainer implements IDirtyMarkListener {

    private final World world;
    private final Map<String, ITerminalStorageTabClient<?>> tabsClient;
    private final Map<String, ITerminalStorageTabServer> tabsServer;
    private final Map<String, ITerminalStorageTabCommon> tabsCommon;
    private final Map<String, List<Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback>>> tabSlots;
    private final Optional<INetwork> network;
    private final Optional<ITerminalStorageTabCommon.IVariableInventory> variableInventory;

    private int selectedTabIndexValueId;
    private int selectedChannelValueId;
    private boolean serverTabsInitialized;

    private final List<String> channelStrings;
    private String channelAllLabel;

    @SideOnly(Side.CLIENT)
    public GuiTerminalStorage screen;

    private static final TerminalStorageState GLOBAL_PLAYER_STATE = new TerminalStorageState();

    public ContainerTerminalStorageBase(EntityPlayer player, IGuiContainerProvider provider,
        ContainerTerminalStorageBase.InitTabData initTabData, Optional<INetwork> network,
        Optional<ITerminalStorageTabCommon.IVariableInventory> variableInventory) {
        super(player.inventory, provider);

        this.world = player.getEntityWorld();
        this.tabsClient = Maps.newLinkedHashMap();
        this.tabsServer = Maps.newLinkedHashMap();
        this.tabsCommon = Maps.newLinkedHashMap();
        this.tabSlots = Maps.newHashMap();
        this.network = network;
        this.variableInventory = variableInventory;

        this.selectedTabIndexValueId = getNextValueId();
        this.selectedChannelValueId = getNextValueId();
        this.serverTabsInitialized = false;

        addPlayerInventory(player.inventory, 31, 143);
        addInventoryAndOffHand(player);

        this.channelAllLabel = "All";
        this.channelStrings = Lists.newArrayList(this.channelAllLabel);

        // Add all tabs from the registry
        for (ITerminalStorageTab tab : TerminalStorageTabs.REGISTRY.getTabs()) {
            String id = tab.getName()
                .toString();
            if (this.world.isRemote) {
                this.tabsClient.put(id, tab.createClientTab(this, player));
            } else {
                this.tabsServer.put(id, tab.createServerTab(this, player, network.get()));
            }
            ITerminalStorageTabCommon commonTab = tab.createCommonTab(this, player);
            if (commonTab != null) {
                this.tabsCommon.put(id, commonTab);

                int slotStartIndex = this.inventorySlots.size();
                List<Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback>> slots = commonTab
                    .loadSlots(this, slotStartIndex, player, getVariableInventory());
                TerminalStorageTabCommonLoadSlotsEvent loadSlotsEvent = new TerminalStorageTabCommonLoadSlotsEvent(
                    commonTab,
                    this,
                    slots);
                MinecraftForge.EVENT_BUS.post(loadSlotsEvent);
                slots = loadSlotsEvent.getSlots();
                this.tabSlots.put(id, slots);
                for (Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback> slot : slots) {
                    if (slot.getLeft()
                        .getSlotIndex() == 0) {
                        this.addSlotToContainer(slot.getLeft());
                    }
                }
            }
        }

        // Disable all tab slots
        for (ITerminalStorageTabCommon tabCommon : this.tabsCommon.values()) {
            disableSlots(
                tabCommon.getName()
                    .toString());
        }

        // Load gui state
        if (player.worldObj.isRemote) {
            TerminalStorageState state = getGuiState();
            setSelectedTab(
                state.hasTab() ? state.getTab()
                    : getTabsClient().size() > 0 ? Iterables.getFirst(getTabsClient().values(), null)
                        .getName()
                        .toString() : null);
            setSelectedChannel(IPositionedAddonsNetwork.WILDCARD_CHANNEL);
        } else {
            setSelectedTab(null);
            setSelectedChannel(IPositionedAddonsNetwork.WILDCARD_CHANNEL);
        }
    }

    protected void addInventoryAndOffHand(EntityPlayer player) {
        for (int k = 0; k < 4; ++k) {
            final int armorType = k;

            this.addSlotToContainer(new Slot(player.inventory, 39 - k, -7 + (k % 2) * 18, 152 + (k / 2) * 18) {

                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    if (stack == null || stack.getItem() == null) {
                        return false;
                    }
                    return stack.getItem()
                        .isValidArmor(stack, armorType, player);
                }

                @Override
                @SideOnly(Side.CLIENT)
                public IIcon getBackgroundIconIndex() {
                    return ItemArmor.func_94602_b(armorType);
                }
            });
        }
        // TODO Add BackHand Compat
    }

    public Optional<ITerminalStorageTabCommon.IVariableInventory> getVariableInventory() {
        return this.variableInventory;
    }

    public Optional<INetwork> getNetwork() {
        return this.network;
    }

    public abstract ITerminalStorageLocation<L> getLocation();

    public abstract L getLocationInstance();

    @Override
    public void onDirty() {

    }

    public World getWorld() {
        return world;
    }

    public TerminalStorageState getGuiState() {
        return GLOBAL_PLAYER_STATE;
    }

    public int getNextValueId() {
        return super.getNextValueId();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        // Init tabs
        if (!serverTabsInitialized) {
            for (ITerminalStorageTabServer tab : this.tabsServer.values()) {
                tab.init();
            }
            serverTabsInitialized = true;
        }

        // Update common tabs
        for (ITerminalStorageTabCommon tab : this.tabsCommon.values()) {
            tab.onUpdate(this, player, getVariableInventory());
        }

        // Update active server tab
        ITerminalStorageTabServer activeServerTab = getTabServer(getSelectedTab());
        if (activeServerTab != null) {
            activeServerTab.updateActive();
        }
    }

    public <T, M, L> void sendOpenCraftingPlanGuiPacketToServer(CraftingOptionGuiData<T, M, L> craftingOptionData) {
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientOpenCraftingPlanGuiPacket<>(craftingOptionData));
    }

    public <T, M, L> void sendOpenCraftingJobAmountGuiPacketToServer(
        CraftingOptionGuiData<T, M, L> craftingOptionData) {
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientOpenCraftingJobAmountGuiPacket<>(craftingOptionData));
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        // Do nothing, we handle this manually using dirty listeners
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!world.isRemote && serverTabsInitialized) {
            for (ITerminalStorageTabServer tab : this.tabsServer.values()) {
                tab.deInit();
            }
        }
    }

    @Override
    protected int getSizeInventory() {
        return inventorySlots.size() - player.inventory.mainInventory.length;
    }

    public List<Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback>> getTabSlots(String tabName) {
        List<Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback>> slots = this.tabSlots.get(tabName);
        if (slots == null) {
            return Collections.emptyList();
        }
        return slots;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        // Handle any (modded) client-side quick move controls
        if (player.worldObj.isRemote) {
            Optional<ITerminalStorageTabClient<?>> tabOptional = this.screen.getSelectedClientTab();
            if (tabOptional.isPresent() && !tabOptional.get()
                .isQuickMovePrevented(slotID)) {
                tabOptional.get()
                    .handleClick(this, this.getSelectedChannel(), -1, 0, false, false, slotID, true);
            }
        }
        // Always return empty stack because the tab's #handleClick already does the quick move
        return ItemHelpers.EMPTY;
    }

    protected void enableSlots(String tabName) {
        // Do nothing, they will be placed on the correct location client-side upon init
    }

    protected void disableSlots(String tabName) {
        List<Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback>> slots = getTabSlots(tabName);
        if (slots != null) {
            for (Pair<Slot, ITerminalStorageTabCommon.ISlotPositionCallback> slot : slots) {
                slot.getLeft().xDisplayPosition = Integer.MIN_VALUE;
                slot.getLeft().yDisplayPosition = Integer.MIN_VALUE;
            }
        }
    }

    public void setSelectedTab(@Nullable String selectedTab) {
        disableSlots(getSelectedTab());

        if (player.worldObj.isRemote) {
            ITerminalStorageTabClient previousTab = getTabClient(getSelectedTab());
            if (previousTab != null) {
                previousTab.onDeselect(getSelectedChannel());
            }
            getGuiState().setTab(selectedTab);
            ITerminalStorageTabClient newTab = getTabClient(selectedTab);
            if (newTab != null) {
                newTab.onSelect(getSelectedChannel());
            }
        }
        if (selectedTab != null) {
            ValueNotifierHelpers.setValue(this, selectedTabIndexValueId, selectedTab);
        }

        enableSlots(getSelectedTab());
    }

    @Nullable
    public String getSelectedTab() {
        return ValueNotifierHelpers.getValueString(this, selectedTabIndexValueId);
    }

    public void setSelectedChannel(int selectedChannel) {
        ValueNotifierHelpers.setValue(this, selectedChannelValueId, selectedChannel);
        refreshChannelStrings();
    }

    public int getSelectedChannel() {
        return ValueNotifierHelpers.getValueInt(this, selectedChannelValueId);
    }

    @Nullable
    public ITerminalStorageTabClient getTabClient(String id) {
        return tabsClient.get(id);
    }

    @Nullable
    public ITerminalStorageTabServer getTabServer(String id) {
        return tabsServer.get(id);
    }

    @Nullable
    public ITerminalStorageTabCommon getTabCommon(String id) {
        return tabsCommon.get(id);
    }

    public int getTabsClientCount() {
        return getTabsClient().size();
    }

    public Map<String, ITerminalStorageTabClient<?>> getTabsClient() {
        Map<String, ITerminalStorageTabClient<?>> tabs = Maps.newLinkedHashMap();
        for (Map.Entry<String, ITerminalStorageTabClient<?>> entry : tabsClient.entrySet()) {
            if (entry.getValue()
                .isEnabled()) {
                tabs.put(entry.getKey(), entry.getValue());
            }
        }
        return tabs;
    }

    public Map<String, ITerminalStorageTabCommon> getTabsCommon() {
        Map<String, ITerminalStorageTabCommon> tabs = Maps.newLinkedHashMap();
        for (Map.Entry<String, ITerminalStorageTabCommon> entry : tabsCommon.entrySet()) {
            tabs.put(entry.getKey(), entry.getValue());
        }
        return tabs;
    }

    public Map<String, ITerminalStorageTabServer> getTabsServer() {
        return tabsServer;
    }

    public List<String> getChannelStrings() {
        return channelStrings;
    }

    public void refreshChannelStrings() {
        this.channelStrings.clear();
        this.channelStrings.add(channelAllLabel);
        ITerminalStorageTabClient<?> tab = tabsClient.get(getSelectedTab());
        if (tab != null) {
            for (int channel : tab.getChannels()) {
                this.channelStrings.add(String.valueOf(channel));
            }
        }
    }

    public abstract void onVariableContentsUpdated(INetwork network, IVariable<?> variable);

    public static class InitTabData {

        private final String tabName;
        private final int channel;

        public InitTabData(String tabName, int channel) {
            this.tabName = tabName;
            this.channel = channel;
        }

        public String getTabName() {
            return tabName;
        }

        public int getChannel() {
            return channel;
        }

    }

}
