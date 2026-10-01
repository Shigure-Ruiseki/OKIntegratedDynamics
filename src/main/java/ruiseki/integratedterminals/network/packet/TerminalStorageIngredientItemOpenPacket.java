package ruiseki.integratedterminals.network.packet;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageItem;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
import ruiseki.integratedterminals.item.ItemTerminalStoragePortable;
import ruiseki.okcore.helper.PlayerHelpers;
import ruiseki.okcore.inventory.IContainerConstructor;
import ruiseki.okcore.inventory.ItemLocation;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class TerminalStorageIngredientItemOpenPacket extends PacketCodec {

    @CodecField
    private ItemLocation itemLocation;
    @CodecField
    private ContainerTerminalStorageBase.InitTabData tabData;

    public TerminalStorageIngredientItemOpenPacket() {

    }

    public TerminalStorageIngredientItemOpenPacket(ItemLocation itemLocation,
        ContainerTerminalStorageBase.InitTabData tabData) {
        this.itemLocation = itemLocation;
        this.tabData = tabData;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        openServer(world, itemLocation, player, tabData);
    }

    public static void openServer(World world, ItemLocation itemLocation, EntityPlayerMP player,
        ContainerTerminalStorageBase.InitTabData tabData) {
        // Create common data
        TerminalStorageState terminalStorageState = ItemTerminalStoragePortable
            .getTerminalStorageState(itemLocation.getItemStack(player), player, itemLocation);

        // Create temporary container provider
        IContainerConstructor containerProvider = new IContainerConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int id, InventoryPlayer playerInventory,
                EntityPlayer player) {
                return new ContainerTerminalStorageItem(
                    id,
                    playerInventory,
                    itemLocation,
                    Optional.of(tabData),
                    terminalStorageState);
            }
        };

        // Trigger gui opening
        PlayerHelpers.openGui(player, containerProvider, packetBuffer -> {
            try {
                ItemLocation.writeToPacketBuffer(packetBuffer, itemLocation);
                packetBuffer.writeBoolean(true);
                tabData.writeToPacketBuffer(packetBuffer);
                terminalStorageState.writeToPacketBuffer(packetBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void send(ItemLocation itemLocation, ContainerTerminalStorageBase.InitTabData tabData) {
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientItemOpenPacket(itemLocation, tabData));
    }
}
