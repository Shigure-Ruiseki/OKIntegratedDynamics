package ruiseki.integrateddynamics.api.part;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.IPartNetworkElement;
import ruiseki.integrateddynamics.api.network.event.INetworkEvent;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.IInitListener;

/**
 * Default implementation of {@link IPartType}.
 *
 * @author rubensworks
 */
public abstract class PartTypeAdapter<P extends IPartType<P, S>, S extends IPartState<P>> implements IPartType<P, S> {

    private String unlocalizedName = null;

    @Override
    public String getUnlocalizedName() {
        return unlocalizedName != null ? unlocalizedName : (unlocalizedName = getUnlocalizedNameBase() + ".name");
    }

    @Override
    public boolean isSolid(S state) {
        return false;
    }

    @Override
    public void onInit(IInitListener.Step initStep) {

    }

    @Override
    public void toNBT(NBTTagCompound tag, S partState) {
        partState.writeToNBT(tag);
    }

    @Override
    public S fromNBT(NBTTagCompound tag) {
        S partState = constructDefaultState();
        partState.readFromNBT(tag);
        partState.gatherCapabilities((P) this);
        return partState;
    }

    @Override
    public void setUpdateInterval(S state, int updateInterval) {
        state.setUpdateInterval(updateInterval);
    }

    @Override
    public int getUpdateInterval(S state) {
        return state.getUpdateInterval();
    }

    @Override
    public int getMinimumUpdateInterval(S state) {
        return 1;
    }

    @Override
    public void setPriorityAndChannel(INetwork network, IPartNetwork partNetwork, PartTarget target, S state,
        int priority, int channel) {
        // noinspection deprecation
        state.setPriority(priority);
        state.setChannel(channel);
    }

    @Override
    public int getPriority(S state) {
        return state.getPriority();
    }

    @Override
    public int getChannel(S state) {
        return state.getChannel();
    }

    @Override
    public Vector3i getTargetOffset(S state) {
        return state.getTargetOffset();
    }

    @Override
    public boolean setTargetOffset(S state, PartPos center, Vector3i offset) {
        int max = state.getMaxOffset();
        if (offset.x() >= -max && offset.y() >= -max
            && offset.z() >= -max
            && offset.x() <= max
            && offset.y() <= max
            && offset.z() <= max) {
            state.setTargetOffset(offset);
            return true;
        }
        return false;
    }

    @Override
    public void setTargetSideOverride(S state, @Nullable ForgeDirection side) {
        state.setTargetSideOverride(side);
    }

    @Nullable
    @Override
    public ForgeDirection getTargetSideOverride(S state) {
        return state.getTargetSideOverride();
    }

    @Override
    public PartTarget getTarget(PartPos pos, S state) {
        PartTarget target = PartTarget.fromCenter(pos);
        ForgeDirection sideOverride = getTargetSideOverride(state);
        if (sideOverride != null) {
            target = target.forTargetSide(sideOverride);
        }
        Vector3i offset = getTargetOffset(state);
        if (offset.equals(new Vector3i(0, 0, 0))) {
            target = target.forOffset(offset);
        }
        return target;
    }

    protected boolean hasOffsetVariables(S state) {
        NonNullList<ItemStack> inventory = state.getInventoryNamed("offsetVariablesInventory");
        return inventory != null && inventory.stream()
            .anyMatch(Objects::nonNull);
    }

    @Override
    public void onOffsetVariablesChanged(PartTarget target, S state) {
        state.markOffsetVariablesChanged();
    }

    @Override
    public boolean isUpdate(S state) {
        return hasOffsetVariables(state);
    }

    @Override
    public void update(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        state.updateOffsetVariables((P) this, network, partNetwork, target);
    }

    @Override
    public void beforeNetworkKill(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {

    }

    @Override
    public void afterNetworkAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {

    }

    @Override
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        // This resets any errored offset variables and forces them to reload.
        state.markOffsetVariablesChanged();
    }

    @Override
    public ItemStack getItemStack(S state, boolean saveState) {
        ItemStack itemStack = new ItemStack(getItem());
        if (saveState) {
            NBTTagCompound tag = new NBTTagCompound();
            toNBT(tag, state);
            itemStack.setTagCompound(tag);
        }
        return itemStack;
    }

    @Override
    public ItemStack getPickBlock(World world, BlockPos pos, S state) {
        return getItemStack(state, false);
    }

    @Override
    public S getState(ItemStack itemStack) {
        S partState = null;
        if (itemStack != null && itemStack.getTagCompound() != null
            && itemStack.getTagCompound()
                .hasKey("id", MinecraftHelpers.NBTTag_Types.NBTTagInt.ordinal())) {
            partState = fromNBT(itemStack.getTagCompound());
        }
        if (partState == null) {
            partState = getDefaultState();
        }
        return partState;
    }

    /**
     * @return Constructor call for a new default state for this part type.
     */
    protected abstract S constructDefaultState();

    @Override
    public S getDefaultState() {
        S defaultState = constructDefaultState();
        defaultState.generateId();
        defaultState.gatherCapabilities((P) this);
        return defaultState;
    }

    @Override
    public void addDrops(PartTarget target, S state, List<ItemStack> itemStacks, boolean dropMainElement,
        boolean saveState) {
        if (dropMainElement) {
            itemStacks.add(getItemStack(state, saveState));
        }

        // Drop contents of named inventories
        for (Map.Entry<String, NonNullList<ItemStack>> entry : state.getInventoriesNamed()
            .entrySet()) {
            for (ItemStack itemStack : entry.getValue()) {
                if (itemStack != null) {
                    itemStacks.add(itemStack);
                }
            }
        }
        state.clearInventoriesNamed();
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        state.initializeOffsets();
    }

    @Override
    public void onNetworkRemoval(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {

    }

    @Override
    public boolean onPartActivated(World world, BlockPos pos, S partState, EntityPlayer player, ItemStack heldItem,
        ForgeDirection side, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void updateTick(World world, BlockPos pos, S partState, Random random) {

    }

    @Override
    public void onPreRemoved(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {

    }

    @Override
    public void onPostRemoved(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {

    }

    @Override
    public void onBlockNeighborChange(INetwork network, IPartNetwork partNetwork, PartTarget target, S state,
        IBlockAccess world, Block neighbourBlock, BlockPos neighbourBlockPos) {}

    @Override
    public int getConsumptionRate(S state) {
        return 0;
    }

    @Override
    public void postUpdate(INetwork network, IPartNetwork partNetwork, PartTarget target, S state, boolean updated) {
        setEnabled(state, updated);
    }

    @Override
    public boolean isEnabled(S state) {
        return state.isEnabled();
    }

    @Override
    public void setEnabled(S state, boolean enabled) {
        state.setEnabled(enabled);
    }

    @Override
    public void loadTooltip(S state, List<String> lines) {

    }

    @Override
    public void loadTooltip(ItemStack itemStack, List<String> lines) {

    }

    @Override
    public boolean shouldTriggerBlockRenderUpdate(@Nullable S oldPartState, @Nullable S newPartState) {
        return oldPartState == null || newPartState == null || oldPartState.isForceBlockRenderUpdateAndReset();
    }

    @Override
    public boolean hasEventSubscriptions() {
        return false;
    }

    @Override
    public Set<Class<? extends INetworkEvent>> getSubscribedEvents() {
        return Collections.emptySet();
    }

    @Override
    public void onEvent(INetworkEvent event, IPartNetworkElement<P, S> networkElement) {

    }
}
