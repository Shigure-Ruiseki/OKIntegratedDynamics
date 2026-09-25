package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalStorageCraftingOptionAmount;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

/**
 * Config for {@link ContainerTerminalStorageCraftingOptionAmountItem}.
 *
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingOptionAmountItemConfig
    extends GuiConfig<ContainerTerminalStorageCraftingOptionAmountItem> {

    /**
     * The unique instance.
     */
    public static ContainerTerminalStorageCraftingOptionAmountItemConfig _instance;

    public ContainerTerminalStorageCraftingOptionAmountItemConfig() {
        super(
            IntegratedTerminals._instance,
            true,
            "part_terminal_storage_crafting_option_amount_item",
            null,
            eConfig -> new ContainerType<>((i, inventoryPlayer, extendedBuffer) -> {
                try {
                    return new ContainerTerminalStorageCraftingOptionAmountItem(inventoryPlayer, extendedBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerTerminalStorageCraftingOptionAmountItem>> GuiScreens.ScreenConstructor<ContainerTerminalStorageCraftingOptionAmountItem, U> getScreenFactory() {
        return new ScreenFactorySafe<>(
            new GuiScreens.ScreenConstructor<ContainerTerminalStorageCraftingOptionAmountItem, GuiTerminalStorageCraftingOptionAmount<Integer, ContainerTerminalStorageCraftingOptionAmountItem>>() {

                @Override
                public GuiTerminalStorageCraftingOptionAmount<Integer, ContainerTerminalStorageCraftingOptionAmountItem> create(
                    ContainerTerminalStorageCraftingOptionAmountItem container, InventoryPlayer inventoryPlayer) {
                    return new GuiTerminalStorageCraftingOptionAmount<>(container);
                }
            });
    }
}
