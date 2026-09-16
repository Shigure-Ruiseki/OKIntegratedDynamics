package ruiseki.integratednbt.network.packet;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;
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
public class NbtExtractorSetExtractionPathPacket extends PacketCodec {

    @CodecField
    private BlockPos blockPos;
    @CodecField
    private int defaultNBTId;
    private SegmentedNbtPath path;

    public NbtExtractorSetExtractionPathPacket() {

    }

    public NbtExtractorSetExtractionPathPacket(BlockPos blockPos, SegmentedNbtPath path, int defaultNBTId) {
        this.blockPos = blockPos;
        this.path = path;
        this.defaultNBTId = defaultNBTId;
    }

    @Override
    public void encode(ExtendedBuffer output) {
        super.encode(output);
        try {
            output.writeNBTTagCompoundToBuffer(this.path.toNBTCompound());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decode(ExtendedBuffer input) {
        super.decode(input);
        try {
            this.path = SegmentedNbtPath.fromNBT(input.readNBTTagCompoundFromBuffer())
                .orElse(new SegmentedNbtPath());
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
        // Do nothing
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        TileHelpers.get(world, blockPos, TileNbtExtractor.class)
            .ifPresent(tile -> {
                tile.setExtractionPath(path);
                tile.setDefaultNBTId((byte) defaultNBTId);
            });
    }

}
