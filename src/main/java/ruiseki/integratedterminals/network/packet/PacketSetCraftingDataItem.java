package ruiseki.integratedterminals.network.packet;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

public class PacketSetCraftingDataItem extends PacketCodec {

    @CodecField
    private int slotIndex;
    private CraftingOptionGuiData craftingOptionGuiData;

    public PacketSetCraftingDataItem() {}

    public PacketSetCraftingDataItem(int slotIndex, CraftingOptionGuiData craftingOptionGuiData) {
        this.slotIndex = slotIndex;
        this.craftingOptionGuiData = craftingOptionGuiData;
    }

    @Override
    public void encode(ExtendedBuffer output) {
        super.encode(output);
        try {
            craftingOptionGuiData.writeToPacketBuffer(output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decode(ExtendedBuffer input) {
        super.decode(input);
        try {
            this.craftingOptionGuiData = CraftingOptionGuiData.readFromPacketBuffer(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.CRAFTING_OPTION_ITEM, Pair.of(slotIndex, craftingOptionGuiData));
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}
