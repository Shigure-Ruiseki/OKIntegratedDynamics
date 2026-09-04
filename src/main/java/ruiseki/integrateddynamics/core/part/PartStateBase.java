package ruiseki.integrateddynamics.core.part;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.AttachCapabilitiesEventPart;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.core.evaluate.InventoryVariableEvaluator;
import ruiseki.integrateddynamics.core.part.aspect.property.AspectProperties;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.persist.IDirtyMarkListener;
import ruiseki.okcore.persist.nbt.NBTClassType;

/**
 * A default implementation of the {@link IPartState}.
 *
 * @author rubensworks
 */
public abstract class PartStateBase<P extends IPartType> implements IPartState<P>, IDirtyMarkListener {

    private boolean dirty = false;
    private boolean update = false;
    private boolean forceBlockUpdateRender = false;
    private int updateInterval = getDefaultUpdateInterval();
    private int priority = 0;
    private int channel = 0;
    private int maxOffset;
    private Vector3i targetOffset = new Vector3i(0, 0, 0);
    private ForgeDirection targetSide = null;
    private int id = -1;
    private Map<IAspect, IAspectProperties> aspectProperties = new IdentityHashMap<>();
    private boolean enabled = true;
    private final Map<String, NonNullList<ItemStack>> inventoriesNamed = Maps.newHashMap();
    private final PartStateOffsetHandler<P> offsetHandler = new PartStateOffsetHandler<>();

    private CapabilityDispatcher capabilities = null;
    private IdentityHashMap<Capability<?>, LazyOptional<?>> volatileCapabilities = new IdentityHashMap<>();

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("updateInterval", this.updateInterval);
        tag.setInteger("priority", this.priority);
        tag.setInteger("channel", this.channel);
        if (this.targetSide != null) {
            tag.setInteger("targetSide", this.targetSide.ordinal());
        }
        tag.setInteger("id", this.id);
        writeAspectProperties("aspectProperties", tag);
        tag.setBoolean("enabled", this.enabled);
        if (this.capabilities != null) {
            tag.setTag("OKCaps", this.capabilities.serializeNBT());
        }
        tag.setInteger("maxOffset", this.maxOffset);
        tag.setInteger("offsetX", this.targetOffset.x());
        tag.setInteger("offsetY", this.targetOffset.y());
        tag.setInteger("offsetZ", this.targetOffset.z());

        // Write inventoriesNamed
        NBTTagList namedInventoriesList = new NBTTagList();
        for (Map.Entry<String, NonNullList<ItemStack>> entry : this.inventoriesNamed.entrySet()) {
            NBTTagCompound listEntry = new NBTTagCompound();
            listEntry.setString("tabName", entry.getKey());
            listEntry.setInteger(
                "itemCount",
                entry.getValue()
                    .size());

            ItemHelpers.saveAllItems(listEntry, entry.getValue());
            namedInventoriesList.appendTag(listEntry);
        }
        tag.setTag("inventoriesNamed", namedInventoriesList);

        // Write offsetVariablesSlotMessages
        NBTTagCompound errorsTag = new NBTTagCompound();
        for (Int2ObjectMap.Entry<String> entry : this.offsetHandler.offsetVariablesSlotMessages.int2ObjectEntrySet()) {
            NBTClassType.writeNbt(String.class, String.valueOf(entry.getIntKey()), entry.getValue(), errorsTag);
        }
        tag.setTag("offsetVariablesSlotMessages", errorsTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.updateInterval = tag.getInteger("updateInterval");
        this.priority = tag.getInteger("priority");
        this.channel = tag.getInteger("channel");
        if (tag.hasKey("targetSide", Constants.NBT.TAG_INT)) {
            this.targetSide = ForgeDirection.VALID_DIRECTIONS[tag.getInteger("targetSide")];
        }
        this.id = tag.getInteger("id");
        this.aspectProperties.clear();
        readAspectProperties("aspectProperties", tag);
        this.enabled = tag.getBoolean("enabled");
        if (this.capabilities != null && tag.hasKey("OKCaps")) {
            this.capabilities.deserializeNBT(tag.getCompoundTag("OKCaps"));
        }
        this.maxOffset = tag.getInteger("maxOffset");
        this.targetOffset = new Vector3i(
            tag.getInteger("offsetX"),
            tag.getInteger("offsetY"),
            tag.getInteger("offsetZ"));

        // Read inventoriesNamed
        this.inventoriesNamed.clear();
        NBTTagList namedInventoriesList = tag.getTagList("inventoriesNamed", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < namedInventoriesList.tagCount(); i++) {
            NBTTagCompound listEntry = namedInventoriesList.getCompoundTagAt(i);
            String tabName = listEntry.getString("tabName");
            int itemCount = listEntry.getInteger("itemCount");

            NonNullList<ItemStack> list = NonNullList.withSize(itemCount, null);
            ItemHelpers.loadAllItems(listEntry, list);
            this.inventoriesNamed.put(tabName, list);
        }

        // Read offsetVariablesSlotMessages
        this.offsetHandler.offsetVariablesSlotMessages.clear();
        NBTTagCompound errorsTag = tag.getCompoundTag("offsetVariablesSlotMessages");
        for (String slot : errorsTag.func_150296_c()) {
            String unlocalizedString = NBTClassType.readNbt(String.class, slot, errorsTag);
            this.offsetHandler.offsetVariablesSlotMessages.put(Integer.parseInt(slot), unlocalizedString);
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
    public void setChannel(int channel) {
        this.channel = channel;
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public Vector3i getTargetOffset() {
        return targetOffset;
    }

    @Override
    public void setTargetOffset(Vector3i targetOffset) {
        this.targetOffset = targetOffset;
        this.markDirty();
    }

    @Override
    public void setTargetSideOverride(ForgeDirection targetSide) {
        this.targetSide = targetSide;
    }

    @Nullable
    @Override
    public ForgeDirection getTargetSideOverride() {
        return targetSide;
    }

    @Override
    public void markDirty() {
        this.dirty = true;
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
    public void forceBlockRenderUpdate() {
        this.forceBlockUpdateRender = true;
    }

    @Override
    public boolean isForceBlockRenderUpdateAndReset() {
        boolean wasForceBlockUpdateRender = this.forceBlockUpdateRender;
        this.forceBlockUpdateRender = false;
        return wasForceBlockUpdateRender;
    }

    @Override
    public void onDirty() {
        this.dirty = true;
        this.forceBlockRenderUpdate();
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

    public NonNullList<ItemStack> getInventoryNamed(String name) {
        return this.inventoriesNamed.get(name);
    }

    public void setInventoryNamed(String name, NonNullList<ItemStack> inventory) {
        this.inventoriesNamed.put(name, inventory);
        onDirty();
    }

    @Override
    public Map<String, NonNullList<ItemStack>> getInventoriesNamed() {
        return this.inventoriesNamed;
    }

    @Override
    public void clearInventoriesNamed() {
        this.inventoriesNamed.clear();
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
    public <T> LazyOptional<T> getCapability(Capability<T> capability, INetwork network, IPartNetwork partNetwork,
        PartTarget target) {
        LazyOptional<?> lazyOptional = volatileCapabilities.get(capability);
        if (lazyOptional != null) {
            return lazyOptional.cast();
        }
        return capabilities == null ? null : capabilities.getCapability(capability, null);
    }

    @Override
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

    protected int getDefaultUpdateInterval() {
        return GeneralConfig.defaultPartUpdateFreq;
    }

    @Override
    public void initializeOffsets() {
        this.offsetHandler.initializeVariableEvaluators(this.offsetHandler.getOffsetVariablesInventory(this));
    }

    @Override
    public void updateOffsetVariables(P partType, INetwork network, IPartNetwork partNetwork, PartTarget target) {
        this.offsetHandler.updateOffsetVariables(partType, this, network, partNetwork, target);
    }

    @Nullable
    @Override
    public String getOffsetVariableError(int slot) {
        return this.offsetHandler.getOffsetVariableError(slot);
    }

    @Override
    public boolean requiresOffsetUpdates() {
        return this.offsetHandler.offsetVariableEvaluators.stream()
            .anyMatch(InventoryVariableEvaluator::hasVariable);
    }

    @Override
    public void markOffsetVariablesChanged() {
        this.offsetHandler.markOffsetVariablesChanged();
    }

    @Override
    public int getMaxOffset() {
        return maxOffset;
    }

    @Override
    public void setMaxOffset(int maxOffset) {
        this.maxOffset = maxOffset;
        markDirty();
    }
}
