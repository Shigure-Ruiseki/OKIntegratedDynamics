package ruiseki.integratedterminals.inventory.container;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.api.terminalstorage.crafting.CraftingJobStartException;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlan;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingOption;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.InventoryContainer;

/**
 * A container for previewing a crafting plan.
 *
 * @author rubensworks
 */
public abstract class ContainerTerminalStorageCraftingPlanBase<L> extends InventoryContainer {

    public static final String BUTTON_START = "start";
    private static final ExecutorService WORKER_POOL = Executors
        .newFixedThreadPool(GeneralConfig.craftingPlannerThreads);

    private final CraftingOptionGuiData craftingOptionGuiData;
    private final int craftingPlanNotifierId;
    private final int craftingPlanFlatNotifierId;
    private final World world;

    private boolean calculatedCraftingPlan;
    private ITerminalCraftingPlan craftingPlan;

    public ContainerTerminalStorageCraftingPlanBase(@Nullable ContainerType<?> type, InventoryPlayer playerInventory,
        CraftingOptionGuiData craftingOptionGuiData) {
        super(type, playerInventory, new SimpleInventory());

        this.craftingOptionGuiData = craftingOptionGuiData;
        this.craftingPlanNotifierId = getNextValueId();
        this.craftingPlanFlatNotifierId = getNextValueId();
        this.world = player.worldObj;

        putButtonAction(BUTTON_START, (buttonId, container) -> startCraftingJob());
    }

    public abstract Optional<INetwork> getNetwork();

    public World getWorld() {
        return world;
    }

    public CraftingOptionGuiData getCraftingOptionGuiData() {
        return craftingOptionGuiData;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        // Calculate crafting plan on server
        if (!player.worldObj.isRemote && !calculatedCraftingPlan) {
            this.calculatedCraftingPlan = true;
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
        HandlerWrappedTerminalCraftingOption craftingOptionWrapper = this.craftingOptionGuiData.getCraftingOption();
        getNetwork().ifPresent(network -> {
            if (GeneralConfig.craftingPlannerEnableMultithreading) {
                WORKER_POOL.execute(() -> { this.updateCraftingPlanJob(craftingOptionWrapper, network); });
            } else {
                this.updateCraftingPlanJob(craftingOptionWrapper, network);
            }
        });
    }

    protected void updateCraftingPlanJob(HandlerWrappedTerminalCraftingOption craftingOptionWrapper, INetwork network) {
        this.setCraftingPlan(
            craftingOptionWrapper.getHandler()
                .calculateCraftingPlan(
                    network,
                    this.craftingOptionGuiData.getChannel(),
                    craftingOptionWrapper.getCraftingOption(),
                    this.craftingOptionGuiData.getAmount()));
    }

    protected void setCraftingPlan(ITerminalCraftingPlan craftingPlan) {
        this.craftingPlan = craftingPlan;
        if (!ContainerTerminalCraftingJobsPlan.isPlanTooLarge(this.craftingPlan)) {
            setValue(
                this.craftingPlanNotifierId,
                this.craftingOptionGuiData.getCraftingOption()
                    .getHandler()
                    .serializeCraftingPlan(this.craftingPlan));
        }
        setValue(
            this.craftingPlanFlatNotifierId,
            this.craftingOptionGuiData.getCraftingOption()
                .getHandler()
                .serializeCraftingPlanFlat(this.craftingPlan.flatten()));
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    private void startCraftingJob() {
        if (!getWorld().isRemote) {
            // Start the crafting job
            if (craftingPlan != null) {
                getNetwork().ifPresent(network -> {
                    try {
                        craftingOptionGuiData.getCraftingOption()
                            .getHandler()
                            .startCraftingJob(
                                network,
                                craftingOptionGuiData.getChannel(),
                                craftingPlan,
                                (EntityPlayerMP) player);

                        // Re-open terminal gui
                        craftingOptionGuiData.getLocation()
                            .openContainerFromServer(craftingOptionGuiData, getWorld(), (EntityPlayerMP) player);
                    } catch (CraftingJobStartException e) {
                        // If the job could not be started, display the error in the plan
                        craftingPlan.setError(e.getUnlocalizedError());
                        this.setCraftingPlan(craftingPlan);
                    }
                });
            }
        }
    }

}
