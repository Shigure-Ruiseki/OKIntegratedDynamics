package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.ScreenFactorySafe;
import ruiseki.okcore.config.extendedconfig.GuiConfig;

/**
 * Config for {@link ContainerTerminalStoragePart}.
 *
 * @author rubensworks
 */
public class ContainerTerminalStoragePartConfig extends GuiConfig<ContainerTerminalStoragePart> {

    /**
     * The unique instance.
     */
    public static ContainerTerminalStoragePartConfig _instance;

    public ContainerTerminalStoragePartConfig() {
        super(
            IntegratedTerminals._instance,
            true,
            "part_terminal_storage_part",
            null,
            eConfig -> new ContainerType<>((i, inventoryPlayer, extendedBuffer) -> {
                try {
                    return new ContainerTerminalStoragePart(inventoryPlayer, extendedBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
    }

    @Override
    public <U extends GuiScreen & IContainerAccess<ContainerTerminalStoragePart>> GuiScreens.ScreenConstructor<ContainerTerminalStoragePart, U> getScreenFactory() {
        return new ScreenFactorySafe<>(
            new GuiScreens.ScreenConstructor<ContainerTerminalStoragePart, GuiTerminalStorage<PartPos, ContainerTerminalStoragePart>>() {

                @Override
                public GuiTerminalStorage<PartPos, ContainerTerminalStoragePart> create(
                    ContainerTerminalStoragePart container, InventoryPlayer inventoryPlayer) {
                    return new GuiTerminalStorage<>(container);
                }
            });
    }
}
