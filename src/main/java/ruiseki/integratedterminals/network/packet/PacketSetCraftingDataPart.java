package ruiseki.integratedterminals.network.packet;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

public class PacketSetCraftingDataPart extends PacketCodec {

    @CodecField
    private ForgeDirection side;
    private CraftingOptionGuiData craftingOptionGuiData;

    public PacketSetCraftingDataPart() {}

    public PacketSetCraftingDataPart(ForgeDirection side, CraftingOptionGuiData craftingOptionGuiData) {
        this.side = side;
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
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.CRAFTING_OPTION_PART, Pair.of(side, craftingOptionGuiData));
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {}
}
