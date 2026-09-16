package ruiseki.integratednbt.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.integratednbt.evaluate.NbtExtractorOutputMode;
import ruiseki.integratednbt.tileentity.TileNbtExtractor;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

/**
 * Sets the extraction path in the NBT Extractor.
 * 
 * @author rubensworks
 */
public class NbtExtractorSetOutputModePacket extends PacketCodec {

    @CodecField
    private BlockPos blockPos;
    private NbtExtractorOutputMode outputMode;

    public NbtExtractorSetOutputModePacket() {

    }

    public NbtExtractorSetOutputModePacket(BlockPos blockPos, NbtExtractorOutputMode outputMode) {
        this.blockPos = blockPos;
        this.outputMode = outputMode;
    }

    @Override
    public void encode(ExtendedBuffer output) {
        super.encode(output);
        output.writeByte(this.outputMode.ordinal());
    }

    @Override
    public void decode(ExtendedBuffer input) {
        super.decode(input);
        this.outputMode = NbtExtractorOutputMode.values()[input.readByte()];
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        // Do nothing
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        TileHelpers.get(world, blockPos, TileNbtExtractor.class)
            .ifPresent(blockEntity -> blockEntity.setOutputMode(outputMode));
    }

}
