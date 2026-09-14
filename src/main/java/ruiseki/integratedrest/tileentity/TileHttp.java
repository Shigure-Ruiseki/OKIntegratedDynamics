package ruiseki.integratedrest.tileentity;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Sets;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.expression.VariableAdapter;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.tileentity.TileProxy;
import ruiseki.integratedrest.api.item.IHttpVariableFacade;
import ruiseki.integratedrest.evaluate.HttpVariableFacadeHandler;
import ruiseki.integratedrest.item.HttpVariableFacade;
import ruiseki.integratedrest.network.HttpNetworkElement;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.LangHelpers;

public class TileHttp extends TileProxy {

    public static final int INVENTORY_SIZE = 2;
    public static final int SLOT_WRITE_IN = 0;
    public static final int SLOT_WRITE_OUT = 1;

    private final HttpVariableAdapter variable;

    public TileHttp() {
        super(TileHttp.INVENTORY_SIZE);

        addSlotsToSide(ForgeDirection.UP, Sets.newHashSet(SLOT_WRITE_IN));
        addSlotsToSide(ForgeDirection.DOWN, Sets.newHashSet(SLOT_WRITE_IN));
        addSlotsToSide(ForgeDirection.SOUTH, Sets.newHashSet(SLOT_WRITE_IN));
        addSlotsToSide(ForgeDirection.WEST, Sets.newHashSet(SLOT_WRITE_IN));
        addSlotsToSide(ForgeDirection.EAST, Sets.newHashSet(SLOT_WRITE_IN));
        addSlotsToSide(ForgeDirection.UP, Sets.newHashSet(SLOT_WRITE_IN));
        addSlotsToSide(ForgeDirection.DOWN, Sets.newHashSet(SLOT_WRITE_OUT));

        this.variable = new HttpVariableAdapter(this, ValueTypes.CATEGORY_ANY, ValueTypeBoolean.ValueBoolean.of(false));
    }

    @Override
    protected void registerCapabilityResolvers() {
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver
                .create(NetworkElementProviderConfig.CAPABILITY, () -> new NetworkElementProviderSingleton() {

                    @Override
                    public INetworkElement createNetworkElement(World world, BlockPos blockPos) {
                        return new HttpNetworkElement(DimPos.of(world, blockPos));
                    }
                }));
    }

    @Override
    public ItemStack writeProxyInfo(boolean generateId, ItemStack itemStack, final int proxyId) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(
            generateId,
            itemStack,
            HttpVariableFacadeHandler.getInstance(),
            new IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IHttpVariableFacade>() {

                @Override
                public IHttpVariableFacade create(boolean generateId) {
                    return new HttpVariableFacade(generateId, proxyId);
                }

                @Override
                public IHttpVariableFacade create(int id) {
                    return new HttpVariableFacade(id, proxyId);
                }
            },
            lastPlayer,
            getBlock());
    }

    @Override
    public IVariable<?> getVariable(IPartNetwork network) {
        return variable;
    }

    @Override
    public int getSlotRead() {
        return -1;
    }

    @Override
    protected int getSlotWriteIn() {
        return SLOT_WRITE_IN;
    }

    @Override
    protected int getSlotWriteOut() {
        return SLOT_WRITE_OUT;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.variable
            .setValueTypeRaw(ValueTypes.REGISTRY.getValueType(new ResourceLocation(tag.getString("valueType"))));
        if (tag.hasKey("value", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound valueTag = tag.getCompoundTag("value");
            setValue(ValueHelpers.deserialize(valueTag));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
    }

    public IValueType<IValue> getValueType() {
        return this.variable.getValueTypeRaw();
    }

    public void setValueType(IValueType valueType) {
        this.variable.setValueTypeRaw(valueType);
        if (!valueType.isCategory()) {
            setValue(valueType.getDefault());
        } else {
            setValue(ValueTypeBoolean.ValueBoolean.of(false));
        }
        sendUpdate();
    }

    public void setValue(IValue value) {
        this.variable.invalidate();
        this.variable.setValueRaw(value);
        sendUpdate();
    }

    @Override
    public boolean hasVariable() {
        return this.variable.getValueRaw() != null;
    }

    @Override
    protected void updateReadVariable(boolean sendVariablesUpdateEvent) {
        // Do nothing
    }

    public static class HttpVariableAdapter extends VariableAdapter {

        private final TileHttp tile;
        private IValueType valueType;
        private IValue value;

        public HttpVariableAdapter(TileHttp tile, IValueType valueType, IValue value) {
            this.tile = tile;
            this.valueType = valueType;
            this.value = value;
        }

        @Nullable
        public IValue getValueRaw() {
            return this.value;
        }

        public IValueType getValueTypeRaw() {
            return valueType;
        }

        public void setValueRaw(IValue value) {
            this.value = value;
        }

        public void setValueTypeRaw(IValueType valueType) {
            this.valueType = valueType;
        }

        @Override
        public IValueType getType() {
            return this.valueType;
        }

        @Override
        public IValue getValue() throws EvaluationException {
            if (value == null) {
                throw new EvaluationException(
                    LangHelpers.localize("http.integratedrest.error.http_invalid", tile.getProxyId()));
            }
            return value;
        }
    }
}
