package ruiseki.integrateddynamics.core.logicprogrammer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.cleanroommc.modularui.api.inventory.ClickType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import lombok.Setter;
import ruiseki.commoncapabilities.api.capability.fluidhandler.FluidMatch;
import ruiseki.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import ruiseki.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesList;
import ruiseki.commoncapabilities.api.capability.recipehandler.RecipeDefinition;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.MixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElementType;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeRecipe;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.Helpers;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.ingredient.ItemMatchProperties;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeRecipeSlotPropertiesChangedPacket;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.inventory.slot.SlotExtended;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagKey;

/**
 * Element for recipes.
 * This is hardcoded to only support items, fluids and energy
 *
 * @author rubensworks
 */
public class ValueTypeRecipeLPElement extends ValueTypeLPElementBase {

    public static final int SLOT_OFFSET = 4;
    public static final int TICK_DELAY = 30;

    @SideOnly(Side.CLIENT)
    public ValueTypeRecipeLPElementMasterSubGui lastGui;

    @Getter
    private NonNullList<ItemMatchProperties> inputStacks;
    private ItemStack inputFluid;
    @Getter
    @Setter
    private String inputFluidAmount = "0";
    @Getter
    @Setter
    private String inputEnergy = "0";
    private List<ItemStack> outputStacks;
    private ItemStack outputFluid;
    @Getter
    @Setter
    private String outputFluidAmount = "0";
    @Getter
    @Setter
    private String outputEnergy = "0";

    public ValueTypeRecipeLPElement() {
        super(ValueTypes.OBJECT_RECIPE);
    }

    @Override
    public ILogicProgrammerElementType getType() {
        return LogicProgrammerElementTypes.VALUETYPE;
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return IConfigRenderPattern.RECIPE;
    }

    @Override
    public void onInputSlotUpdated(int slotId, ItemStack itemStack) {
        if (inputStacks == null) {
            return;
        }

        ItemStack copiedStack = (itemStack != null) ? itemStack.copy() : null;
        if (slotId >= 0 && slotId < 9) {
            ItemStack itemStackOld = inputStacks.get(slotId)
                .getItemStack();

            Item itemOld = (itemStackOld != null) ? itemStackOld.getItem() : null;
            Item itemNew = (itemStack != null) ? itemStack.getItem() : null;

            if (itemOld != itemNew) {
                inputStacks.set(slotId, new ItemMatchProperties(copiedStack));
                if (MinecraftHelpers.isClientSide()) {
                    refreshPropertiesGui(slotId);
                }
            }
        }

        if (slotId == 9) {
            inputFluid = copiedStack;
            if (inputFluid != null && inputFluidAmount.equalsIgnoreCase("0")) {
                int amount = FluidHelpers.getAmount(Helpers.getFluidStack(inputFluid));
                inputFluidAmount = Integer.toString(amount);
                if (MinecraftHelpers.isClientSide() && lastGui != null) {
                    refreshInputFluidAmountBox();
                }
            }
        }

        if (slotId >= 10 && slotId < 13) {
            if (outputStacks != null) {
                outputStacks.set(slotId - 10, copiedStack);
            }
        }

        if (slotId == 13) {
            outputFluid = copiedStack;
            if (outputFluid != null && outputFluidAmount.equalsIgnoreCase("0")) {
                int amount = FluidHelpers.getAmount(Helpers.getFluidStack(outputFluid));
                outputFluidAmount = Integer.toString(amount);
                if (MinecraftHelpers.isClientSide() && lastGui != null) {
                    refreshOutputFluidAmountBox();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    protected void refreshPropertiesGui(int slot) {
        if (this.lastGui != null && this.lastGui.subGuiRecipe.getInputFluidAmountBox() != null) {
            this.lastGui.subGuiRecipe.getInputFluidAmountBox()
                .setText(inputFluidAmount);
        }
    }

    @SideOnly(Side.CLIENT)
    protected void refreshInputFluidAmountBox() {
        if (this.lastGui != null && this.lastGui.subGuiRecipe.getInputFluidAmountBox() != null) {
            this.lastGui.subGuiRecipe.getInputFluidAmountBox()
                .setText(inputFluidAmount);
        }
    }

    @SideOnly(Side.CLIENT)
    protected void refreshOutputFluidAmountBox() {
        if (this.lastGui != null && this.lastGui.subGuiRecipe.getOutputFluidAmountBox() != null) {
            this.lastGui.subGuiRecipe.getOutputFluidAmountBox()
                .setText(outputFluidAmount);
        }
    }

    public void sendSlotPropertiesToServer(int slotId, ItemMatchProperties props) {
        IntegratedDynamics._instance.getPacketHandler()
            .sendToServer(
                new LogicProgrammerValueTypeRecipeSlotPropertiesChangedPacket(
                    slotId,
                    props.isNbt(),
                    props.getItemTag() == null ? "" : props.getItemTag(),
                    props.getTagQuantity()));
    }

    // Used by ID-Compat for JEI recipe transfer handler
    public boolean isValidForRecipeGrid(List<ItemMatchProperties> itemInputs, List<FluidStack> fluidInputs,
        List<ItemStack> itemOutputs, List<FluidStack> fluidOutputs) {
        return itemInputs.size() <= 9 && itemOutputs.size() <= 3 && fluidInputs.size() <= 1 && fluidOutputs.size() <= 1;
    }

    protected void putItemPropertiesInContainer(ContainerLogicProgrammerBase container, int slot,
        ItemMatchProperties props) {
        putStackInContainer(container, slot, props.getItemStack());
        getInputStacks().set(slot, props);
    }

    protected void putStackInContainer(ContainerLogicProgrammerBase container, int slot, ItemStack itemStack) {
        // Offset: Player inventory, recipe grid slots
        container.putStackInSlot(container.inventorySlots.size() - (36 + 14) + slot, itemStack);
    }

    // Used by ID-Compat for JEI recipe transfer handler
    public void setRecipeGrid(ContainerLogicProgrammerBase container, List<ItemMatchProperties> itemInputs,
        List<FluidStack> fluidInputs, List<ItemStack> itemOutputs, List<FluidStack> fluidOutputs) {
        int slot = 0;

        // Fill input item slots
        for (ItemMatchProperties itemInput : itemInputs) {
            putItemPropertiesInContainer(container, slot, itemInput);
            slot++;
        }
        while (slot < 9) {
            putItemPropertiesInContainer(container, slot, new ItemMatchProperties(null));
            slot++;
        }

        // Fill input fluid slot
        slot = 9;
        FluidStack fluidStackInput = null;
        if (!fluidInputs.isEmpty()) {
            fluidStackInput = fluidInputs.get(0);
        }
        putStackInContainer(container, slot, fluidStackInput == null ? null : getFluidBucket(fluidStackInput));
        inputFluidAmount = String.valueOf(FluidHelpers.getAmount(fluidStackInput));
        if (MinecraftHelpers.isClientSide()) {
            refreshInputFluidAmountBox();
        }

        // Fill input output slots
        slot = 10;
        for (ItemStack itemOutput : itemOutputs) {
            putStackInContainer(container, slot, itemOutput);
            slot++;
        }
        while (slot < 13) {
            putStackInContainer(container, slot, null);
            slot++;
        }

        // Fill output fluid slot
        slot = 13;
        FluidStack fluidStackOutput = null;
        if (!fluidOutputs.isEmpty()) {
            fluidStackOutput = fluidOutputs.get(0);
        }
        putStackInContainer(container, slot, fluidStackOutput == null ? null : getFluidBucket(fluidStackOutput));
        outputFluidAmount = String.valueOf(FluidHelpers.getAmount(fluidStackOutput));
        if (MinecraftHelpers.isClientSide()) {
            refreshOutputFluidAmountBox();
        }
    }

    protected ItemStack getFluidBucket(FluidStack fluidStack) {
        ItemStack itemStack = new ItemStack(Items.bucket);
        IFluidHandlerItem fluidHandler = CapabilityHelpers
            .getCapability(itemStack, CapabilityFluidHandler.FLUID_HANDLER_ITEM)
            .getOrNull();
        if (fluidHandler != null) {
            fluidHandler.fill(new FluidStack(fluidStack, FluidHelpers.BUCKET_VOLUME), true);
            return fluidHandler.getContainer();
        }
        return itemStack;
    }

    protected boolean isInputValid() {
        return inputStacks.stream()
            .anyMatch(ItemMatchProperties::isValid) || inputFluid != null
            || !inputFluidAmount.equalsIgnoreCase("0")
            || !inputEnergy.equalsIgnoreCase("0");
    }

    protected boolean isOutputValid() {
        return outputStacks.stream()
            .anyMatch(Objects::nonNull) || outputFluid != null
            || !outputFluidAmount.equalsIgnoreCase("0")
            || !outputEnergy.equalsIgnoreCase("0");
    }

    @Override
    public boolean canWriteElementPre() {
        return isInputValid() == isOutputValid(); // Not &&, because we also allow fully blank recipes
    }

    @Override
    public void activate() {
        inputStacks = NonNullList.withSize(9, new ItemMatchProperties(null));
        for (int i = 0; i < 9; i++) {
            inputStacks.set(i, new ItemMatchProperties(null));
        }
        inputFluid = null;
        inputFluidAmount = "0";
        inputEnergy = "0";
        outputStacks = new ArrayList<>(Collections.nCopies(3, null));
        outputFluid = null;
        outputFluidAmount = "0";
        outputEnergy = "0";
    }

    @Override
    public void deactivate() {

    }

    @Override
    public LangHelpers.UnlocalizedString validate() {
        if (inputFluid != null && Helpers.getFluidStack(inputFluid) == null) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_OBJECT_FLUID_ERROR_NOFLUID);
        }
        if (outputFluid != null && Helpers.getFluidStack(outputFluid) == null) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_OBJECT_FLUID_ERROR_NOFLUID);
        }
        try {
            Integer.parseInt(inputFluidAmount);
        } catch (NumberFormatException e) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_ERROR_INVALIDINPUT, inputFluidAmount);
        }
        try {
            Integer.parseInt(outputFluidAmount);
        } catch (NumberFormatException e) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_ERROR_INVALIDINPUT, outputFluidAmount);
        }
        try {
            Integer.parseInt(inputEnergy);
        } catch (NumberFormatException e) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_ERROR_INVALIDINPUT, inputEnergy);
        }
        try {
            Integer.parseInt(outputEnergy);
        } catch (NumberFormatException e) {
            return new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_ERROR_INVALIDINPUT, outputEnergy);
        }
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int slotId, ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean slotClick(int slotId, Slot slot, int mouseButton, int clickType, EntityPlayer player) {
        if (slotId >= SLOT_OFFSET && slotId < 9 + SLOT_OFFSET) {
            if (ClickType.fromNumber(clickType) == ClickType.QUICK_MOVE && mouseButton == 0) {
                if (player.worldObj.isRemote) {
                    int id = slotId - SLOT_OFFSET;
                    lastGui.setPropertySubGui(id);
                }
                return true;
            } else {
                // Similar logic as ContainerExtended.adjustPhantomSlot
                ItemMatchProperties props = getInputStacks().get(slotId - SLOT_OFFSET);
                int quantityCurrent = props.getTagQuantity();
                int quantityNew;
                if (ClickType.fromNumber(clickType) == ClickType.QUICK_MOVE) {
                    quantityNew = mouseButton == 0 ? (quantityCurrent + 1) / 2 : quantityCurrent * 2;
                } else {
                    quantityNew = mouseButton == 0 ? quantityCurrent - 1 : quantityCurrent + 1;
                }

                if (quantityNew > slot.getSlotStackLimit()) {
                    quantityNew = slot.getSlotStackLimit();
                }

                props.setTagQuantity(quantityNew);

                if (quantityNew <= 0) {
                    props.setItemTag(null);
                    props.setTagQuantity(1);
                    if (MinecraftHelpers.isClientSide()) {
                        refreshPropertiesGui(slotId - SLOT_OFFSET);
                    }
                }
            }
        }

        return super.slotClick(slotId, slot, mouseButton, clickType, player);
    }

    @Override
    public SlotExtended createSlot(IInventory temporaryInputSlots, int slotId, int x, int y) {
        SlotExtended slot = new SlotExtended(temporaryInputSlots, slotId, x, y) {

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return ValueTypeRecipeLPElement.this.isItemValidForSlot(slotId, itemStack);
            }

            @Override
            public ItemStack getStack() {
                if (MinecraftHelpers.isClientSide() && slotId < 9) {
                    ItemMatchProperties props = getInputStacks().get(slotId);
                    String tagName = props.getItemTag();

                    if (tagName != null && !tagName.isEmpty()) {
                        List<ItemStack> stacks;

                        if (tagName.contains(":")) {
                            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, new ResourceLocation(tagName));
                            stacks = TagHelpers.toItemStacks(tagKey);
                        } else {
                            stacks = OreDictionary.getOres(tagName);
                        }

                        if (stacks != null && !stacks.isEmpty()) {
                            World world = Minecraft.getMinecraft().theWorld;
                            long gameTime = world != null ? world.getTotalWorldTime() : 0;

                            int tick = (int) (gameTime / TICK_DELAY);
                            ItemStack baseStack = stacks.get(Math.abs(tick) % stacks.size());

                            if (baseStack != null && baseStack.getItem() != null) {
                                ItemStack resultStack = baseStack.copy();
                                resultStack.stackSize = props.getTagQuantity();
                                return resultStack;
                            }
                        }
                    }
                }
                return super.getStack();
            }
        };
        slot.setPhantom(true);
        return slot;
    }

    @Override
    public int getItemStackSizeLimit() {
        return 64;
    }

    protected Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> getInputs(
        List<ItemMatchProperties> itemStacks, ItemStack fluid, int fluidAmount, long energy) {

        // Truncate list up to the last non-empty stack
        int lastNonEmpty = 0;
        for (int i = 0; i < itemStacks.size(); i++) {
            if (itemStacks.get(i)
                .isValid()) {
                lastNonEmpty = i + 1;
            }
        }
        itemStacks = itemStacks.subList(0, lastNonEmpty);

        // Override fluid amount
        FluidStack fluidStack = Helpers.getFluidStack(fluid);
        if (fluidStack != null) {
            fluidStack.amount = fluidAmount;
        }

        Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = Maps
            .newIdentityHashMap();

        List<IPrototypedIngredientAlternatives<ItemStack, Integer>> items = itemStacks.stream()
            .map(ItemMatchProperties::createPrototypedIngredient)
            .collect(Collectors.toList());

        List<IPrototypedIngredientAlternatives<FluidStack, Integer>> fluids = fluidStack != null
            ? Collections.singletonList(
                new PrototypedIngredientAlternativesList<>(
                    Collections.singletonList(
                        new PrototypedIngredient<>(
                            IngredientComponent.FLUIDSTACK,
                            fluidStack,
                            FluidMatch.FLUID | FluidMatch.NBT))))
            : Collections.emptyList();

        List<IPrototypedIngredientAlternatives<Long, Boolean>> energies = energy > 0
            ? Collections.singletonList(
                new PrototypedIngredientAlternativesList<>(
                    Collections.singletonList(new PrototypedIngredient<>(IngredientComponent.ENERGY, energy, false))))
            : Collections.emptyList();

        if (!items.isEmpty()) {
            inputs.put(IngredientComponent.ITEMSTACK, (List) items);
        }
        if (!fluids.isEmpty()) {
            inputs.put(IngredientComponent.FLUIDSTACK, (List) fluids);
        }
        if (!energies.isEmpty()) {
            inputs.put(IngredientComponent.ENERGY, (List) energies);
        }

        return inputs;
    }

    protected Map<IngredientComponent<?, ?>, List<?>> getOutputs(List<ItemStack> itemStacksIn, ItemStack fluid,
        int fluidAmount, long energy) {

        // Truncate list up to the last non-empty stack
        List<ItemStack> itemStacks = Lists.newArrayList();
        for (int i = 0; i < itemStacksIn.size(); i++) {
            if (itemStacksIn.get(i) != null) {
                itemStacks.add(itemStacksIn.get(i));
            }
        }

        // Override fluid amount
        FluidStack fluidStack = Helpers.getFluidStack(fluid);
        if (fluidStack != null) {
            fluidStack.amount = fluidAmount;
        }

        Map<IngredientComponent<?, ?>, List<?>> outputs = Maps.newIdentityHashMap();
        if (!itemStacks.isEmpty()) {
            outputs.put(IngredientComponent.ITEMSTACK, itemStacks);
        }
        if (fluidStack != null) {
            outputs.put(IngredientComponent.FLUIDSTACK, Collections.singletonList(fluidStack));
        }
        if (energy > 0) {
            outputs.put(IngredientComponent.ENERGY, Collections.singletonList(energy));
        }

        return outputs;
    }

    @Override
    public IValue getValue() {
        if (!isInputValid() && !isOutputValid()) {
            return ValueObjectTypeRecipe.ValueRecipe.of(null);
        }
        return ValueObjectTypeRecipe.ValueRecipe.of(
            new RecipeDefinition(
                getInputs(
                    this.inputStacks,
                    this.inputFluid,
                    Integer.parseInt(this.inputFluidAmount),
                    Long.parseLong(this.inputEnergy)),
                new MixedIngredients(
                    getOutputs(
                        this.outputStacks,
                        this.outputFluid,
                        Integer.parseInt(this.outputFluidAmount),
                        Long.parseLong(this.outputEnergy)))));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ISubGuiBox createSubGui(int baseX, int baseY, int maxWidth, int maxHeight, GuiLogicProgrammerBase gui,
        ContainerLogicProgrammerBase container) {
        return lastGui = new ValueTypeRecipeLPElementMasterSubGui(
            this,
            baseX,
            baseY,
            maxWidth,
            maxHeight,
            gui,
            container);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setValueInGui(ISubGuiBox subGui) {
        ValueTypeRecipeLPElementRecipeSubGui gui = ((ValueTypeRecipeLPElementMasterSubGui) subGui).getSubGuiRecipe();
        IInventory slots = gui.container.getTemporaryInputSlots();
        for (int i = 0; i < this.inputStacks.size(); i++) {
            ItemMatchProperties entry = this.inputStacks.get(i);
            slots.setInventorySlotContents(i, entry.getItemStack());
        }
        slots.setInventorySlotContents(9, this.inputFluid);
        if (gui.getInputFluidAmountBox() != null) {
            gui.getInputFluidAmountBox()
                .setText(this.inputFluidAmount);
            gui.getInputEnergyBox()
                .setText(this.inputEnergy);
            for (int i = 0; i < this.outputStacks.size(); i++) {
                slots.setInventorySlotContents(10 + i, this.outputStacks.get(i));
                // No need to set slot type, as this can't be changed for output stacks
            }
            slots.setInventorySlotContents(13, this.outputFluid);
            gui.getOutputFluidAmountBox()
                .setText(this.outputFluidAmount);
            gui.getOutputEnergyBox()
                .setText(this.outputEnergy);
        }
    }
}
