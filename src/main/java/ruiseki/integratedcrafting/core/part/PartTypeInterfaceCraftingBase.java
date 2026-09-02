package ruiseki.integratedcrafting.core.part;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.IngredientInstanceWrapper;
import ruiseki.commoncapabilities.api.ingredient.MixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integratedcrafting.GeneralConfig;
import ruiseki.integratedcrafting.api.crafting.CraftingJob;
import ruiseki.integratedcrafting.api.crafting.CraftingJobStatus;
import ruiseki.integratedcrafting.api.crafting.ICraftingInterface;
import ruiseki.integratedcrafting.api.crafting.ICraftingResultsSink;
import ruiseki.integratedcrafting.api.network.ICraftingNetwork;
import ruiseki.integratedcrafting.capability.network.CraftingInterfaceConfig;
import ruiseki.integratedcrafting.capability.network.CraftingNetworkConfig;
import ruiseki.integratedcrafting.core.CraftingHelpers;
import ruiseki.integratedcrafting.core.CraftingJobHandler;
import ruiseki.integratedcrafting.core.CraftingProcessOverrides;
import ruiseki.integratedcrafting.ingredient.storage.IngredientComponentStorageSlottedInsertProxy;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.PrioritizedPartPos;
import ruiseki.integrateddynamics.capability.network.PositionedAddonsNetworkIngredientsHandlerConfig;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.part.PartStateBase;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * Base logic for parts that do crafting interfacing.
 *
 * @author rubensworks
 */
public abstract class PartTypeInterfaceCraftingBase<P extends PartTypeInterfaceCraftingBase<P, S>, S extends PartTypeInterfaceCraftingBase.State<P, S>>
    extends PartTypeCraftingBase<P, S> {

    public PartTypeInterfaceCraftingBase(String name) {
        super(name);
    }

    @Override
    public Class<? super P> getPartTypeClass() {
        return PartTypeCraftingBase.class;
    }

    @Override
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.afterNetworkReAlive(network, partNetwork, target, state);
        addTargetToNetwork(network, target, state, true);
    }

    @Override
    public void onNetworkRemoval(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkRemoval(network, partNetwork, target, state);
        removeTargetFromNetwork(network, target.getTarget(), state);
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkAddition(network, partNetwork, target, state);
        addTargetToNetwork(network, target, state, true);
    }

    @Override
    public void setPriorityAndChannel(INetwork network, IPartNetwork partNetwork, PartTarget target, S state,
        int priority, int channel) {
        // We need to do this because the crafting network is not automagically aware of the priority changes,
        // so we have to re-add it.
        removeTargetFromNetwork(network, target.getTarget(), state);
        super.setPriorityAndChannel(network, partNetwork, target, state, priority, channel);
        addTargetToNetwork(network, target, state, false);
    }

    protected Capability<ICraftingNetwork> getNetworkCapability() {
        return CraftingNetworkConfig.CAPABILITY;
    }

    protected void addTargetToNetwork(INetwork network, PartTarget pos, State state, boolean initialize) {
        network.getCapability(getNetworkCapability())
            .ifPresent(craftingNetwork -> {
                int channel = state.getChannel();
                state.setTarget(pos);
                state.setNetworks(
                    network,
                    craftingNetwork,
                    NetworkHelpers.getPartNetworkChecked(network),
                    channel,
                    initialize);
                state.setShouldAddToCraftingNetwork(true);
            });
    }

    protected void removeTargetFromNetwork(INetwork network, PartPos pos, S state) {
        ICraftingNetwork craftingNetwork = state.getCraftingNetwork();
        if (craftingNetwork != null) {
            network.getCapability(getNetworkCapability())
                .ifPresent(n -> n.removeCraftingInterface(state.getChannelCrafting(), state));
        }
        state.setNetworks(null, null, null, -1, false);
        state.setTarget(null);
    }

    @Override
    public boolean isUpdate(S state) {
        return true;
    }

    @Override
    public int getMinimumUpdateInterval(S state) {
        return state.getDefaultUpdateInterval();
    }

    @Nullable
    protected static <T, M> IngredientInstanceWrapper<T, M> insertIntoNetwork(IngredientInstanceWrapper<T, M> wrapper,
        INetwork network, int channel) {
        IPositionedAddonsNetworkIngredients<T, M> storageNetwork = wrapper.getComponent()
            .getCapability(PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY)
            .map(
                n -> (IPositionedAddonsNetworkIngredients<T, M>) n.getStorage(network)
                    .orElse(null))
            .orElse(null);
        if (storageNetwork != null) {
            IIngredientComponentStorage<T, M> storage = storageNetwork.getChannel(channel);
            T remaining = storage.insert(wrapper.getInstance(), false);
            if (wrapper.getComponent()
                .getMatcher()
                .isEmpty(remaining)) {
                return null;
            } else {
                return new IngredientInstanceWrapper<>(wrapper.getComponent(), remaining);
            }
        }
        return wrapper;
    }

    @Override
    public void update(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.update(network, partNetwork, target, state);

        // Init network data in part state if it has not been done yet.
        // This can occur when the part chunk is being reloaded.
        if (state.getCraftingNetwork() == null) {
            addTargetToNetwork(network, target, state, false);
        }

        int channelCrafting = state.getChannelCrafting();

        // Update the network data in the part state
        if (state.shouldAddToCraftingNetwork()) {
            ICraftingNetwork craftingNetwork = network.getCapability(getNetworkCapability())
                .orElse(null);
            craftingNetwork.addCraftingInterface(channelCrafting, state);
            state.setShouldAddToCraftingNetwork(false);
        }

        // Push any pending output ingredients into the network
        state.flushInventoryOutputBuffer(network);

        // Block job ticking if there still are outputs in our crafting result buffer.
        if (state.getInventoryOutputBuffer()
            .isEmpty()) {
            // Tick the job handler
            PartPos targetPos = state.getTarget()
                .getTarget();
            state.getCraftingJobHandler()
                .update(network, channelCrafting, targetPos);
        }
    }

    @Override
    public void addDrops(PartTarget target, S state, List<ItemStack> itemStacks, boolean dropMainElement,
        boolean saveState) {
        // Drop any remaining output ingredients (only items)
        for (IngredientInstanceWrapper<?, ?> ingredientInstanceWrapper : state.getInventoryOutputBuffer()) {
            if (ingredientInstanceWrapper.getComponent() == IngredientComponent.ITEMSTACK) {
                itemStacks.add((ItemStack) ingredientInstanceWrapper.getInstance());
            }
        }
        state.getInventoryOutputBuffer()
            .clear();

        // Drop buffered items from running crafting jobs (only items)
        for (CraftingJob craftingJob : state.getCraftingJobHandler()
            .getAllCraftingJobs()
            .values()) {
            for (ItemStack instance : craftingJob.getIngredientsStorageBuffer()
                .getInstances(IngredientComponent.ITEMSTACK)) {
                itemStacks.add(instance);
            }
            craftingJob.setIngredientsStorageBuffer(new MixedIngredients(Maps.newIdentityHashMap()));
        }

        super.addDrops(target, state, itemStacks, dropMainElement, saveState);
    }

    public static abstract class State<P extends PartTypeInterfaceCraftingBase<P, S>, S extends PartTypeInterfaceCraftingBase.State<P, S>>
        extends PartStateBase<P> implements ICraftingInterface, ICraftingResultsSink {

        private final CraftingJobHandler craftingJobHandler;
        private final List<IngredientInstanceWrapper<?, ?>> inventoryOutputBuffer;

        private int channelCrafting = 0;
        private PartTarget target = null;
        protected INetwork network = null;
        protected IPartNetwork partNetwork = null;
        protected ICraftingNetwork craftingNetwork = null;
        private int channel = -1;
        private boolean shouldAddToCraftingNetwork = false;
        protected EntityPlayer lastPlayer;

        public State() {
            this.craftingJobHandler = new CraftingJobHandler(
                1,
                true,
                CraftingProcessOverrides.REGISTRY.getCraftingProcessOverrides(),
                this);
            this.inventoryOutputBuffer = Lists.newArrayList();
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);

            NBTTagList instanceTags = new NBTTagList();
            for (IngredientInstanceWrapper instanceWrapper : inventoryOutputBuffer) {
                NBTTagCompound instanceTag = new NBTTagCompound();
                instanceTag.setString(
                    "component",
                    instanceWrapper.getComponent()
                        .getName()
                        .toString());
                instanceTag.setTag(
                    "instance",
                    instanceWrapper.getComponent()
                        .getSerializer()
                        .serializeInstance(instanceWrapper.getInstance()));
                instanceTags.appendTag(instanceTag);
            }
            tag.setTag("inventoryOutputBuffer", instanceTags);

            this.craftingJobHandler.writeToNBT(tag);
            tag.setInteger("channelCrafting", channelCrafting);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);

            this.inventoryOutputBuffer.clear();
            for (Object instanceTagRaw : tag.getTagList("inventoryOutputBuffer", Constants.NBT.TAG_COMPOUND).tagList) {
                NBTTagCompound instanceTag = (NBTTagCompound) instanceTagRaw;
                String componentName = instanceTag.getString("component");
                IngredientComponent<?, ?> component = IngredientComponent.REGISTRY
                    .getValue(new ResourceLocation(componentName));
                this.inventoryOutputBuffer.add(
                    new IngredientInstanceWrapper(
                        component,
                        component.getSerializer()
                            .deserializeInstance(instanceTag.getTag("instance"))));
            }

            this.craftingJobHandler.readFromNBT(tag);
            this.channelCrafting = tag.getInteger("channelCrafting");
        }

        @Override
        protected int getDefaultUpdateInterval() {
            return GeneralConfig.minCraftingInterfaceUpdateFreq;
        }

        public void setChannelCrafting(int channelCrafting) {
            if (this.channelCrafting != channelCrafting) {
                // Unregister from the network
                if (craftingNetwork != null) {
                    craftingNetwork.removeCraftingInterface(this.channelCrafting, this);
                }

                // Update the channel
                this.channelCrafting = channelCrafting;

                // Re-register to the network
                if (craftingNetwork != null) {
                    craftingNetwork.addCraftingInterface(this.channelCrafting, this);
                }

                sendUpdate();
            }
        }

        public int getChannelCrafting() {
            return channelCrafting;
        }

        public void setTarget(PartTarget target) {
            this.target = target;
        }

        public PartTarget getTarget() {
            return target;
        }

        public void setNetworks(@Nullable INetwork network, @Nullable ICraftingNetwork craftingNetwork,
            @Nullable IPartNetwork partNetwork, int channel, boolean initialize) {
            this.network = network;
            this.craftingNetwork = craftingNetwork;
            this.partNetwork = partNetwork;
            this.setChannel(channel);
            reloadRecipes(initialize);
        }

        public void reloadRecipes(boolean initialize) {
            // Do nothing
        }

        public void setLastPlayer(EntityPlayer lastPlayer) {
            this.lastPlayer = lastPlayer;
        }

        public ICraftingNetwork getCraftingNetwork() {
            return craftingNetwork;
        }

        @Override
        public boolean canScheduleCraftingJobs() {
            return getCraftingJobHandler().canScheduleCraftingJobs();
        }

        @Override
        public void scheduleCraftingJob(CraftingJob craftingJob) {
            getCraftingJobHandler().scheduleCraftingJob(craftingJob);
        }

        @Override
        public void fillCraftingJobBufferFromStorage(CraftingJob craftingJob,
            Function<IngredientComponent<?, ?>, IIngredientComponentStorage> storageGetter) {
            getCraftingJobHandler().fillCraftingJobBufferFromStorage(craftingJob, storageGetter);
        }

        @Override
        public int getCraftingJobsCount() {
            return this.craftingJobHandler.getAllCraftingJobs()
                .size();
        }

        @Override
        public Iterator<CraftingJob> getCraftingJobs() {
            return this.craftingJobHandler.getAllCraftingJobs()
                .values()
                .iterator();
        }

        @Override
        public List<Map<IngredientComponent<?, ?>, List<IPrototypedIngredient<?, ?>>>> getPendingCraftingJobOutputs(
            int craftingJobId) {
            List<Map<IngredientComponent<?, ?>, List<IPrototypedIngredient<?, ?>>>> pending = this.craftingJobHandler
                .getProcessingCraftingJobsPendingIngredients()
                .get(craftingJobId);
            if (pending == null) {
                pending = Lists.newArrayList();
            }
            return pending;
        }

        @Override
        public CraftingJobStatus getCraftingJobStatus(ICraftingNetwork network, int channel, int craftingJobId) {
            return craftingJobHandler.getCraftingJobStatus(network, channel, craftingJobId);
        }

        @Override
        public void cancelCraftingJob(int channel, int craftingJobId) {
            craftingJobHandler.markCraftingJobFinished(craftingJobId);
        }

        @Override
        public PrioritizedPartPos getPosition() {
            return PrioritizedPartPos.of(getTarget().getCenter(), getPriority());
        }

        public CraftingJobHandler getCraftingJobHandler() {
            return craftingJobHandler;
        }

        public boolean shouldAddToCraftingNetwork() {
            return shouldAddToCraftingNetwork;
        }

        public void setShouldAddToCraftingNetwork(boolean shouldAddToCraftingNetwork) {
            this.shouldAddToCraftingNetwork = shouldAddToCraftingNetwork;
        }

        public List<IngredientInstanceWrapper<?, ?>> getInventoryOutputBuffer() {
            return inventoryOutputBuffer;
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, INetwork network, IPartNetwork partNetwork,
            PartTarget target) {
            if (capability == CraftingInterfaceConfig.CAPABILITY) {
                return LazyOptional.of(() -> this)
                    .cast();
            }

            // Expose the whole storage
            if (this.network != null) {
                IngredientComponent<?, ?> ingredientComponent = IngredientComponent
                    .getIngredientComponentForStorageCapability(capability);
                if (ingredientComponent != null) {
                    T cap = wrapStorageCapability(capability, ingredientComponent);
                    if (cap != null) {
                        return LazyOptional.of(() -> cap);
                    }
                }
            }

            return super.getCapability(capability, network, partNetwork, target);
        }

        protected <C, T, M> C wrapStorageCapability(Capability<C> capability,
            IngredientComponent<T, M> ingredientComponent) {
            IIngredientComponentStorage<T, M> storage = CraftingHelpers
                .getNetworkStorage(this.network, this.channelCrafting, ingredientComponent, false);

            // Don't allow extraction, only insertion
            storage = new IngredientComponentStorageSlottedInsertProxy<>(storage);

            return ingredientComponent.getStorageWrapperHandler(capability)
                .wrapStorage(storage);
        }

        @Override
        public <T, M> void addResult(IngredientComponent<T, M> ingredientComponent, T instance) {
            this.getInventoryOutputBuffer()
                .add(new IngredientInstanceWrapper<>(ingredientComponent, instance));

            // Try to flush buffer immediately
            if (this.network != null) {
                this.flushInventoryOutputBuffer(this.network);
            }
        }

        public void setIngredientComponentTargetSideOverride(IngredientComponent<?, ?> ingredientComponent,
            ForgeDirection side) {
            if (getTarget().getTarget()
                .getSide() == side) {
                craftingJobHandler.setIngredientComponentTarget(ingredientComponent, null);
            } else {
                craftingJobHandler.setIngredientComponentTarget(ingredientComponent, side);
            }
            sendUpdate();
        }

        public ForgeDirection getIngredientComponentTargetSideOverride(IngredientComponent<?, ?> ingredientComponent) {
            ForgeDirection side = craftingJobHandler.getIngredientComponentTarget(ingredientComponent);
            if (side == null) {
                side = getTarget().getTarget()
                    .getSide();
            }
            return side;
        }

        public void flushInventoryOutputBuffer(INetwork network) {
            // Try to insert each ingredient in the buffer into the network.

            ListIterator<IngredientInstanceWrapper<?, ?>> outputBufferIt = this.getInventoryOutputBuffer()
                .listIterator();
            while (outputBufferIt.hasNext()) {
                IngredientInstanceWrapper<?, ?> remainingInstance = outputBufferIt.next();

                // First try to give the ingredients to pending crafting jobs.
                remainingInstance = getCraftingJobHandler()
                    .beforeFlushIngredientToNetwork(remainingInstance, channelCrafting);

                // If none of the jobs need it, dump it into the network.
                remainingInstance = insertIntoNetwork(remainingInstance, network, this.getChannelCrafting());
                if (remainingInstance == null) {
                    outputBufferIt.remove();
                } else {
                    outputBufferIt.set(remainingInstance);
                }
            }
        }
    }

}
