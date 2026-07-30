package ruiseki.integrateddynamics.core.network.packet;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.persist.world.LabelsWorldStorage;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for notifying onLabelPacket changes.
 *
 * @author rubensworks
 *
 */
public class AllLabelsPacket extends PacketCodec {

    @CodecField
    private Map<Integer, String> labels;

    public AllLabelsPacket() {

    }

    public AllLabelsPacket(Map<Integer, String> labels) {
        this.labels = labels;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        for (Map.Entry<Integer, String> entry : labels.entrySet()) {
            LabelsWorldStorage.getInstance(IntegratedDynamics._instance)
                .putUnsafe(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

}
