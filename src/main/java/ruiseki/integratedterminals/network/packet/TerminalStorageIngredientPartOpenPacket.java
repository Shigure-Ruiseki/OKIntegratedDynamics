package ruiseki.integratedterminals.network.packet;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStoragePart;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
import ruiseki.integratedterminals.part.PartTypeTerminalStorage;
import ruiseki.integratedterminals.part.TerminalPartTypes;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.PlayerHelpers;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for telling the server that the storage terminal gui should be opened on a specific tab.
 *
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientPartOpenPacket extends PacketCodec {

    @CodecField
    private BlockPos pos;
    @CodecField
    private ForgeDirection side;
    @CodecField
    private String tabName;
    @CodecField
    private int channel;

    public TerminalStorageIngredientPartOpenPacket() {

    }

    public TerminalStorageIngredientPartOpenPacket(BlockPos pos, ForgeDirection side, String tabName, int channel) {
        this.pos = pos;
        this.side = side;
        this.tabName = tabName;
        this.channel = channel;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        openServer(world, pos, side, player, tabName, channel);
    }

    public static void openServer(World world, BlockPos pos, ForgeDirection side, EntityPlayerMP player, String tabName,
        int channel) {
        // Create common data
        ContainerTerminalStorageBase.InitTabData initData = new ContainerTerminalStorageBase.InitTabData(
            tabName,
            channel);
        PartPos partPos = PartPos.of(world, pos, side);
        Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers.getContainerPartConstructionData(partPos);
        PartTypeTerminalStorage.State state = (PartTypeTerminalStorage.State) data.getLeft()
            .getPartState(
                data.getRight()
                    .getCenter()
                    .getSide());
        TerminalStorageState terminalStorageState = state.getPlayerStorageState(player);

        // Create temporary container provider
        IGuiConstructor containerProvider = new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                return new ContainerTerminalStoragePart(
                    playerInventory,
                    data.getRight(),
                    (PartTypeTerminalStorage) data.getMiddle(),
                    Optional.of(initData),
                    terminalStorageState);
            }
        };

        // Trigger gui opening
        PlayerHelpers.openGui(player, containerProvider, packetBuffer -> {
            try {
                PacketCodec.getAction(PartPos.class)
                    .encode(partPos, packetBuffer);
                packetBuffer.writeString(
                    TerminalPartTypes.TERMINAL_STORAGE.getUniqueName()
                        .toString());

                packetBuffer.writeBoolean(true);
                initData.writeToPacketBuffer(packetBuffer);

                terminalStorageState.writeToPacketBuffer(packetBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void send(BlockPos pos, ForgeDirection side, String tabName, int channel) {
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new TerminalStorageIngredientPartOpenPacket(pos, side, tabName, channel));
    }

}
