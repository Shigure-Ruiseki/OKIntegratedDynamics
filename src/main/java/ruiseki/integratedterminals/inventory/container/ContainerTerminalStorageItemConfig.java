package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
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
public class ContainerTerminalStorageItemConfig extends GuiConfig<ContainerTerminalStorageItem> {

    /**
     * The unique instance.
     */
    public static ContainerTerminalStorageItemConfig _instance;

    public ContainerTerminalStorageItemConfig() {
        super(
            IntegratedTerminals._instance,
            true,
            "part_terminal_storage_item",
            null,
            eConfig -> new ContainerType<>((i, inventoryPlayer, extendedBuffer) -> {
                try {
                    return new ContainerTerminalStorageItem(inventoryPlayer, extendedBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerTerminalStorageItem>> GuiScreens.ScreenConstructor<ContainerTerminalStorageItem, U> getScreenFactory() {
        return new ScreenFactorySafe<>(
            new GuiScreens.ScreenConstructor<ContainerTerminalStorageItem, GuiTerminalStorage<Integer, ContainerTerminalStorageItem>>() {

                @Override
                public GuiTerminalStorage<Integer, ContainerTerminalStorageItem> create(
                    ContainerTerminalStorageItem container, InventoryPlayer inventoryPlayer) {
                    return new GuiTerminalStorage<>(container);
                }
            });
    }
}
