package ruiseki.integratedterminals.core.terminalstorage.location;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocation;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.integratedterminals.network.packet.PacketSetCraftingDataPart;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientPartOpenPacket;
import ruiseki.integratedterminals.proxy.guiprovider.GuiProviders;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

/**
 * Terminal storage location implementation for PartPos.
 *
 * @author rubensworks
 */
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
        PartPos partPos = craftingOptionGuiData.getLocationInstance();
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.CRAFTING_OPTION, Pair.of(partPos.getSide(), craftingOptionGuiData));

        IntegratedTerminals._instance.getPacketHandler()
            .sendToPlayer(new PacketSetCraftingDataPart(partPos.getSide(), craftingOptionGuiData), player);

        BlockPos cPos = partPos.getPos()
            .getBlockPos();
        player.openGui(
            IntegratedTerminals._instance,
            GuiProviders.ID_GUI_TERMINAL_STORAGE_CRAFTNG_PLAN_PART,
            world,
            cPos.getX(),
            cPos.getY(),
            cPos.getZ());
    }

    @Override
    public <T, M> void openContainerCraftingOptionAmount(CraftingOptionGuiData<T, M, PartPos> craftingOptionGuiData,
        World world, EntityPlayerMP player) {
        PartPos partPos = craftingOptionGuiData.getLocationInstance();
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.CRAFTING_OPTION, Pair.of(partPos.getSide(), craftingOptionGuiData));

        IntegratedTerminals._instance.getPacketHandler()
            .sendToPlayer(new PacketSetCraftingDataPart(partPos.getSide(), craftingOptionGuiData), player);

        BlockPos cPos = partPos.getPos()
            .getBlockPos();
        player.openGui(
            IntegratedTerminals._instance,
            GuiProviders.ID_GUI_TERMINAL_STORAGE_CRAFTNG_OPTION_AMOUNT,
            world,
            cPos.getX(),
            cPos.getY(),
            cPos.getZ());
    }

    @Override
    public void writeToPacketBuffer(ExtendedBuffer packetBuffer, PartPos location) {
        try {
            PacketCodec.getAction(PartPos.class)
                .encode(location, packetBuffer);
        } catch (IOException e) {}
    }

    @Override
    public PartPos readFromPacketBuffer(ExtendedBuffer packetBuffer) {
        return (PartPos) PacketCodec.getAction(PartPos.class)
            .decode(packetBuffer);
    }
}
