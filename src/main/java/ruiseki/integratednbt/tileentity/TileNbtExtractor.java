package ruiseki.integratednbt.tileentity;

import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import ruiseki.integrateddynamics.capability.variablecontainer.VariableContainerConfig;
import ruiseki.integrateddynamics.capability.variablecontainer.VariableContainerDefault;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.tileentity.TileActiveVariableBase;
import ruiseki.integratednbt.evaluate.NbtExtractorOutputMode;
import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;
import ruiseki.integratednbt.network.NbtExtractorNetworkElement;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.Wrapper;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

public class TileNbtExtractor extends TileActiveVariableBase<NbtExtractorNetworkElement> {

    public static final int SRC_NBT_SLOT = 0;
    public static final int VAR_OUT_SLOT = 1;
    private VariableContainerDefault variableContainerCapability = new VariableContainerDefault();
    /**
     * A set of expanded paths in this extractor;
     * <p>
     * This is client-side only and it is not persisted.
     */
    private HashSet<SegmentedNbtPath> expandedPaths;
    /**
     * How much has the user scrolled in this extractor;
     * <p>
     * This is client-side only and it is not persisted.
     */
    private Wrapper<Integer> scrollTop = new Wrapper<>(0);
    /**
     * Whether should run refreshVariable on next tick
     */
    private boolean shouldRefreshVariable = false;
    /**
     * Whether should send update on next tick
     */
    private boolean shouldUpdateOutVariable = false;
    private SegmentedNbtPath extractionPath = new SegmentedNbtPath();
    private byte defaultNBTId = 1;
    private NbtExtractorOutputMode outputMode = NbtExtractorOutputMode.REFERENCE;
    private NBTBase lastEvaluatedNBT = null;
    // If null, then there is no frozen value available
    private Wrapper<NBTBase> frozenNBT = null;
    private boolean autoRefresh = true;
    /**
     * The item stack that yielded the current frozen NBT
     */
    private ItemStack frozenNBTItemStack = ItemHelpers.EMPTY;
    private EntityPlayer lastPlayer;

    public TileNbtExtractor(int inventorySize) {
        super(inventorySize, "");
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver
                .create(NetworkElementProviderConfig.CAPABILITY, () -> new NetworkElementProviderSingleton() {

                    @Override
                    public INetworkElement createNetworkElement(World world, BlockPos blockPos) {
                        return new NbtExtractorNetworkElement(DimPos.of(world, blockPos));
                    }
                }));
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(VariableContainerConfig.CAPABILITY, () -> variableContainerCapability));
    }

    public void setLastPlayer(EntityPlayer player) {
        this.lastPlayer = player;
    }

    @Override
    public int getSlotRead() {
        return SRC_NBT_SLOT;
    }

    public void setShouldRefreshVariable(boolean shouldRefreshVariable) {
        this.shouldRefreshVariable = shouldRefreshVariable;
    }

    public boolean isShouldRefreshVariable() {
        return shouldRefreshVariable;
    }

    public void setShouldUpdateOutVariable(boolean shouldUpdateOutVariable) {
        this.shouldUpdateOutVariable = shouldUpdateOutVariable;
    }

    public boolean isShouldUpdateOutVariable() {
        return shouldUpdateOutVariable;
    }

    public NbtExtractorOutputMode getOutputMode() {
        return this.outputMode;
    }

    public void setOutputMode(NbtExtractorOutputMode outputMode) {
        this.outputMode = outputMode;
        this.markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.worldObj == null) {
            return;
        }
        if (!this.worldObj.isRemote) {
            this.shouldUpdateOutVariable = true;
            if (!this.autoRefresh && !ItemHelpers.areItemsEqual(
                this.getInventory()
                    .getStackInSlot(SRC_NBT_SLOT),
                this.frozenNBTItemStack)) {
                this.frozenNBTItemStack = this.getInventory()
                    .getStackInSlot(SRC_NBT_SLOT);
                this.frozenNBT = null;
            }
        }
    }

    @Override
    protected void updateReadVariable(boolean sendVariablesUpdateEvent) {
        super.updateReadVariable(sendVariablesUpdateEvent);
        this.variableContainerCapability.refreshVariables(getNetwork(), getInventory(), sendVariablesUpdateEvent);
    }

    public void setDefaultNBTId(byte defaultNBTId) {
        if (defaultNBTId < 1 || defaultNBTId > 12) {
            this.defaultNBTId = 1;
        } else {
            this.defaultNBTId = defaultNBTId;
        }
        this.markDirty();
    }

    public SegmentedNbtPath getExtractionPath() {
        return this.extractionPath;
    }

    public void setExtractionPath(SegmentedNbtPath extractionPath) {
        this.extractionPath = extractionPath;
        this.markDirty();
    }

    public boolean isAutoRefresh() {
        return this.autoRefresh;
    }

    public void updateAutoRefresh(boolean autoRefresh) {
        if (this.autoRefresh == autoRefresh) {
            return;
        }
        this.autoRefresh = autoRefresh;
        if (!autoRefresh) {
            this.frozenNBT = null;
            this.frozenNBTItemStack = ItemHelpers.EMPTY;
        }
        this.markDirty();
    }

    public void updateLastEvaluatedNBT(NBTBase lastEvaluatedNBT) {
        this.lastEvaluatedNBT = lastEvaluatedNBT;
        if (!this.autoRefresh && this.frozenNBT == null) {
            this.frozenNBT = new Wrapper<>(this.lastEvaluatedNBT);
            this.frozenNBTItemStack = this.getInventory()
                .getStackInSlot(SRC_NBT_SLOT)
                .copy();
        }
    }

    public HashSet<SegmentedNbtPath> getExpandedPaths() {
        return this.expandedPaths;
    }

    public Wrapper<Integer> getScrollTop() {
        return this.scrollTop;
    }

    public IVariable<?> getSrcNBTVariable() {
        INetwork network = this.getNetwork();
        IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network)
            .getOrNull();
        if (partNetwork == null) {
            return null;
        }
        return getEvaluator().getVariable(network, partNetwork);
    }

    public LangHelpers.UnlocalizedString getFirstErrorMessage() {
        List<LangHelpers.UnlocalizedString> errors = getEvaluator().getErrors();
        if (errors.isEmpty()) {
            return null;
        } else {
            return errors.get(0);
        }
    }

    public Wrapper<NBTBase> getFrozenValue() {
        if (this.autoRefresh) {
            return null;
        } else {
            return this.frozenNBT;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!MinecraftHelpers.isClientSide()) {
            this.shouldRefreshVariable = true;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("path", this.extractionPath.toNBT());
        tag.setByte("defaultNBTId", this.defaultNBTId);
        tag.setByte("outputMode", (byte) this.outputMode.ordinal());
        tag.setBoolean("isAutoRefresh", this.autoRefresh);
        if (!this.autoRefresh) {
            if (this.frozenNBT != null) {
                NBTTagCompound compound = new NBTTagCompound();
                if (this.frozenNBT.get() != null) {
                    compound.setTag("value", this.frozenNBT.get());
                }
                tag.setTag("frozenNBT", compound);
            }
            if (this.frozenNBTItemStack != null) {
                tag.setTag("frozenNBTItemStack", this.frozenNBTItemStack.writeToNBT(new NBTTagCompound()));
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("path")) {
            this.extractionPath = SegmentedNbtPath.fromNBT(tag.getTag("path"))
                .orElse(new SegmentedNbtPath());
        }
        if (tag.hasKey("defaultNBTId")) {
            this.defaultNBTId = tag.getByte("defaultNBTId");
        }
        if (tag.hasKey("outputMode")) {
            this.outputMode = NbtExtractorOutputMode.values()[tag.getByte("outputMode")];
        }
        if (tag.hasKey("isAutoRefresh")) {
            this.autoRefresh = tag.getBoolean("isAutoRefresh");
            if (!this.autoRefresh) {
                if (tag.hasKey("frozenNBT")) {
                    NBTTagCompound frozenTag = tag.getCompoundTag("frozenNBT");
                    if (frozenTag.hasKey("value")) {
                        // Đọc linh hoạt NBTBase bất kỳ bằng getTag()
                        this.frozenNBT = new Wrapper<>(frozenTag.getTag("value"));
                    } else {
                        this.frozenNBT = new Wrapper<>(null);
                    }
                }
                if (tag.hasKey("frozenNBTItemStack")) {
                    this.frozenNBTItemStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("frozenNBTItemStack"));
                }
            }
        }
        this.shouldRefreshVariable = true;
    }

    public void afterNetworkReAlive() {
        this.shouldRefreshVariable = true;
    }

    public void refreshVariables(boolean sendVariablesUpdateEvent) {
        updateReadVariable(sendVariablesUpdateEvent);
    }

    public void updateOutVariable() {
        if (!ItemHelpers.isEmpty(
            this.getInventory()
                .getStackInSlot(VAR_OUT_SLOT))) {
            ItemStack result = this.outputMode.writeItemStack(() -> {
                this.updateReadVariable(true);
                return getEvaluator().getVariableFacade();
            },
                this.getInventory()
                    .getStackInSlot(VAR_OUT_SLOT),
                ((!this.autoRefresh && this.frozenNBT != null) ? this.frozenNBT.get() : this.lastEvaluatedNBT),
                this.extractionPath,
                this.defaultNBTId,
                this.getWorldObj(),
                this.getBlock(),
                this.lastPlayer);
            if (result != null) {
                this.getInventory()
                    .setInventorySlotContents(VAR_OUT_SLOT, result);
            }
        }
    }
}
