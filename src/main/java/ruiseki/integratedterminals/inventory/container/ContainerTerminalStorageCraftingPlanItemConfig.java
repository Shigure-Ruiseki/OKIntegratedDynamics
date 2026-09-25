package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

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
public class ContainerTerminalStorageCraftingPlanItemConfig
    extends GuiConfig<ContainerTerminalStorageCraftingPlanItem> {

    /**
     * The unique instance.
     */
    public static ContainerTerminalStorageCraftingPlanItemConfig _instance;

    public ContainerTerminalStorageCraftingPlanItemConfig() {
        super(
            IntegratedTerminals._instance,
            true,
            "part_terminal_storage_crafting_plan_item",
            null,
            eConfig -> new ContainerType<>((i, inventoryPlayer, extendedBuffer) -> {
                try {
                    return new ContainerTerminalStorageCraftingPlanItem(inventoryPlayer, extendedBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerTerminalStorageCraftingPlanItem>> GuiScreens.ScreenConstructor<ContainerTerminalStorageCraftingPlanItem, U> getScreenFactory() {
        return new ScreenFactorySafe<>(
            new GuiScreens.ScreenConstructor<ContainerTerminalStorageCraftingPlanItem, GuiTerminalStorageCraftingPlan<Integer, ContainerTerminalStorageCraftingPlanItem>>() {

                @Override
                public GuiTerminalStorageCraftingPlan<Integer, ContainerTerminalStorageCraftingPlanItem> create(
                    ContainerTerminalStorageCraftingPlanItem container, InventoryPlayer inventoryPlayer) {
                    return new GuiTerminalStorageCraftingPlan<>(container);
                }
            });
    }
}
