package ruiseki.integratedterminals.core.terminalstorage.location;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocation;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.integratedterminals.network.packet.PacketSetCraftingDataItem;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemOpenPacket;
import ruiseki.integratedterminals.proxy.guiprovider.GuiProviders;
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

        TerminalStorageIngredientItemOpenPacket
            .send(slot, craftingOptionGuiData.getTabName(), craftingOptionGuiData.getChannel());
    }

    @Override
    public <T, M> void openContainerFromServer(CraftingOptionGuiData<T, M, Integer> craftingOptionGuiData, World world,
        EntityPlayerMP player) {
        Integer slot = craftingOptionGuiData.getLocationInstance();

        TerminalStorageIngredientItemOpenPacket
            .openServer(world, slot, player, craftingOptionGuiData.getTabName(), craftingOptionGuiData.getChannel());
    }

    @Override
    public <T, M> void openContainerCraftingPlan(CraftingOptionGuiData<T, M, Integer> craftingOptionGuiData,
        World world, EntityPlayerMP player) {
        Integer location = craftingOptionGuiData.getLocationInstance();

        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.CRAFTING_OPTION_ITEM, Pair.of(location, craftingOptionGuiData));

        IntegratedTerminals._instance.getPacketHandler()
            .sendToPlayer(new PacketSetCraftingDataItem(location, craftingOptionGuiData), player);

        player.openGui(
            IntegratedTerminals._instance,
            GuiProviders.ID_GUI_TERMINAL_STORAGE_CRAFTING_PLAN_ITEM,
            world,
            (int) player.posX,
            (int) player.posY,
            (int) player.posZ);
    }

    @Override
    public <T, M> void openContainerCraftingOptionAmount(CraftingOptionGuiData<T, M, Integer> craftingOptionGuiData,
        World world, EntityPlayerMP player) {
        Integer location = craftingOptionGuiData.getLocationInstance();

        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.CRAFTING_OPTION_ITEM, Pair.of(location, craftingOptionGuiData));

        IntegratedTerminals._instance.getPacketHandler()
            .sendToPlayer(new PacketSetCraftingDataItem(location, craftingOptionGuiData), player);

        player.openGui(
            IntegratedTerminals._instance,
            GuiProviders.ID_GUI_TERMINAL_STORAGE_CRAFTING_PLAN_ITEM,
            world,
            (int) player.posX,
            (int) player.posY,
            (int) player.posZ);
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
