package ruiseki.integratednbt.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.integratednbt.item.ItemNbtExtractorRemote;
import ruiseki.integratednbt.item.ItemNbtExtractorRemoteConfig;
import ruiseki.okcore.network.PacketCodec;

/**
 * Requests to open the GUI for an NBT Extractor at location.
 * 
 * @author rubensworks
 */
public class OpenNbtExtractorRemoteGuiPacket extends PacketCodec {

    public OpenNbtExtractorRemoteGuiPacket() {

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
        ItemNbtExtractorRemote remote = (ItemNbtExtractorRemote) ItemNbtExtractorRemoteConfig._instance.getInstance();
        assert player != null;
        if (player.getItemInUse()
            .getItem() == remote) {
            remote.serverUse(player.getItemInUse(), player);
        }
    }

}
