package ruiseki.integratedterminals.core.client.gui;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalStorageTabIngredientCraftingHandler;
import ruiseki.integratedterminals.core.terminalstorage.crafting.TerminalStorageTabIngredientCraftingHandlers;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * @author rubensworks
 */
public class CraftingJobGuiData {

    private final BlockPos pos;
    private final ForgeDirection side;
    private final int channel;
    private final ITerminalStorageTabIngredientCraftingHandler handler;
    private final Object craftingJob;

    public CraftingJobGuiData(BlockPos pos, ForgeDirection side, int channel,
        ITerminalStorageTabIngredientCraftingHandler handler, Object craftingJob) {
        this.pos = pos;
        this.side = side;
        this.channel = channel;
        this.handler = handler;
        this.craftingJob = craftingJob;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ForgeDirection getSide() {
        return side;
    }

    public int getChannel() {
        return channel;
    }

    public ITerminalStorageTabIngredientCraftingHandler getHandler() {
        return handler;
    }

    public Object getCraftingJob() {
        return craftingJob;
    }

    public void writeToPacketBuffer(ExtendedBuffer packetBuffer) throws IOException {
        packetBuffer.writeLong(pos.toLong());
        packetBuffer.writeInt(side.ordinal());
        packetBuffer.writeInt(channel);
        packetBuffer.writeString(
            handler.getId()
                .toString());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("id", handler.serializeCraftingJobId(craftingJob));
        packetBuffer.writeNBTTagCompoundToBuffer(tag);
    }

    public static CraftingJobGuiData readFromPacketBuffer(ExtendedBuffer packetBuffer) throws IOException {
        BlockPos pos = BlockPos.fromLong(packetBuffer.readLong());
        ForgeDirection side = ForgeDirection.values()[packetBuffer.readInt()];
        int channel = packetBuffer.readInt();
        ITerminalStorageTabIngredientCraftingHandler handler = TerminalStorageTabIngredientCraftingHandlers.REGISTRY
            .getHandler(new ResourceLocation(packetBuffer.readString()));
        Object craftingJob = handler.deserializeCraftingJobId(
            packetBuffer.readNBTTagCompoundFromBuffer()
                .getCompoundTag("id"));
        return new CraftingJobGuiData(pos, side, channel, handler, craftingJob);
    }
}
