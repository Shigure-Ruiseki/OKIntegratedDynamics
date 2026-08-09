package ruiseki.integrateddynamics.tileentity;

import java.util.Collection;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.block.IVariableContainer;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import ruiseki.integrateddynamics.capability.variablecontainer.VariableContainerConfig;
import ruiseki.integrateddynamics.capability.variablecontainer.VariableContainerDefault;
import ruiseki.integrateddynamics.capability.variablefacade.VariableFacadeHolderConfig;
import ruiseki.integrateddynamics.core.network.event.VariableContentsUpdatedEvent;
import ruiseki.integrateddynamics.core.tileentity.TileCableConnectableInventory;
import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.integrateddynamics.network.VariablestoreNetworkElement;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * A tile entity used to store variables.
 * Internally, this also acts as an expression cache
 *
 * @author rubensworks
 */
public class TileVariablestore extends TileCableConnectableInventory implements IDirtyMarkListener {

    public static final int ROWS = 5;
    public static final int COLS = 9;
    private Map<Integer, IVariableFacade> variableCache = Maps.newHashMap();

    private final IVariableContainer variableContainer;

    private boolean shouldSendUpdateEvent = false;

    public TileVariablestore() {
        super(ROWS * COLS, "variables", 1);
        inventory.addDirtyMarkListener(this);

        // Make all sides active for all slots
        Collection<Integer> slots = Lists.newArrayListWithCapacity(getInventory().getSizeInventory());
        for (int i = 0; i < getInventory().getSizeInventory(); i++) {
            slots.add(i);
        }
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            addSlotsToSide(side, slots);
        }

        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver
                .create(NetworkElementProviderConfig.CAPABILITY, () -> new NetworkElementProviderSingleton() {

                    @Override
                    public INetworkElement createNetworkElement(World world, BlockPos blockPos) {
                        return new VariablestoreNetworkElement(DimPos.of(world, blockPos));
                    }
                }));
        variableContainer = new VariableContainerDefault();
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(VariableContainerConfig.CAPABILITY, () -> variableContainer));
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return super.isItemValidForSlot(index, stack)
            && (stack == null || CapabilityHelpers.getCapability(stack, VariableFacadeHolderConfig.CAPABILITY)
                .isPresent());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        refreshVariables(inventory);
    }

    protected void refreshVariables(IInventory inventory) {
        variableCache.clear();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null) {
                IVariableFacade variableFacade = ItemVariable.getInstance()
                    .getVariableFacade(itemStack);
                if (variableFacade != null && variableFacade.isValid()) {
                    variableCache.put(variableFacade.getId(), variableFacade);
                }
            }
        }

        INetwork network = getNetwork();
        if (network != null) {
            network.getEventBus()
                .post(new VariableContentsUpdatedEvent(network));
        }
    }

    @Override
    public void onDirty() {
        if (!getWorldObj().isRemote) {
            refreshVariables(inventory);
        }
    }

    // Make sure that when this TE is loaded, and after the network has been set,
    // that we trigger a variable update event in the network.

    @Override
    public void onLoad() {
        super.onLoad();
        if (!MinecraftHelpers.isClientSide()) {
            shouldSendUpdateEvent = true;
        }
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        if (shouldSendUpdateEvent && getNetwork() != null) {
            shouldSendUpdateEvent = false;
            refreshVariables(inventory);
        }
    }
}
