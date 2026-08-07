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
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.capability.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.NetworkElementProviderSingleton;
import ruiseki.integrateddynamics.core.network.event.VariableContentsUpdatedEvent;
import ruiseki.integrateddynamics.core.tileentity.TileCableConnectableInventory;
import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.integrateddynamics.network.VariablestoreNetworkElement;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * A tile entity used to store variables.
 * Internally, this also acts as an expression cache
 *
 * @author rubensworks
 */
public class TileVariablestore extends TileCableConnectableInventory implements IVariableContainer, IDirtyMarkListener {

    public static final int ROWS = 5;
    public static final int COLS = 9;
    private Map<Integer, IVariableFacade> variableCache = Maps.newHashMap();

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
            BasicCapabilityResolver.create(
                NetworkElementProviderConfig.CAPABILITY,
                () -> new NetworkElementProviderSingleton<IPartNetwork>() {

                    @Override
                    public INetworkElement<IPartNetwork> createNetworkElement(World world, BlockPos blockPos) {
                        return new VariablestoreNetworkElement(DimPos.of(world, blockPos));
                    }
                }));
    }

    @Override
    public void readCommon(NBTTagCompound tag) {
        super.readCommon(tag);
        refreshVariables(inventory);
    }

    protected void refreshVariables(IInventory inventory) {
        variableCache.clear();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null) {
                IVariableFacade variableFacade = ItemVariable.getInstance()
                    .getVariableFacade(itemStack);
                if (variableFacade.isValid()) {
                    variableCache.put(variableFacade.getId(), variableFacade);
                }
            }
        }

        IPartNetwork network = getNetwork();
        if (network != null) {
            network.getEventBus()
                .post(new VariableContentsUpdatedEvent(network));
        }
    }

    @Override
    public DimPos getPosition() {
        return DimPos.of(this.getWorldObj(), getPos());
    }

    @Override
    public Map<Integer, IVariableFacade> getVariableCache() {
        return variableCache;
    }

    @Override
    public void onDirty() {
        if (!getWorldObj().isRemote) {
            refreshVariables(inventory);
        }
    }
}
