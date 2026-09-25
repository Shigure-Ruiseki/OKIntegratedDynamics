package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipart;
import ruiseki.integrateddynamics.core.part.PartStateEmpty;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlan;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlanFlat;
import ruiseki.integratedterminals.core.client.gui.CraftingJobGuiData;
import ruiseki.integratedterminals.part.PartTypeTerminalCraftingJob;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * A container for visualizing a live crafting plan.
 *
 * @author rubensworks
 */
public class ContainerTerminalCraftingJobsPlan
    extends ContainerMultipart<PartTypeTerminalCraftingJob, PartStateEmpty<PartTypeTerminalCraftingJob>> {

    private final CraftingJobGuiData craftingJobGuiData;
    private final int craftingPlanNotifierId;
    private final int craftingPlanFlatNotifierId;

    private long lastUpdate;
    @Nullable
    private Optional<ITerminalCraftingPlan> craftingPlan;
    private Optional<ITerminalCraftingPlanFlat> craftingPlanFlat;

    public ContainerTerminalCraftingJobsPlan(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer)
        throws IOException {
        this(
            playerInventory,
            PartHelpers.readPartTarget(packetBuffer),
            Optional.empty(),
            PartHelpers.readPart(packetBuffer),
            CraftingJobGuiData.readFromPacketBuffer(packetBuffer));
    }

    public ContainerTerminalCraftingJobsPlan(InventoryPlayer playerInventory, PartTarget target,
        Optional<IPartContainer> partContainer, PartTypeTerminalCraftingJob partType,
        CraftingJobGuiData craftingJobGuiData) {
        super(
            ContainerTerminalCraftingJobsPlanConfig._instance.getInstance(),
            playerInventory,
            new SimpleInventory(),
            Optional.of(target),
            partContainer,
            partType);

        this.craftingJobGuiData = craftingJobGuiData;

        this.craftingPlanNotifierId = getNextValueId();
        this.craftingPlanFlatNotifierId = getNextValueId();
    }

    public CraftingJobGuiData getCraftingJobGuiData() {
        return craftingJobGuiData;
    }

    public Optional<ITerminalCraftingPlan> getCraftingPlan() {
        return craftingPlan;
    }

    public Optional<ITerminalCraftingPlanFlat> getCraftingPlanFlat() {
        return craftingPlanFlat;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        // Calculate crafting plan on server
        if (!this.getWorld().isRemote && this.lastUpdate < System.currentTimeMillis()) {
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
        getTarget().ifPresent(target -> {
            INetwork network = NetworkHelpers.getNetworkChecked(target.getCenter());
            this.craftingPlan = Optional.ofNullable(
                craftingJobGuiData.getHandler()
                    .getCraftingJob(
                        network,
                        this.craftingJobGuiData.getChannel(),
                        craftingJobGuiData.getCraftingJob()));
            if (this.craftingPlan.isPresent()) {
                ITerminalCraftingPlan plan = this.craftingPlan.get();
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
        });
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
                this.craftingPlan = Optional.of(
                    craftingJobGuiData.getHandler()
                        .deserializeCraftingPlan(value));
            } catch (IllegalArgumentException e) {
                this.craftingPlan = Optional.empty();
            }
        } else if (valueId == this.craftingPlanFlatNotifierId) {
            try {
                this.craftingPlanFlat = Optional.of(
                    craftingJobGuiData.getHandler()
                        .deserializeCraftingPlanFlat(value));
            } catch (IllegalArgumentException e) {
                this.craftingPlanFlat = Optional.empty();
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
