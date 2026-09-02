package ruiseki.integratedterminals.api.terminalstorage.crafting;

import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentServer;

/**
 * Handles crafting actions inside ingredient-based terminal storage tabs.
 *
 * @param <O> The type of crafting option.
 * @param <I> The type of crafting plan identifier.
 * @author rubensworks
 */
public interface ITerminalStorageTabIngredientCraftingHandler<O extends ITerminalCraftingOption<?>, I> {

    /**
     * @return The unique id of this handler.
     */
    public ResourceLocation getId();

    /**
     * @param tab An ingredient tab.
     * @param <T> The instance type.
     * @param <M> The matching condition parameter.
     * @return All channels that have crafting options.
     */
    public <T, M> int[] getChannels(TerminalStorageTabIngredientComponentServer<T, M> tab);

    /**
     * Get all crafting options in the given tab.
     *
     * @param tab     An ingredient tab.
     * @param channel The channel to get the options for.
     * @param <T>     The instance type.
     * @param <M>     The matching condition parameter.
     * @return Crafting options.
     */
    public <T, M> Collection<O> getCraftingOptions(TerminalStorageTabIngredientComponentServer<T, M> tab, int channel);

    /**
     * Get all crafting options in the given tab that produce the given output instance.
     * 
     * @param tab            An ingredient tab.
     * @param channel        The channel to get the options for.
     * @param instance       The expect output instance.
     * @param matchCondition The expected output match condition.
     * @param <T>            The instance type.
     * @param <M>            The matching condition parameter.
     * @return Crafting options.
     */
    public <T, M> Collection<O> getCraftingOptionsWithOutput(TerminalStorageTabIngredientComponentServer<T, M> tab,
        int channel, T instance, M matchCondition);

    /**
     * Serialize a crafting option to NBT.
     *
     * @param craftingOption A crafting option.
     * @return An NBT tag.
     */
    public NBTTagCompound serializeCraftingOption(O craftingOption);

    /**
     * Deserialize a crafting option from NBT.
     *
     * @param ingredientComponent The ingredient component for the crafting option.
     * @param tag                 An NBT tag representing a crafting option.
     * @param <T>                 The instance type.
     * @param <M>                 The matching condition parameter.
     * @return A crafting option.
     * @throws IllegalArgumentException If the given tag was invalid.
     */
    public <T, M> O deserializeCraftingOption(IngredientComponent<T, M> ingredientComponent, NBTTagCompound tag)
        throws IllegalArgumentException;

    /**
     * Calculate a crafting plan for the given crafting option.
     *
     * @param network        The network in which the plan should be calculated.
     * @param channel        The channel to get the options for.
     * @param craftingOption A crafting option.
     * @param quantity       The requested output quantity.
     * @return The calculated crafting plan.
     */
    public ITerminalCraftingPlan<I> calculateCraftingPlan(INetwork network, int channel,
        ITerminalCraftingOption craftingOption, long quantity);

    /**
     * Serialize a crafting plan to NBT.
     *
     * @param craftingPlan A crafting plan.
     * @return An NBT tag.
     */
    public default NBTTagCompound serializeCraftingPlan(ITerminalCraftingPlan<I> craftingPlan) {
        return TerminalCraftingPlanStatic.serialize((TerminalCraftingPlanStatic) craftingPlan, this);
    }

    /**
     * Deserialize a crafting plan from NBT.
     *
     * @param tag An NBT tag representing a crafting plan.
     * @return A crafting option.
     * @throws IllegalArgumentException If the given tag was invalid.
     */
    public default ITerminalCraftingPlan<I> deserializeCraftingPlan(NBTTagCompound tag)
        throws IllegalArgumentException {
        return TerminalCraftingPlanStatic.deserialize(tag, this);
    }

    /**
     * Serialize a flat crafting plan to NBT.
     *
     * @param craftingPlan A flat crafting plan.
     * @return An NBT tag.
     */
    public default NBTTagCompound serializeCraftingPlanFlat(ITerminalCraftingPlanFlat<I> craftingPlan) {
        return TerminalCraftingPlanFlatStatic.serialize((TerminalCraftingPlanFlatStatic) craftingPlan, this);
    }

    /**
     * Deserialize a flat crafting plan from NBT.
     *
     * @param tag An NBT tag representing a flat crafting plan.
     * @return A crafting option.
     * @throws IllegalArgumentException If the given tag was invalid.
     */
    public default ITerminalCraftingPlanFlat<I> deserializeCraftingPlanFlat(NBTTagCompound tag)
        throws IllegalArgumentException {
        return TerminalCraftingPlanFlatStatic.deserialize(tag, this);
    }

    /**
     * Serializes a crafting job id.
     *
     * @param id An id.
     * @return An NBT tag.
     */
    public NBTBase serializeCraftingJobId(I id);

    /**
     * Deserialize a crafting job id.
     *
     * @param tag An NBT tag.
     * @return An id.
     */
    public I deserializeCraftingJobId(NBTBase tag);

    /**
     * Start a crafting job.
     *
     * @param network      The network in which the plan should be started.
     * @param channel      The channel to get the options for.
     * @param craftingPlan A crafting plan.
     * @param player       The player that started the crafting job.
     * @throws CraftingJobStartException If the crafting job failed to start.
     */
    public void startCraftingJob(INetwork network, int channel, ITerminalCraftingPlan<I> craftingPlan,
        EntityPlayerMP player) throws CraftingJobStartException;

    /**
     * @param network The network in which the plan should be started.
     * @param channel The channel to get the options for.
     * @return All running crafting plans.
     */
    public List<ITerminalCraftingPlan<I>> getCraftingJobs(INetwork network, int channel);

    /**
     * Get the crafting job with the given id.
     *
     * @param network       The network in which the plan should be started.
     * @param channel       The channel to get the options for.
     * @param craftingJobId A crafting job id.
     * @return A crafting job or null.
     */
    @Nullable
    public ITerminalCraftingPlan<I> getCraftingJob(INetwork network, int channel, I craftingJobId);

    /**
     * Cancel the crafting job with the given id.
     *
     * @param network       The network in which the plan should be started.
     * @param channel       The channel to get the options for.
     * @param craftingJobId A crafting job id.
     * @return If the crafting job was successfully cancelled.
     */
    public boolean cancelCraftingJob(INetwork network, int channel, I craftingJobId);

}
