package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalStorageCraftingPlan;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

/**
 * Config for {@link ContainerTerminalStorageItem}.
 *
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingPlanPartConfig
    extends GuiConfig<ContainerTerminalStorageCraftingPlanPart> {

    /**
     * The unique instance.
     */
    public static ContainerTerminalStorageCraftingPlanPartConfig _instance;

    public ContainerTerminalStorageCraftingPlanPartConfig() {
        super(
            IntegratedTerminals._instance,
            true,
            "part_terminal_storage_crafting_plan_part",
            null,
            eConfig -> new ContainerType<>((i, inventoryPlayer, extendedBuffer) -> {
                try {
                    return new ContainerTerminalStorageCraftingPlanPart(inventoryPlayer, extendedBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerTerminalStorageCraftingPlanPart>> GuiScreens.ScreenConstructor<ContainerTerminalStorageCraftingPlanPart, U> getScreenFactory() {
        return new ScreenFactorySafe<>(
            new GuiScreens.ScreenConstructor<ContainerTerminalStorageCraftingPlanPart, GuiTerminalStorageCraftingPlan<PartPos, ContainerTerminalStorageCraftingPlanPart>>() {

                @Override
                public GuiTerminalStorageCraftingPlan<PartPos, ContainerTerminalStorageCraftingPlanPart> create(
                    ContainerTerminalStorageCraftingPlanPart container, InventoryPlayer inventoryPlayer) {
                    return new GuiTerminalStorageCraftingPlan<>(container);
                }
            });
    }
}
