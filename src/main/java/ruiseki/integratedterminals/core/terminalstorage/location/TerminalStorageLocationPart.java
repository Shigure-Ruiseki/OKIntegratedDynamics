package ruiseki.integratedterminals.core.terminalstorage.location;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocation;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingOptionAmountPart;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageCraftingPlanPart;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientPartOpenPacket;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;
import ruiseki.integratedterminals.part.TerminalPartTypes;
import ruiseki.okcore.helper.PlayerHelpers;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

public class TerminalStorageLocationPart implements ITerminalStorageLocation<PartPos> {

    @Override
    public ResourceLocation getName() {
        return new ResourceLocation(Reference.MOD_ID, "part");
    }

    @Override
    public <T, M> void openContainerFromClient(CraftingOptionGuiData<T, M, PartPos> craftingOptionGuiData) {
        PartPos partPos = craftingOptionGuiData.getLocationInstance();
        TerminalStorageIngredientPartOpenPacket.send(
            partPos.getPos()
                .getBlockPos(),
            partPos.getSide(),
            craftingOptionGuiData.getTabName(),
            craftingOptionGuiData.getChannel());
    }

    @Override
    public <T, M> void openContainerFromServer(CraftingOptionGuiData<T, M, PartPos> craftingOptionGuiData, World world,
        EntityPlayerMP player) {
        PartPos partPos = craftingOptionGuiData.getLocationInstance();
        TerminalStorageIngredientPartOpenPacket.openServer(
            world,
            partPos.getPos()
                .getBlockPos(),
            partPos.getSide(),
            player,
            craftingOptionGuiData.getTabName(),
            craftingOptionGuiData.getChannel());
    }

    @Override
    public <T, M> void openContainerCraftingPlan(CraftingOptionGuiData<T, M, PartPos> craftingOptionGuiData,
        World world, EntityPlayerMP player) {
        // Create temporary container provider
        IGuiConstructor containerProvider = new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                PartPos location = craftingOptionGuiData.getLocationInstance();
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers.getContainerPartConstructionData(
                    PartPos.of(
                        world,
                        location.getPos()
                            .getBlockPos(),
                        location.getSide()));
                return new ContainerTerminalStorageCraftingPlanPart(
                    playerInventory,
                    Optional.of(data.getRight()),
                    Optional.of(data.getLeft()),
                    (PartTypeTerminalStorage) data.getMiddle(),
                    craftingOptionGuiData);
            }
        };

        // Trigger gui opening
        PlayerHelpers.openGui(player, containerProvider, packetBuffer -> {
            packetBuffer.writeString(
                TerminalPartTypes.TERMINAL_STORAGE.getUniqueName()
                    .toString());
            try {
                craftingOptionGuiData.writeToPacketBuffer(packetBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <T, M> void openContainerCraftingOptionAmount(CraftingOptionGuiData<T, M, PartPos> craftingOptionGuiData,
        World world, EntityPlayerMP player) {
        // Create temporary container provider
        IGuiConstructor containerProvider = new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                PartPos location = craftingOptionGuiData.getLocationInstance();
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers.getContainerPartConstructionData(
                    PartPos.of(
                        world,
                        location.getPos()
                            .getBlockPos(),
                        location.getSide()));
                return new ContainerTerminalStorageCraftingOptionAmountPart(
                    playerInventory,
                    Optional.of(data.getRight()),
                    Optional.of(data.getLeft()),
                    (PartTypeTerminalStorage) data.getMiddle(),
                    craftingOptionGuiData);
            }
        };

        // Trigger gui opening
        PlayerHelpers.openGui(player, containerProvider, packetBuffer -> {
            packetBuffer.writeString(
                TerminalPartTypes.TERMINAL_STORAGE.getUniqueName()
                    .toString());
            try {
                craftingOptionGuiData.writeToPacketBuffer(packetBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void writeToPacketBuffer(ExtendedBuffer packetBuffer, PartPos location) {
        try {
            PacketCodec.getAction(PartPos.class)
                .encode(location, packetBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PartPos readFromPacketBuffer(ExtendedBuffer packetBuffer) {
        return (PartPos) PacketCodec.getAction(PartPos.class)
            .decode(packetBuffer);
    }
}
