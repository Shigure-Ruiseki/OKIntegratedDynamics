package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.client.gui.container.GuiTerminalStorageCraftingOptionAmount;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

/**
 * Config for {@link ContainerTerminalStorageCraftingOptionAmountPart}.
 *
 * @author rubensworks
 */
public class ContainerTerminalStorageCraftingOptionAmountPartConfig
    extends GuiConfig<ContainerTerminalStorageCraftingOptionAmountPart> {

    /**
     * The unique instance.
     */
    public static ContainerTerminalStorageCraftingOptionAmountPartConfig _instance;

    public ContainerTerminalStorageCraftingOptionAmountPartConfig() {
        super(
            IntegratedTerminals._instance,
            true,
            "part_terminal_storage_crafting_option_amount_part",
            null,
            eConfig -> new ContainerType<>((i, inventoryPlayer, extendedBuffer) -> {
                try {
                    return new ContainerTerminalStorageCraftingOptionAmountPart(inventoryPlayer, extendedBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerTerminalStorageCraftingOptionAmountPart>> GuiScreens.ScreenConstructor<ContainerTerminalStorageCraftingOptionAmountPart, U> getScreenFactory() {
        return new ScreenFactorySafe<>(
            new GuiScreens.ScreenConstructor<ContainerTerminalStorageCraftingOptionAmountPart, GuiTerminalStorageCraftingOptionAmount<PartPos, ContainerTerminalStorageCraftingOptionAmountPart>>() {

                @Override
                public GuiTerminalStorageCraftingOptionAmount<PartPos, ContainerTerminalStorageCraftingOptionAmountPart> create(
                    ContainerTerminalStorageCraftingOptionAmountPart container, InventoryPlayer inventoryPlayer) {
                    return new GuiTerminalStorageCraftingOptionAmount<>(container);
                }
            });
    }
}
