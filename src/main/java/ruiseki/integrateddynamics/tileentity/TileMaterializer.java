package ruiseki.integrateddynamics.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Sets;

import lombok.Setter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IValueTypeVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeVariableFacade;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.tileentity.TileActiveVariableBase;
import ruiseki.integrateddynamics.network.MaterializerNetworkElement;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A part entity for the variable materializer.
 *
 * @author rubensworks
 */
public class TileMaterializer extends TileActiveVariableBase<MaterializerNetworkElement> {

    public static final int SLOT_READ = 0;
    public static final int SLOT_WRITE_IN = 1;
    public static final int SLOT_WRITE_OUT = 2;

    @Setter
    private EntityPlayer lastPlayer = null;
    private boolean writeVariable;

    public TileMaterializer() {
        super(3, "materializer");

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
                        return new MaterializerNetworkElement(DimPos.of(world, blockPos));
                    }
                }));
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return slot != SLOT_WRITE_OUT && super.canInsertItem(slot, itemStack, side);
    }

    @Override
    public int getSlotRead() {
        return SLOT_READ;
    }

    protected boolean canWrite() {
        return NetworkHelpers.getPartNetwork(getNetwork())
            .map(
                partNetwork -> getVariable(partNetwork) != null && getEvaluator().getErrors()
                    .isEmpty())
            .orElse(false);
    }

    @Override
    public void onDirty() {
        super.onDirty();
        if (!worldObj.isRemote) {
            this.writeVariable = true;
        }
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();

        if (!worldObj.isRemote && this.writeVariable
            && getInventory().getStackInSlot(SLOT_WRITE_IN) != null
            && canWrite()
            && getInventory().getStackInSlot(SLOT_WRITE_OUT) == null) {
            this.writeVariable = false;

            // Write proxy reference
            ItemStack outputStack = writeMaterialized(
                !getWorldObj().isRemote,
                getInventory().getStackInSlot(SLOT_WRITE_IN));
            if (outputStack != null) {
                getInventory().setInventorySlotContents(SLOT_WRITE_OUT, outputStack);
                getInventory().removeStackFromSlot(SLOT_WRITE_IN);
            }
        }
    }

    public ItemStack writeMaterialized(boolean generateId, ItemStack itemStack) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class);
        IVariable variable = getVariable(NetworkHelpers.getPartNetworkChecked(getNetwork()));
        try {
            final IValue value = variable.getType()
                .materialize(variable.getValue());
            final IValueType valueType = value.getType();
            return registry.writeVariableFacadeItem(
                generateId,
                itemStack,
                ValueTypes.REGISTRY,
                new IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IValueTypeVariableFacade>() {

                    @Override
                    public IValueTypeVariableFacade create(boolean generateId) {
                        return new ValueTypeVariableFacade(generateId, valueType, value);
                    }

                    @Override
                    public IValueTypeVariableFacade create(int id) {
                        return new ValueTypeVariableFacade(id, valueType, value);
                    }
                },
                lastPlayer,
                getBlock());
        } catch (EvaluationException e) {
            getEvaluator().addError(new LangHelpers.UnlocalizedString(e.getMessage()));
        }
        return null;
    }
}
