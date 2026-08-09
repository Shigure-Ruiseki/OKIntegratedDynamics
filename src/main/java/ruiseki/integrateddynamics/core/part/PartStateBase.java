package ruiseki.integrateddynamics.core.part;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.AttachCapabilitiesEventPart;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.core.part.aspect.property.AspectProperties;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * A default implementation of the {@link IPartState}.
 *
 * @author rubensworks
 */
public abstract class PartStateBase<P extends IPartType> implements IPartState<P>, IDirtyMarkListener {

    private boolean dirty = false;
    private boolean update = false;
    private int updateInterval = GeneralConfig.defaultPartUpdateFreq;
    private int priority = 0;
    private int id = -1;
    private Map<IAspect, IAspectProperties> aspectProperties = new IdentityHashMap<>();
    private boolean enabled = true;
    private CapabilityDispatcher capabilities = null;
    private IdentityHashMap<Capability<?>, LazyOptional<?>> volatileCapabilities = new IdentityHashMap<>();

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("updateInterval", this.updateInterval);
        tag.setInteger("priority", this.priority);
        tag.setInteger("id", this.id);
        writeAspectProperties("aspectProperties", tag);
        tag.setBoolean("enabled", this.enabled);
        if (this.capabilities != null) {
            tag.setTag("OKCaps", this.capabilities.serializeNBT());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.updateInterval = tag.getInteger("updateInterval");
        this.priority = tag.getInteger("priority");
        this.id = tag.getInteger("id");
        this.aspectProperties.clear();
        readAspectProperties("aspectProperties", tag);
        this.enabled = tag.getBoolean("enabled");
        if (this.capabilities != null && tag.hasKey("OKCaps")) {
            this.capabilities.deserializeNBT(tag.getCompoundTag("OKCaps"));
        }
    }

    protected void writeAspectProperties(String name, NBTTagCompound tag) {
        NBTTagCompound mapTag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (Map.Entry<IAspect, IAspectProperties> entry : aspectProperties.entrySet()) {
            NBTTagCompound entryTag = new NBTTagCompound();
            entryTag.setString(
                "key",
                entry.getKey()
                    .getUnlocalizedName());
            if (entry.getValue() != null) {
                entryTag.setTag(
                    "value",
                    entry.getValue()
                        .serializeNBT());
            }
            list.appendTag(entryTag);
        }
        mapTag.setTag("map", list);
        tag.setTag(name, mapTag);
    }

    public void readAspectProperties(String name, NBTTagCompound tag) {
        NBTTagCompound mapTag = tag.getCompoundTag(name);
        NBTTagList list = mapTag.getTagList("map", MinecraftHelpers.NBTTag_Types.NBTTagCompound.ordinal());
        if (list.tagCount() > 0) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound entryTag = list.getCompoundTagAt(i);
                IAspect key = Aspects.REGISTRY.getAspect(entryTag.getString("key"));
                IAspectProperties value = null;
                if (entryTag.hasKey("value")) {
                    value = new AspectProperties();
                    value.deserializeNBT(entryTag.getCompoundTag("value"));
                }
                if (key != null && value != null) {
                    this.aspectProperties.put(key, value);
                }
            }
        }
    }

    @Override
    public void generateId() {
        this.id = IntegratedDynamics.globalCounters.getNext(IPartState.GLOBALCOUNTER_KEY);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean isDirtyAndReset() {
        boolean wasDirty = this.dirty;
        this.dirty = false;
        return wasDirty;
    }

    @Override
    public boolean isUpdateAndReset() {
        boolean wasUpdate = this.update;
        this.update = false;
        return wasUpdate;
    }

    @Override
    public void onDirty() {
        this.dirty = true;
    }

    /**
     * Enables a flag that tells the part container to send an NBT update to the client(s).
     */
    public void sendUpdate() {
        this.update = true;
    }

    @Override
    public IAspectProperties getAspectProperties(IAspect aspect) {
        return aspectProperties.get(aspect);
    }

    @Override
    public void setAspectProperties(IAspect aspect, IAspectProperties properties) {
        aspectProperties.put(aspect, properties);
        sendUpdate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean wasEnabled = this.enabled;
        this.enabled = enabled;
        if (this.enabled != wasEnabled) {
            sendUpdate();
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gathers the capabilities of this part state.
     * Don't call this unless you know what you're doing!
     */
    public void gatherCapabilities(P partType) {
        AttachCapabilitiesEventPart event = new AttachCapabilitiesEventPart(partType, this);
        MinecraftForge.EVENT_BUS.post(event);
        this.capabilities = !event.getCapabilities()
            .isEmpty() ? new CapabilityDispatcher(event.getCapabilities(), event.getListeners()) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(Capability<T> capability) {
        LazyOptional<?> lazyOptional = volatileCapabilities.get(capability);
        if (lazyOptional != null) {
            return (LazyOptional<T>) lazyOptional;
        }
        return capabilities == null ? LazyOptional.empty() : capabilities.getCapability(capability, null);
    }

    @Override
    public <T> void addVolatileCapability(Capability<T> capability, T value) {
        addVolatileCapability(capability, LazyOptional.of(() -> value));
    }

    public <T> void addVolatileCapability(Capability<T> capability, LazyOptional<T> lazyOptional) {
        volatileCapabilities.put(capability, lazyOptional);
    }

    @Override
    public void removeVolatileCapability(Capability<?> capability) {
        LazyOptional<?> removed = volatileCapabilities.remove(capability);
        if (removed != null) {
            removed.invalidate();
        }
    }

}
