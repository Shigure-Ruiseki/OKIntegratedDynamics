package ruiseki.integrateddynamics.tileentity;

import java.util.Queue;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import lombok.Getter;
import lombok.Setter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IDelayVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.core.evaluate.DelayVariableFacadeHandler;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.item.DelayVariableFacade;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.persist.nbt.NBTPersist;

/**
 * A part entity for the variable delay.
 *
 * @author rubensworks
 */
public class TileDelay extends TileProxy {

    @NBTPersist
    @Getter
    private int capacity = 5;

    // Initialize with default capacity to prevent NPE on placement/first tick
    protected Queue<IValue> values = Queues.newArrayBlockingQueue(this.capacity);

    @NBTPersist
    @Getter
    @Setter
    private int updateInterval = 1;
    private ValueTypeList.ValueList list = ValueTypes.LIST.getDefault();
    private final IVariable<?> variable;

    @Setter
    private EntityPlayer lastPlayer = null;

    public TileDelay() {
        this.variable = new IVariable<ValueTypeList.ValueList>() {

            @Override
            public ValueTypeList getType() {
                return ValueTypes.LIST;
            }

            @Override
            public ValueTypeList.ValueList getValue() throws EvaluationException {
                return list;
            }
        };
    }

    @Override
    public ItemStack writeProxyInfo(boolean generateId, ItemStack itemStack, final int proxyId) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(
            generateId,
            itemStack,
            DelayVariableFacadeHandler.getInstance(),
            new IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IDelayVariableFacade>() {

                @Override
                public IDelayVariableFacade create(boolean generateId) {
                    return new DelayVariableFacade(generateId, proxyId);
                }

                @Override
                public IDelayVariableFacade create(int id) {
                    return new DelayVariableFacade(id, proxyId);
                }
            },
            lastPlayer,
            getBlock());
    }

    @Override
    public IVariable<?> getVariable(IPartNetwork network) {
        return variable;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;

        // Preserve existing elements when capacity changes if queue exists
        Queue<IValue> newValues = Queues.newArrayBlockingQueue(this.capacity);
        if (this.values != null) {
            while (!this.values.isEmpty() && newValues.size() < this.capacity) {
                newValues.add(this.values.poll());
            }
        }
        this.values = newValues;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagList valueList = new NBTTagList();
        if (values != null) {
            for (IValue value : values) {
                valueList.appendTag(ValueHelpers.serialize(value));
            }
        }
        tag.setTag("values", valueList);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        values = Queues.newArrayBlockingQueue(this.capacity);

        NBTTagList valueList = tag.getTagList("values", MinecraftHelpers.NBTTag_Types.NBTTagCompound.ordinal());
        for (int i = 0; i < valueList.tagCount(); i++) {
            IValue value = ValueHelpers.deserialize(valueList.getCompoundTagAt(i));
            if (value != null) {
                this.values.add(value);
            }
        }
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        if (!getWorldObj().isRemote && updateInterval > 0 && getWorldObj().getTotalWorldTime() % updateInterval == 0) {
            // Guard against null queue
            if (values == null) {
                values = Queues.newArrayBlockingQueue(this.capacity);
            }

            // Remove oldest elements from the queue until we have room for a new one.
            while (values.size() >= this.capacity) {
                values.poll();
            }

            // Add new value to the queue
            IVariable<?> variable = super.getVariable(NetworkHelpers.getPartNetwork(getNetwork()));
            IValue value = null;
            if (variable != null) {
                try {
                    value = variable.getValue();
                } catch (EvaluationException e) {
                    addError(new LangHelpers.UnlocalizedString(e.toString()));
                }
                if (value != null) {
                    values.add(value);

                    // Update variable with as value the materialized queue list
                    this.list = ValueTypeList.ValueList.ofList(value.getType(), Lists.newArrayList(values));
                }
            } else {
                values.clear();
                this.list = ValueTypes.LIST.getDefault();
            }
        }
    }
}
