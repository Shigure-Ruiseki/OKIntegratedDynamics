package ruiseki.integrateddynamics.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.item.IProxyVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import ruiseki.integrateddynamics.core.evaluate.ProxyVariableFacadeHandler;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.item.ProxyVariableFacade;
import ruiseki.integrateddynamics.core.tileentity.TileActiveVariableBase;
import ruiseki.integrateddynamics.network.ProxyNetworkElement;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.persist.nbt.NBTPersist;

/**
 * A tile entity for the variable proxy.
 *
 * @author rubensworks
 */
public class TileProxy extends TileActiveVariableBase<ProxyNetworkElement> {

    public static final int SLOT_READ = 0;
    public static final int SLOT_WRITE_IN = 1;
    public static final int SLOT_WRITE_OUT = 2;

    public static final String GLOBALCOUNTER_KEY = "proxy";

    @NBTPersist
    @Getter
    private int proxyId = -1;

    @Setter
    private EntityPlayer lastPlayer = null;

    public TileProxy() {
        super(3, "proxy");

        addSlotsToSide(ForgeDirection.UP, Sets.newHashSet(SLOT_READ));
        addSlotsToSide(ForgeDirection.DOWN, Sets.newHashSet(SLOT_READ));
        addSlotsToSide(ForgeDirection.SOUTH, Sets.newHashSet(SLOT_READ));
        addSlotsToSide(ForgeDirection.WEST, Sets.newHashSet(SLOT_WRITE_OUT));
        addSlotsToSide(ForgeDirection.EAST, Sets.newHashSet(SLOT_WRITE_IN));

        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver
                .create(NetworkElementProviderConfig.CAPABILITY, () -> new NetworkElementProviderSingleton() {

                    @Override
                    public INetworkElement createNetworkElement(World world, BlockPos blockPos) {
                        return new ProxyNetworkElement(DimPos.of(world, blockPos));
                    }
                }));
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return slot != SLOT_WRITE_OUT && super.canInsertItem(slot, itemStack, side);
    }

    /**
     * This will generate a new proxy id.
     * Be careful when calling this!
     */
    public void generateNewProxyId() {
        this.proxyId = IntegratedDynamics.globalCounters.getNext(GLOBALCOUNTER_KEY);
        markDirty();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!worldObj.isRemote && this.proxyId == -1) {
            generateNewProxyId();
        }
    }

    @Override
    public int getSlotRead() {
        return SLOT_READ;
    }

    @Override
    public void onDirty() {
        super.onDirty();
        if (!worldObj.isRemote) {
            if (getStackInSlot(SLOT_WRITE_IN) != null && getStackInSlot(SLOT_WRITE_OUT) == null) {
                // Write proxy reference
                ItemStack outputStack = writeProxyInfo(
                    !getWorldObj().isRemote,
                    getStackInSlotOnClosing(SLOT_WRITE_IN),
                    proxyId);
                setInventorySlotContents(SLOT_WRITE_OUT, outputStack);
            }
        }
    }

    public ItemStack writeProxyInfo(boolean generateId, ItemStack itemStack, final int proxyId) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(
            generateId,
            itemStack,
            ProxyVariableFacadeHandler.getInstance(),
            new IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IProxyVariableFacade>() {

                @Override
                public IProxyVariableFacade create(boolean generateId) {
                    return new ProxyVariableFacade(generateId, proxyId);
                }

                @Override
                public IProxyVariableFacade create(int id) {
                    return new ProxyVariableFacade(id, proxyId);
                }
            },
            lastPlayer,
            getBlock());
    }

    @Override
    protected void preValidate(IVariableFacade variableStored) {
        super.preValidate(variableStored);
        // Hard check to make sure the variable is not directly referring to this proxy.
        if (variableStored instanceof IProxyVariableFacade) {
            if (((IProxyVariableFacade) variableStored).getProxyId() == getProxyId()) {
                addError(
                    new LangHelpers.UnlocalizedString(L10NValues.VARIABLE_ERROR_RECURSION, variableStored.getId()));
            }
        }
    }
}
