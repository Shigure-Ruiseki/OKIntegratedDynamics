package ruiseki.integratedterminals.inventory.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlan;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalStorageTabIngredientCraftingHandler;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingPlan;
import ruiseki.integratedterminals.core.terminalstorage.crafting.TerminalStorageTabIngredientCraftingHandlers;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;

/**
 * Container for the crafting jobs overview gui.
 *
 * @author rubensworks
 */
public class ContainerTerminalCraftingJobs extends ExtendedInventoryContainer {

    private final World world;
    private final PartTarget target;
    private final IPartContainer partContainer;
    private final IPartType partType;
    private final INetwork network;
    private final int valueIdCraftingJobs;

    private long lastUpdate;
    private List<HandlerWrappedTerminalCraftingPlan> craftingJobs;

    /**
     * Make a new instance.
     *
     * @param target        The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The part type.
     */
    public ContainerTerminalCraftingJobs(final EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType) {
        super(player.inventory, (IGuiContainerProvider) partType);

        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
        this.world = player.worldObj;
        this.network = NetworkHelpers.getNetwork(target.getCenter())
            .getOrNull();

        this.lastUpdate = 0;
        this.craftingJobs = Lists.newArrayList();
        this.valueIdCraftingJobs = getNextValueId();
    }

    public PartTarget getTarget() {
        return target;
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

        if (!this.world.isRemote && this.lastUpdate < System.currentTimeMillis()) {
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
