package ruiseki.integratedterminals.inventory.container;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipart;
import ruiseki.integrateddynamics.core.part.PartStateEmpty;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlan;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalStorageTabIngredientCraftingHandler;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingPlan;
import ruiseki.integratedterminals.core.terminalstorage.crafting.TerminalStorageTabIngredientCraftingHandlers;
import ruiseki.integratedterminals.part.PartTypeTerminalCraftingJob;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * Container for the crafting jobs overview gui.
 *
 * @author rubensworks
 */
public class ContainerTerminalCraftingJobs
    extends ContainerMultipart<PartTypeTerminalCraftingJob, PartStateEmpty<PartTypeTerminalCraftingJob>> {

    private final LazyOptional<INetwork> network;
    private final int valueIdCraftingJobs;

    private long lastUpdate;
    private List<HandlerWrappedTerminalCraftingPlan> craftingJobs;

    public ContainerTerminalCraftingJobs(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer) {
        this(
            playerInventory,
            PartHelpers.readPartTarget(packetBuffer),
            Optional.empty(),
            PartHelpers.readPart(packetBuffer));
    }

    public ContainerTerminalCraftingJobs(InventoryPlayer playerInventory, PartTarget target,
        Optional<IPartContainer> partContainer, PartTypeTerminalCraftingJob partType) {
        super(
            ContainerTerminalCraftingJobsConfig._instance.getInstance(),
            playerInventory,
            new SimpleInventory(),
            Optional.of(target),
            partContainer,
            partType);

        this.network = getTarget().map(t -> NetworkHelpers.getNetwork(t.getCenter()))
            .orElse(LazyOptional.empty());

        this.lastUpdate = 0;
        this.craftingJobs = Lists.newArrayList();
        this.valueIdCraftingJobs = getNextValueId();
    }

    public LazyOptional<INetwork> getNetwork() {
        return network;
    }

    public int getChannel() {
        return IPositionedAddonsNetwork.WILDCARD_CHANNEL;
    }

    public int getValueIdCraftingJobs() {
        return valueIdCraftingJobs;
    }

    public List<HandlerWrappedTerminalCraftingPlan> getCraftingJobs() {
        return craftingJobs;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (!this.getWorld().isRemote && this.lastUpdate < System.currentTimeMillis()) {
            getNetwork().ifPresent(network -> {
                this.lastUpdate = System.currentTimeMillis() + GeneralConfig.guiTerminalCraftingJobsUpdateFrequency;

                // Load crafting jobs
                int channel = getChannel();
                this.craftingJobs = Lists.newArrayList();
                for (ITerminalStorageTabIngredientCraftingHandler<?, ?> handler : TerminalStorageTabIngredientCraftingHandlers.REGISTRY
                    .getHandlers()) {
                    for (ITerminalCraftingPlan craftingJob : handler.getCraftingJobs(network, channel)) {
                        this.craftingJobs.add(new HandlerWrappedTerminalCraftingPlan(handler, craftingJob.flatten()));
                    }
                }

                // Send crafting jobs to client
                NBTTagList tagList = new NBTTagList();
                for (HandlerWrappedTerminalCraftingPlan craftingJob : this.craftingJobs) {
                    tagList.appendTag(HandlerWrappedTerminalCraftingPlan.serialize(craftingJob));
                }
                NBTTagCompound tag = new NBTTagCompound();
                tag.setTag("craftingJobs", tagList);
                setValue(this.valueIdCraftingJobs, tag);
            });
        }
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);

        if (valueId == this.valueIdCraftingJobs) {
            NBTTagList tagList = value.getTagList("craftingJobs", Constants.NBT.TAG_COMPOUND);
            this.craftingJobs = Lists.newArrayListWithExpectedSize(tagList.tagCount());
            for (int i = 0; i < tagList.tagCount(); i++) {
                this.craftingJobs.add(HandlerWrappedTerminalCraftingPlan.deserialize(tagList.getCompoundTagAt(i)));
            }
        }
    }
}
