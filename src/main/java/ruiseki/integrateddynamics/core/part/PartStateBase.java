package ruiseki.integrateddynamics.core.part;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.Maps;

import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.AttachCapabilitiesEventPart;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.persist.IDirtyMarkListener;
import ruiseki.okcore.persist.nbt.INBTProvider;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.okcore.persist.nbt.NBTProviderComponent;

/**
 * A default implementation of the {@link IPartState} with auto-persistence
 * of fields annotated with {@link NBTPersist}.
 * 
 * @author rubensworks
 */
public abstract class PartStateBase<P extends IPartType> implements IPartState<P>, INBTProvider, IDirtyMarkListener {

    private boolean dirty = false;
    private boolean update = false;
    @Delegate
    private INBTProvider nbtProviderComponent = new NBTProviderComponent(this);
    @NBTPersist
    private int updateInterval = GeneralConfig.defaultPartUpdateFreq;
    @NBTPersist
    private int id = -1;
    @NBTPersist
    private Map<String, IAspectProperties> aspectProperties = Maps.newHashMap();
    @NBTPersist
    private boolean enabled = true;
    private CapabilityDispatcher capabilities = null;

    // Đã chuyển value sang LazyOptional<?>
    private Map<Capability<?>, LazyOptional<?>> volatileCapabilities = Maps.newHashMap();

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        writeGeneratedFieldsToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        readGeneratedFieldsFromNBT(tag);
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
        return aspectProperties.get(aspect.getUnlocalizedName());
    }

    @Override
    public void setAspectProperties(IAspect aspect, IAspectProperties properties) {
        aspectProperties.put(aspect.getUnlocalizedName(), properties);
        sendUpdate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
