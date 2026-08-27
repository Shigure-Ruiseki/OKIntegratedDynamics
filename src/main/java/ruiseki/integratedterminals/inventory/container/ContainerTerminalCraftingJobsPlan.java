package ruiseki.integratedterminals.inventory.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlan;
import ruiseki.integratedterminals.core.client.gui.CraftingJobGuiData;
import ruiseki.integratedterminals.proxy.guiprovider.GuiProviders;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;

/**
 * A container for visualizing a live crafting plan.
 *
 * @author rubensworks
 */
public class ContainerTerminalCraftingJobsPlan extends ExtendedInventoryContainer {

    private final World world;
    private final PartTarget target;
    private final CraftingJobGuiData craftingJobGuiData;
    private final int craftingPlanNotifierId;

    private long lastUpdate;
    @Nullable
    private ITerminalCraftingPlan craftingPlan;

    /**
     * Make a new instance.
     *
     * @param target             The target.
     * @param player             The player.
     * @param partContainer      The part container.
     * @param partType           The part type.
     * @param craftingJobGuiData The job data.
     */
    public ContainerTerminalCraftingJobsPlan(final EntityPlayer player, PartTarget target, IPartContainer partContainer,
        IPartType partType, CraftingJobGuiData craftingJobGuiData) {
        super(player.inventory, GuiProviders.GUI_TERMINAL_STORAGE_CRAFTING_PLAN_PART);

        this.world = player.worldObj;
        this.target = target;
        this.craftingJobGuiData = craftingJobGuiData;

        this.craftingPlanNotifierId = getNextValueId();
    }

    public CraftingJobGuiData getCraftingJobGuiData() {
        return craftingJobGuiData;
    }

    public PartTarget getTarget() {
        return target;
    }

    @Nullable
    public ITerminalCraftingPlan getCraftingPlan() {
        return craftingPlan;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        // Calculate crafting plan on server
        if (!this.world.isRemote && this.lastUpdate < System.currentTimeMillis()) {
            this.lastUpdate = System.currentTimeMillis() + GeneralConfig.guiTerminalCraftingJobsUpdateFrequency;
            updateCraftingPlan();
        }
    }

    public int getCraftingPlanNotifierId() {
        return craftingPlanNotifierId;
    }

    protected void updateCraftingPlan() {
        INetwork network = NetworkHelpers.getNetwork(target.getCenter());
        this.craftingPlan = craftingJobGuiData.getHandler()
            .getCraftingJob(network, this.craftingJobGuiData.getChannel(), craftingJobGuiData.getCraftingJob());
        if (this.craftingPlan != null) {
            setValue(
                this.craftingPlanNotifierId,
                this.craftingJobGuiData.getHandler()
                    .serializeCraftingPlan(this.craftingPlan));
        } else {
            setValue(this.craftingPlanNotifierId, new NBTTagCompound());
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
        if (valueId == this.craftingPlanNotifierId) {
            try {
                this.craftingPlan = craftingJobGuiData.getHandler()
                    .deserializeCraftingPlan(value);
            } catch (IllegalArgumentException e) {
                this.craftingPlan = null;
            }
        }

        super.onUpdate(valueId, value);
    }
}
