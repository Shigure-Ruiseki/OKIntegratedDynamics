package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.part.TerminalPartTypes;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for opening a live crafting plan gui.
 *
 * @author rubensworks
 *
 */
public class OpenCraftingJobsGuiPacket extends PacketCodec {

    @CodecField
    private BlockPos pos;
    @CodecField
    private ForgeDirection side;

    public OpenCraftingJobsGuiPacket() {

    }

    public OpenCraftingJobsGuiPacket(BlockPos pos, ForgeDirection side) {
        this.pos = pos;
        this.side = side;
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
        PartHelpers.openContainerPart(
            player,
            PartPos.of(DimPos.of(world, pos), side),
            TerminalPartTypes.TERMINAL_CRAFTING_JOB);
    }

    public static void send(BlockPos pos, ForgeDirection side) {
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new OpenCraftingJobsGuiPacket(pos, side));
    }

}
