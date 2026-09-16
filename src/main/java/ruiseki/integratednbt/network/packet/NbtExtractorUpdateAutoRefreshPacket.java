package ruiseki.integratednbt.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.integratednbt.tileentity.TileNbtExtractor;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Updates the auto refresh flag in the NBT Extractor.
 * 
 * @author rubensworks
 */
public class NbtExtractorUpdateAutoRefreshPacket extends PacketCodec {

    @CodecField
    private BlockPos blockPos;
    @CodecField
    private boolean autoRefresh;

    public NbtExtractorUpdateAutoRefreshPacket() {

    }

    public NbtExtractorUpdateAutoRefreshPacket(BlockPos blockPos, boolean autoRefresh) {
        this.blockPos = blockPos;
        this.autoRefresh = autoRefresh;
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
            .ifPresent(blockEntity -> blockEntity.updateAutoRefresh(autoRefresh));
    }

}
