package ruiseki.integrateddynamics.inventory.container;

import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.logicprogrammer.LogicProgrammerElementTypes;
import ruiseki.integrateddynamics.core.persist.world.LabelsWorldStorage;
import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.ScrollingInventoryContainer;
import ruiseki.okcore.inventory.slot.SlotSingleItem;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * Base container for the logic programmer.
 *
 * @author rubensworks
 */
public abstract class ContainerLogicProgrammerBase extends ScrollingInventoryContainer<ILogicProgrammerElement>
    implements IDirtyMarkListener {

    public static final int OUTPUT_X = 232;
    public static final int OUTPUT_Y = 110;

    protected static final IItemPredicate<ILogicProgrammerElement> FILTERER = new IItemPredicate<ILogicProgrammerElement>() {

        @Override
        public boolean apply(ILogicProgrammerElement item, Pattern pattern) {
            return pattern.matcher(item.getMatchString())
                .matches()
                || pattern.matcher(item.getSymbol())
                    .matches();
        }
    };

    private final SimpleInventory writeSlot;
    private final SimpleInventory filterSlots;
    private ILogicProgrammerElement activeElement = null;
    private ILogicProgrammerElement temporarySlotsElement = null;
    private SimpleInventory temporaryInputSlots = null;
    private LangHelpers.UnlocalizedString lastError;
    private LoadConfigListener loadConfigListener;

    private IValueType filterIn1 = null;
    private IValueType filterIn2 = null;
    private IValueType filterOut = null;

    @SideOnly(Side.CLIENT)
    private GuiLogicProgrammerBase gui;

    private String lastLabel = "";

    public ContainerLogicProgrammerBase(InventoryPlayer inventory, IGuiContainerProvider guiProvider) {
        super(inventory, guiProvider, getElements(), FILTERER);
        this.writeSlot = new SimpleInventory(1, "writeSlot", 1);
        this.filterSlots = new SimpleInventory(3, "filterSlots", 1);
        this.filterSlots.addDirtyMarkListener(new FilterSlotListener());
        this.writeSlot.addDirtyMarkListener(this);
        this.writeSlot.addDirtyMarkListener(loadConfigListener = new LoadConfigListener());
        this.temporaryInputSlots = new SimpleInventory(0, "temporaryInput", 1);
        initializeSlotsPre();
        initializeSlotsPost();
    }

    protected static List<ILogicProgrammerElement> getElements() {
        List<ILogicProgrammerElement> elements = Lists.newLinkedList();
        for (ILogicProgrammerElementType type : LogicProgrammerElementTypes.REGISTRY.getTypes()) {
            elements.addAll(type.createElements());
        }
        return elements;
    }

    @SideOnly(Side.CLIENT)
    public void setGui(GuiLogicProgrammerBase gui) {
        this.gui = gui;
    }

    @SideOnly(Side.CLIENT)
    public GuiLogicProgrammerBase getGui() {
        return this.gui;
    }

    protected void initializeSlotsPre() {
        addSlotToContainer(
            new SlotSingleItem(writeSlot, 0, OUTPUT_X, OUTPUT_Y, ItemVariableConfig._instance.getInstance()));
        SlotSingleItem filterSlotIn1 = new SlotSingleItem(
            filterSlots,
            0,
            6,
            218,
            ItemVariableConfig._instance.getInstance());
        SlotSingleItem filterSlotIn2 = new SlotSingleItem(
            filterSlots,
            1,
            24,
            218,
            ItemVariableConfig._instance.getInstance());
        SlotSingleItem filterSlotOut = new SlotSingleItem(
            filterSlots,
            2,
            58,
            218,
            ItemVariableConfig._instance.getInstance());
        filterSlotIn1.setPhantom(true);
        filterSlotIn2.setPhantom(true);
        filterSlotOut.setPhantom(true);
        addSlotToContainer(filterSlotIn1);
        addSlotToContainer(filterSlotIn2);
        addSlotToContainer(filterSlotOut);
    }

    protected void initializeSlotsPost() {
        addPlayerInventory((InventoryPlayer) getPlayerIInventory(), 88, 131);
    }

    @Override
    public int getPageSize() {
        return 10;
    }

    @Override
    protected int getSizeInventory() {
        return 1;
    }

    public void setActiveElementById(String typeId, String elementId) {
        ILogicProgrammerElementType type = LogicProgrammerElementTypes.REGISTRY.getType(typeId);
        if (type != null) {
            ILogicProgrammerElement element = type.getByName(elementId);
            if (!LogicProgrammerElementTypes.areEqual(getActiveElement(), element)) {
                setActiveElement(element, 0, 0);
                onDirty();
            }
        } else {
            setActiveElement(null, 0, 0);
        }
    }

    /**
     * Set the new active element.
     *
     * @param activeElement The new element.
     * @param baseX         The slots X coordinate
     * @param baseY         The slots Y coordinate
     */
    public void setActiveElement(final ILogicProgrammerElement activeElement, int baseX, int baseY) {
        if (this.activeElement != null) {
            this.activeElement.deactivate();
        }
        this.activeElement = activeElement;

        this.lastError = null;
        this.activeElement = activeElement;

        this.setElementInventory(this.activeElement, baseX, baseY);

        if (activeElement != null) {
            activeElement.activate();
        }
    }

    /**
     * Set the new active element.
     *
     * @param element The new element.
     * @param baseX   The slots X coordinate
     * @param baseY   The slots Y coordinate
     */
    public void setElementInventory(final ILogicProgrammerElement element, int baseX, int baseY) {
        this.lastError = null;
        // This assumes that there is only one other slot, the remaining slots will be erased!
        // (We can do this because they are all ghost slots)
        inventoryItemStacks = Lists.newArrayList();
        inventorySlots = Lists.newArrayList();
        initializeSlotsPre();
        this.temporaryInputSlots.removeDirtyMarkListener(this);
        this.temporaryInputSlots = new SimpleInventory(
            element == null ? 0
                : element.getRenderPattern()
                    .getSlotPositions().length,
            "temporaryInput",
            element == null ? 0 : element.getItemStackSizeLimit());
        temporaryInputSlots.addDirtyMarkListener(this);
        this.temporarySlotsElement = element;
        if (element != null) {
            Pair<Integer, Integer>[] slotPositions = element.getRenderPattern()
                .getSlotPositions();
            for (int i = 0; i < temporaryInputSlots.getSizeInventory(); i++) {
                addSlotToContainer(
                    element.createSlot(
                        temporaryInputSlots,
                        i,
                        1 + baseX + slotPositions[i].getLeft(),
                        1 + baseY + slotPositions[i].getRight()));
            }
        }
        initializeSlotsPost();
        this.lastLabel = "";
    }

    public boolean canWriteActiveElementPre() {
        if (activeElement != null) {
            return activeElement.canWriteElementPre();
        }
        return false;
    }

    public boolean canWriteActiveElement() {
        if (!canWriteActiveElementPre()) {
            return false;
        }
        lastError = activeElement.validate();
        return lastError == null;
    }

    public ILogicProgrammerElement getActiveElement() {
        return activeElement;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!player.worldObj.isRemote) {
            ItemStack itemStack = writeSlot.getStackInSlot(0);
            if (itemStack != null) {
                player.dropPlayerItemWithRandomChoice(itemStack, false);
            }
        }
    }

    public void onLabelPacket(String label) {
        this.lastLabel = label;
        labelCurrent();
    }

    protected void labelCurrent() {
        ItemStack itemStack = writeSlot.getStackInSlot(0);
        if (itemStack != null) {
            IVariableFacade variableFacade = ((ItemVariable) ItemVariableConfig._instance.getInstance())
                .getVariableFacade(itemStack);
            if (this.lastLabel != null && variableFacade.isValid()) {
                LabelsWorldStorage.getInstance(IntegratedDynamics._instance)
                    .put(variableFacade.getId(), this.lastLabel);
            }
        }
    }

    protected ItemStack writeElementInfo() {
        ItemStack itemStack = writeSlot.getStackInSlot(0);
        ItemStack result = getActiveElement().writeElement(player, itemStack.copy());
        return result;
    }

    @Override
    public void onDirty() {
        ILogicProgrammerElement activeElement = getActiveElement();
        if (activeElement != null) {
            for (int i = 0; i < temporaryInputSlots.getSizeInventory(); i++) {
                ItemStack itemStack = temporaryInputSlots.getStackInSlot(i);
                temporarySlotsElement.onInputSlotUpdated(i, itemStack);
            }
        }

        ItemStack itemStack = writeSlot.getStackInSlot(0);
        if (canWriteActiveElement() && itemStack != null) {
            // If the variable has a vanilla custom name, make sure we inherit it as variable label
            if (itemStack.hasDisplayName()) {
                this.lastLabel = itemStack.getDisplayName();
            }
            ItemStack outputStack = writeElementInfo();
            writeSlot.removeDirtyMarkListener(this);
            writeSlot.setInventorySlotContents(0, outputStack);
            if (!StringUtils.isNullOrEmpty(this.lastLabel)) {
                labelCurrent();
            }
            writeSlot.addDirtyMarkListener(this);
        }
    }

    protected void loadConfigFrom(ItemStack itemStack) {
        // Only do this client-side, a packet will be sent to do the same server-side.
        if (MinecraftHelpers.isClientSide()) {
            IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
                .getRegistry(IVariableFacadeHandlerRegistry.class);
            IVariableFacade variableFacade = registry.handle(itemStack);
            for (ILogicProgrammerElement element : getElements()) {
                if (element.isFor(variableFacade)) {
                    getGui().handleElementActivation(element);
                }
            }
        }
    }

    public LangHelpers.UnlocalizedString getLastError() {
        return this.lastError;
    }

    public IInventory getTemporaryInputSlots() {
        return this.temporaryInputSlots;
    }

    public boolean hasWriteItemInSlot() {
        return this.writeSlot.getStackInSlot(0) != null;
    }

    @Override
    protected boolean additionalApplies(ILogicProgrammerElement item) {
        return (((filterIn1 == null || item.matchesInput(filterIn1))
            && (filterIn2 == null || item.matchesInput(filterIn2))) || (filterIn1 == null && filterIn2 == null))
            && (filterOut == null || item.matchesOutput(filterOut));
    }

    @Override
    public ItemStack slotClick(int slotId, int mouseButton, int clickType, EntityPlayer player) {
        // Handle cases where the client may have more (phantom) slots than the server.
        if (slotId >= this.inventorySlots.size() || (this.activeElement != null && this.inventorySlots.size() > slotId
            && slotId >= 0
            && this.activeElement.slotClick(slotId, this.getSlot(slotId), mouseButton, clickType, player))) {
            return null;
        }
        return super.slotClick(slotId, mouseButton, clickType, player);
    }

    /**
     * Load existing operator data when a variable card is inserted into the write slot
     */
    protected class LoadConfigListener implements IDirtyMarkListener {

        @Override
        public void onDirty() {
            // Currently disabled, this requires quite complex negotiation between C and S, not too mention
            // any other players having the gui open!
            /*
             * if ((temporaryInputSlots == null || temporaryInputSlots.isEmpty())
             * && (activeElement == null || activeElement.canCurrentlyReadFromOtherItem())) {
             * ItemStack itemStack = writeSlot.getStackInSlot(0);
             * if (itemStack != null) {
             * ContainerLogicProgrammer.this.loadConfigFrom(itemStack);
             * }
             * }
             */
        }

    }

    /**
     * Filter LP elements based on the filter value types.
     */
    protected class FilterSlotListener implements IDirtyMarkListener {

        protected IValueType getValueType(IInventory inventory, int slot) {
            IVariableFacadeHandlerRegistry handler = IntegratedDynamics._instance.getRegistryManager()
                .getRegistry(IVariableFacadeHandlerRegistry.class);
            if (inventory.getStackInSlot(slot) != null) {
                IVariableFacade variableFacade = handler.handle(inventory.getStackInSlot(slot));
                if (variableFacade.isValid()) {
                    return variableFacade.getOutputType();
                }
            }
            return null;
        }

        @Override
        public void onDirty() {
            IValueType filterIn1Prev = filterIn1;
            IValueType filterIn2Prev = filterIn2;
            IValueType filterOutPrev = filterOut;

            filterIn1 = getValueType(filterSlots, 0);
            filterIn2 = getValueType(filterSlots, 1);
            filterOut = getValueType(filterSlots, 2);
            if (filterIn1Prev != filterIn1 || filterIn2Prev != filterIn2 || filterOutPrev != filterOut) {
                refreshFilter();
            }
        }
    }

}
