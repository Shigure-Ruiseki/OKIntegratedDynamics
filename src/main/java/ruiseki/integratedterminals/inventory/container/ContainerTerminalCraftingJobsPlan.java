package ruiseki.integratedterminals.inventory.container;

import java.util.List;

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
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlanFlat;
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
    private final int craftingPlanFlatNotifierId;

    private long lastUpdate;
    @Nullable
    private ITerminalCraftingPlan craftingPlan;
    private ITerminalCraftingPlanFlat craftingPlanFlat;

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
        this.craftingPlanFlatNotifierId = getNextValueId();
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

    public ITerminalCraftingPlanFlat getCraftingPlanFlat() {
        return craftingPlanFlat;
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

    public int getCraftingPlanFlatNotifierId() {
        return craftingPlanFlatNotifierId;
    }

    protected void updateCraftingPlan() {
        INetwork network = NetworkHelpers.getNetwork(target.getCenter())
            .getOrNull();
        this.craftingPlan = craftingJobGuiData.getHandler()
            .getCraftingJob(network, this.craftingJobGuiData.getChannel(), craftingJobGuiData.getCraftingJob());
        if (this.craftingPlan != null) {
            ITerminalCraftingPlan plan = this.craftingPlan;
            if (!ContainerTerminalCraftingJobsPlan.isPlanTooLarge(plan)) {
                setValue(
                    this.craftingPlanNotifierId,
                    this.craftingJobGuiData.getHandler()
                        .serializeCraftingPlan(plan));
            }
            setValue(
                this.craftingPlanFlatNotifierId,
                this.craftingJobGuiData.getHandler()
                    .serializeCraftingPlanFlat(plan.flatten()));
        } else {
            setValue(this.craftingPlanNotifierId, new NBTTagCompound());
            setValue(this.craftingPlanFlatNotifierId, new NBTTagCompound());
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
        } else if (valueId == this.craftingPlanFlatNotifierId) {
            try {
                this.craftingPlanFlat = craftingJobGuiData.getHandler()
                    .deserializeCraftingPlanFlat(value);
            } catch (IllegalArgumentException e) {
                this.craftingPlanFlat = null;
            }
        }

        super.onUpdate(valueId, value);
    }

    public static boolean isPlanTooLarge(ITerminalCraftingPlan craftingPlan) {
        return getPlanSize(craftingPlan) > GeneralConfig.terminalStorageMaxTreePlanSize;
    }

    public static int getPlanSize(ITerminalCraftingPlan craftingPlan) {
        List<ITerminalCraftingPlan<?>> deps = craftingPlan.getDependencies();
        if (deps.isEmpty()) {
            return 1;
        } else {
            return deps.stream()
                .mapToInt(ContainerTerminalCraftingJobsPlan::getPlanSize)
                .sum();
        }
    }
}
