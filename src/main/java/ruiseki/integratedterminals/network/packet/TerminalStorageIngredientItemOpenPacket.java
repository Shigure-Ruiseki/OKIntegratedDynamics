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
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.helper.PlayerHelpers;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class TerminalStorageIngredientItemOpenPacket extends PacketCodec {

    @CodecField
    private int slotIndex;
    @CodecField
    private ContainerTerminalStorageBase.InitTabData tabData;

    public TerminalStorageIngredientItemOpenPacket() {

    }

    public TerminalStorageIngredientItemOpenPacket(int slotIndex, ContainerTerminalStorageBase.InitTabData tabData) {
        this.slotIndex = slotIndex;
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
        openServer(world, slotIndex, player, tabData);
    }

    public static void openServer(World world, int slotIndex, EntityPlayerMP player,
        ContainerTerminalStorageBase.InitTabData tabData) {
        // Create common data
        TerminalStorageState terminalStorageState = ItemTerminalStoragePortable
            .getTerminalStorageState(InventoryHelpers.getItemFromIndex(player, slotIndex), player, slotIndex);

        // Create temporary container provider
        IGuiConstructor containerProvider = new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                return new ContainerTerminalStorageItem(
                    playerInventory,
                    slotIndex,
                    Optional.of(tabData),
                    terminalStorageState);
            }
        };

        // Trigger gui opening
        PlayerHelpers.openGui(player, containerProvider, packetBuffer -> {
            try {
                packetBuffer.writeInt(slotIndex);

                packetBuffer.writeBoolean(true);
                tabData.writeToPacketBuffer(packetBuffer);
                terminalStorageState.writeToPacketBuffer(packetBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void send(int slotIndex, ContainerTerminalStorageBase.InitTabData tabData) {
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientItemOpenPacket(slotIndex, tabData));
    }
}
