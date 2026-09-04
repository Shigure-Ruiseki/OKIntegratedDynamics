package ruiseki.integratedcrafting.part;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import it.unimi.dsi.fastutil.ints.Int2BooleanArrayMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import ruiseki.commoncapabilities.api.capability.block.BlockCapabilities;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.capability.recipehandler.RecipeHandlerConfig;
import ruiseki.integratedcrafting.GeneralConfig;
import ruiseki.integratedcrafting.IntegratedCrafting;
import ruiseki.integratedcrafting.api.network.ICraftingNetwork;
import ruiseki.integratedcrafting.client.gui.GuiPartInterfaceCrafting;
import ruiseki.integratedcrafting.client.gui.GuiPartInterfaceCraftingSettings;
import ruiseki.integratedcrafting.core.part.PartTypeInterfaceCraftingBase;
import ruiseki.integratedcrafting.inventory.container.ContainerPartInterfaceCrafting;
import ruiseki.integratedcrafting.inventory.container.ContainerPartInterfaceCraftingSettings;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.evaluate.InventoryVariableEvaluator;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeRecipe;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.part.PartTypeConfigurable;
import ruiseki.integrateddynamics.core.part.event.PartVariableDrivenVariableContentsUpdatedEvent;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.SimpleInventory;

/**
 * Interface for auto crafting.
 *
 * @author rubensworks
 */
public class PartTypeInterfaceCrafting
    extends PartTypeInterfaceCraftingBase<PartTypeInterfaceCrafting, PartTypeInterfaceCrafting.State> {

    private final IGuiContainerProvider settingsGuiProvider;

    public PartTypeInterfaceCrafting(String name) {
        super(name);
        getModGui().getGuiHandler()
            .registerGUI(
                (settingsGuiProvider = new PartTypeConfigurable.GuiProviderSettings(
                    Helpers.getNewId(getModGui(), Helpers.IDType.GUI),
                    getModGui()) {

                    @Override
                    public Class<? extends Container> getContainer() {
                        return ContainerPartInterfaceCraftingSettings.class;
                    }

                    @Override
                    public Class<? extends GuiScreen> getGui() {
                        return GuiPartInterfaceCraftingSettings.class;
                    }
                }),
                ExtendedGuiHandler.PART);
    }

    public IGuiContainerProvider getSettingsGuiProvider() {
        return settingsGuiProvider;
    }

    @Override
    public int getConsumptionRate(State state) {
        return state.getCraftingJobHandler()
            .getProcessingCraftingJobs()
            .size() * GeneralConfig.interfaceCraftingBaseConsumption;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiPartInterfaceCrafting.class;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerPartInterfaceCrafting.class;
    }

    @Override
    protected PartTypeInterfaceCrafting.State constructDefaultState() {
        return new PartTypeInterfaceCrafting.State();
    }

    @Override
    public void update(INetwork network, IPartNetwork partNetwork, PartTarget target, State state) {
        super.update(network, partNetwork, target, state);

        // Reload recipes if needed
        IntSet slots = state.getDelayedRecipeReloads();
        if (!slots.isEmpty()) {
            ICraftingNetwork craftingNetwork = network.getCapability(getNetworkCapability())
                .orElse(null);
            if (craftingNetwork != null) {
                IntSet slotsCopy = new IntOpenHashSet(slots); // Create a copy, to allow insertion into slots during
                                                              // this loop
                slots.clear();
                int channel = state.getChannelCrafting();
                for (Integer slot : slotsCopy) {
                    // Remove the old recipe from the network
                    Int2ObjectMap<IRecipeDefinition> recipes = state.getRecipesIndexed();
                    IRecipeDefinition oldRecipe = recipes.get(slot);
                    if (oldRecipe != null) {
                        craftingNetwork.removeCraftingInterfaceRecipe(channel, state, oldRecipe);
                    }

                    // Reload the recipe in the slot
                    // We simulate initialization for the first two ticks, as dependency variables may still be loading,
                    // and errored may only go away after these dependencies are fully loaded.
                    // Related to CyclopsMC/IntegratedCrafting#110
                    state.reloadRecipe(slot, state.ticksAfterReload <= 1);

                    // Add the new recipe to the network
                    IRecipeDefinition newRecipe = recipes.get(slot);
                    if (newRecipe != null) {
                        craftingNetwork.addCraftingInterfaceRecipe(channel, state, newRecipe);
                    }
                }
            }
        }

        // Internal tick counter
        state.ticksAfterReload++;
    }

    @Override
    public void addDrops(PartTarget target, State state, List<ItemStack> itemStacks, boolean dropMainElement,
        boolean saveState) {
        // Drop the stored variables
        for (int i = 0; i < state.getInventoryVariables()
            .getSizeInventory(); i++) {
            ItemStack itemStack = state.getInventoryVariables()
                .getStackInSlot(i);
            if (ItemHelpers.isEmpty(itemStack)) {
                itemStacks.add(itemStack);
            }
        }
        state.getInventoryVariables()
            .clear();

        super.addDrops(target, state, itemStacks, dropMainElement, saveState);
    }

    public static class State
        extends PartTypeInterfaceCraftingBase.State<PartTypeInterfaceCrafting, PartTypeInterfaceCrafting.State> {

        protected int ticksAfterReload = 0;

        private final SimpleInventory inventoryVariables;
        private final List<InventoryVariableEvaluator<ValueObjectTypeRecipe.ValueRecipe>> variableEvaluators;
        private final Int2ObjectMap<LangHelpers.UnlocalizedString> recipeSlotMessages;
        private final Int2BooleanMap recipeSlotValidated;
        private final IntSet delayedRecipeReloads;
        private final Map<IVariable, Boolean> variableListeners;
        private boolean disableCraftingCheck = false;

        private final Int2ObjectMap<IRecipeDefinition> currentRecipes;

        public State() {
            this.inventoryVariables = new SimpleInventory(9, 1);
            this.inventoryVariables.addDirtyMarkListener(this);
            this.variableEvaluators = Lists.newArrayList();
            this.recipeSlotMessages = new Int2ObjectArrayMap<>();
            this.recipeSlotValidated = new Int2BooleanArrayMap();
            this.delayedRecipeReloads = new IntArraySet();
            this.variableListeners = new MapMaker().weakKeys()
                .makeMap();
            this.currentRecipes = new Int2ObjectArrayMap<>();
        }

        /**
         * @return The inner variables inventory
         */
        public SimpleInventory getInventoryVariables() {
            return this.inventoryVariables;
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            inventoryVariables.writeToNBT(tag, "variables");

            NBTTagCompound recipeSlotErrorsTag = new NBTTagCompound();
            for (Int2ObjectMap.Entry<LangHelpers.UnlocalizedString> entry : this.recipeSlotMessages
                .int2ObjectEntrySet()) {
                recipeSlotErrorsTag.setTag(
                    String.valueOf(entry.getIntKey()),
                    entry.getValue()
                        .serializeNBT());
            }
            tag.setTag("recipeSlotMessages", recipeSlotErrorsTag);

            NBTTagCompound recipeSlotValidatedTag = new NBTTagCompound();
            for (Int2BooleanMap.Entry entry : this.recipeSlotValidated.int2BooleanEntrySet()) {
                recipeSlotValidatedTag.setBoolean(String.valueOf(entry.getIntKey()), entry.getBooleanValue());
            }
            tag.setTag("recipeSlotValidated", recipeSlotValidatedTag);
            tag.setBoolean("disableCraftingCheck", disableCraftingCheck);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            inventoryVariables.readFromNBT(tag, "variables");

            this.recipeSlotMessages.clear();
            NBTTagCompound recipeSlotErrorsTag = tag.getCompoundTag("recipeSlotMessages");
            for (String slot : recipeSlotErrorsTag.func_150296_c()) {
                LangHelpers.UnlocalizedString unlocalizedString = new LangHelpers.UnlocalizedString();
                unlocalizedString.deserializeNBT(recipeSlotErrorsTag.getCompoundTag(slot));
                this.recipeSlotMessages.put(Integer.parseInt(slot), unlocalizedString);
            }

            this.recipeSlotValidated.clear();
            NBTTagCompound recipeSlotValidatedTag = tag.getCompoundTag("recipeSlotValidated");
            for (String slot : recipeSlotValidatedTag.func_150296_c()) {
                this.recipeSlotValidated.put(Integer.parseInt(slot), recipeSlotValidatedTag.getBoolean(slot));
            }
            this.disableCraftingCheck = tag.getBoolean("disableCraftingCheck");
        }

        @Override
        public void reloadRecipes(boolean initialize) {
            this.currentRecipes.clear();
            this.recipeSlotMessages.clear();
            this.recipeSlotValidated.clear();
            variableEvaluators.clear();
            for (int i = 0; i < getInventoryVariables().getSizeInventory(); i++) {
                int slot = i;
                variableEvaluators
                    .add(new InventoryVariableEvaluator<>(getInventoryVariables(), slot, ValueTypes.OBJECT_RECIPE) {

                        @Override
                        public void onErrorsChanged() {
                            super.onErrorsChanged();
                            setLocalErrors(slot, getErrors());
                        }
                    });
            }
            if (this.partNetwork != null) {
                for (int i = 0; i < getInventoryVariables().getSizeInventory(); i++) {
                    reloadRecipe(i, initialize);
                }
            }
        }

        private void setLocalErrors(int slot, List<LangHelpers.UnlocalizedString> errors) {
            if (errors.isEmpty()) {
                if (this.recipeSlotMessages.size() > slot) {
                    this.recipeSlotMessages.remove(slot);
                }
            } else {
                this.recipeSlotMessages.put(slot, errors.get(0));
            }
        }

        protected void reloadRecipe(int slot, boolean initialize) {
            this.currentRecipes.remove(slot);
            if (this.recipeSlotMessages.size() > slot) {
                this.recipeSlotMessages.remove(slot);
            }
            if (this.recipeSlotValidated.size() > slot) {
                this.recipeSlotValidated.remove(slot);
            }
            if (this.partNetwork != null) {
                InventoryVariableEvaluator<ValueObjectTypeRecipe.ValueRecipe> evaluator = variableEvaluators.get(slot);
                evaluator.refreshVariable(network, false);
                IVariable<ValueObjectTypeRecipe.ValueRecipe> variable = evaluator.getVariable(network);
                if (variable != null) {
                    try {
                        // Refresh the recipe if variable is changed
                        // The map is needed because we only want to register the listener once for each variable
                        if (!this.variableListeners.containsKey(variable)) {
                            variable.addInvalidationListener(() -> {
                                this.variableListeners.remove(variable);
                                delayedReloadRecipe(slot);
                            });
                            this.variableListeners.put(variable, true);
                        }

                        IValue value = variable.getValue();
                        if (value.getType() == ValueTypes.OBJECT_RECIPE) {
                            Optional<IRecipeDefinition> recipeWrapper = ((ValueObjectTypeRecipe.ValueRecipe) value)
                                .getRawValue();
                            if (recipeWrapper.isPresent()) {
                                IRecipeDefinition recipe = recipeWrapper.get();
                                if (!GeneralConfig.validateRecipesCraftingInterface || this.disableCraftingCheck
                                    || isValid(recipe)) {
                                    this.currentRecipes.put(slot, recipe);
                                    this.recipeSlotValidated.put(slot, true);
                                    this.recipeSlotMessages.put(
                                        slot,
                                        new LangHelpers.UnlocalizedString(
                                            "gui.integratedcrafting.partinterface.slot.message.valid"));
                                } else {
                                    this.recipeSlotMessages.put(
                                        slot,
                                        new LangHelpers.UnlocalizedString(
                                            "gui.integratedcrafting.partinterface.slot.message.invalid"));
                                }
                            }
                        } else {
                            this.recipeSlotMessages.put(
                                slot,
                                new LangHelpers.UnlocalizedString(
                                    "gui.integratedcrafting.partinterface.slot.message.norecipe"));
                        }
                    } catch (EvaluationException e) {
                        this.recipeSlotMessages.put(slot, new LangHelpers.UnlocalizedString(e.getLocalizedMessage()));
                    }
                } else {
                    // If we're initializing, the variable might be referencing other variables that are not yet loaded.
                    // So let's retry once in the next tick.
                    if (initialize && evaluator.hasVariable()) {
                        this.delayedReloadRecipe(slot);
                    } else {
                        this.recipeSlotMessages.put(
                            slot,
                            new LangHelpers.UnlocalizedString(
                                "gui.integratedcrafting.partinterface.slot.message.norecipe"));
                    }
                }

                try {
                    IPartNetwork partNetwork = NetworkHelpers.getPartNetworkChecked(network);
                    MinecraftForge.EVENT_BUS.post(
                        new PartVariableDrivenVariableContentsUpdatedEvent<>(
                            network,
                            partNetwork,
                            getTarget(),
                            CraftingPartTypes.INTERFACE_CRAFTING,
                            this,
                            lastPlayer,
                            variable,
                            variable != null ? variable.getValue() : null));
                } catch (EvaluationException e) {
                    // Ignore error
                }
            }
            sendUpdate();
        }

        private void delayedReloadRecipe(int slot) {
            this.delayedRecipeReloads.add(slot);
        }

        private boolean isValid(IRecipeDefinition recipe) {
            DimPos dimPos = getTarget().getTarget()
                .getPos();
            ForgeDirection side = getTarget().getTarget()
                .getSide();
            IRecipeHandler recipeHandler = CapabilityHelpers
                .getCapability(dimPos.getWorld(), dimPos.getBlockPos(), RecipeHandlerConfig.CAPABILITY, side)
                .getOrNull();
            if (recipeHandler == null) {
                BlockState blockState = BlockStateHelpers.getState(dimPos.getWorld(), dimPos.getBlockPos());
                recipeHandler = BlockCapabilities.getInstance()
                    .getCapability(
                        blockState,
                        RecipeHandlerConfig.CAPABILITY,
                        dimPos.getWorld(),
                        dimPos.getBlockPos(),
                        side)
                    .getOrNull();
            }
            if (recipeHandler != null) {
                IMixedIngredients simulatedOutput = recipeHandler.simulate(recipe);
                if (simulatedOutput != null && !simulatedOutput.isEmpty()) {
                    if (recipe.getOutput()
                        .containsAll(simulatedOutput)) {
                        return true;
                    } else {
                        if (GeneralConfig.logRecipeValidationFailures) {
                            IntegratedCrafting.clog(
                                Level.INFO,
                                "Recipe validation failure: incompatible recipe output and simulated output:\nRecipe output: "
                                    + recipe.getOutput()
                                    + "\nSimulated output: "
                                    + simulatedOutput);
                        }
                        return false;
                    }
                }
                if (GeneralConfig.logRecipeValidationFailures) {
                    IntegratedCrafting.clog(
                        Level.INFO,
                        "Recipe validation failure: No output was obtained when simulating a recipe\n" + recipe);
                }
                return false;
            }
            return true; // No recipe handler capability is present, so we can't confirm that the recipe will work.
        }

        @Override
        public void onDirty() {
            super.onDirty();

            // Unregister from the network, when all old recipes are still in place
            if (craftingNetwork != null) {
                craftingNetwork.removeCraftingInterface(getChannelCrafting(), this);
            }

            // Recalculate recipes
            if (getTarget() != null && !getTarget().getCenter()
                .getPos()
                .getWorld().isRemote) {
                reloadRecipes(false);
            }

            // Re-register to the network, to force an update for all new recipes
            if (craftingNetwork != null) {
                craftingNetwork.addCraftingInterface(getChannelCrafting(), this);
            }
        }

        @Override
        public Collection<IRecipeDefinition> getRecipes() {
            return this.currentRecipes.values();
        }

        public Int2ObjectMap<IRecipeDefinition> getRecipesIndexed() {
            return currentRecipes;
        }

        public boolean isRecipeSlotValid(int slot) {
            return this.recipeSlotValidated.containsKey(slot);
        }

        @Nullable
        public LangHelpers.UnlocalizedString getRecipeSlotUnlocalizedMessage(int slot) {
            return this.recipeSlotMessages.get(slot);
        }

        public IntSet getDelayedRecipeReloads() {
            return delayedRecipeReloads;
        }

        public void setDisableCraftingCheck(boolean disableCraftingCheck) {
            if (disableCraftingCheck != this.disableCraftingCheck) {
                this.disableCraftingCheck = disableCraftingCheck;

                this.sendUpdate();
            }
        }

        public boolean isDisableCraftingCheck() {
            return disableCraftingCheck;
        }
    }
}
