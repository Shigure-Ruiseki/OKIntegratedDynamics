package ruiseki.integratedterminals.core.terminalstorage.location;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocation;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingOptionAmountItem;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingPlanItem;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemOpenPacket;
import ruiseki.okcore.helper.PlayerHelpers;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class TerminalStorageLocationItem implements ITerminalStorageLocation<Integer> {

    @Override
    public ResourceLocation getName() {
        return new ResourceLocation(Reference.MOD_ID, "item");
    }

    @Override
    public <T, M> void openContainerFromClient(CraftingOptionGuiData<T, M, Integer> craftingOptionGuiData) {
        Integer slot = craftingOptionGuiData.getLocationInstance();

        TerminalStorageIngredientItemOpenPacket.send(
            slot,
            new ContainerTerminalStorageBase.InitTabData(
                craftingOptionGuiData.getTabName(),
                craftingOptionGuiData.getChannel()));
    }

    @Override
    public <T, M> void openContainerFromServer(CraftingOptionGuiData<T, M, Integer> craftingOptionGuiData, World world,
        EntityPlayerMP player) {
        Integer slot = craftingOptionGuiData.getLocationInstance();

        TerminalStorageIngredientItemOpenPacket.openServer(
            world,
            slot,
            player,
            new ContainerTerminalStorageBase.InitTabData(
                craftingOptionGuiData.getTabName(),
                craftingOptionGuiData.getChannel()));
    }

    @Override
    public <T, M> void openContainerCraftingPlan(CraftingOptionGuiData<T, M, Integer> craftingOptionGuiData,
        World world, EntityPlayerMP player) {
        Integer location = craftingOptionGuiData.getLocationInstance();

        // Create temporary container provider
        IGuiConstructor containerProvider = new IGuiConstructor() {

            @Override
            public @NotNull ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                return new ContainerTerminalStorageCraftingPlanItem(playerInventory, location, craftingOptionGuiData);
            }
        };

        // Trigger gui opening
        PlayerHelpers.openGui(player, containerProvider, packetBuffer -> {
            packetBuffer.writeInt(location);
            try {
                craftingOptionGuiData.writeToPacketBuffer(packetBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <T, M> void openContainerCraftingOptionAmount(CraftingOptionGuiData<T, M, Integer> craftingOptionGuiData,
        World world, EntityPlayerMP player) {
        Integer location = craftingOptionGuiData.getLocationInstance();

        // Create temporary container provider
        IGuiConstructor containerProvider = new IGuiConstructor() {

            @Override
            public @NotNull ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                return new ContainerTerminalStorageCraftingOptionAmountItem(
                    playerInventory,
                    location,
                    craftingOptionGuiData);
            }
        };

        // Trigger gui opening
        PlayerHelpers.openGui(player, containerProvider, packetBuffer -> {
            packetBuffer.writeInt(location);
            try {
                craftingOptionGuiData.writeToPacketBuffer(packetBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void writeToPacketBuffer(ExtendedBuffer packetBuffer, Integer location) {
        packetBuffer.writeInt(location);
    }

    @Override
    public Integer readFromPacketBuffer(ExtendedBuffer packetBuffer) {
        return packetBuffer.readInt();
    }
}
