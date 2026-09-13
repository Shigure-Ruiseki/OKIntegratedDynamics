package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a button packet for the exalted crafting.
 *
 * @author rubensworks
 *
 */
public class LogicProgrammerActivateElementPacket extends PacketCodec {

    @CodecField
    private ResourceLocation typeId;
    @CodecField
    private ResourceLocation elementId;

    public LogicProgrammerActivateElementPacket() {

    }

    public LogicProgrammerActivateElementPacket(ResourceLocation typeId, ResourceLocation elementId) {
        this.typeId = typeId;
        this.elementId = elementId;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerLogicProgrammerBase) {
            if (typeId != null) {
                ((ContainerLogicProgrammerBase) player.openContainer).returnWriteItemToPlayer();
            }
            ((ContainerLogicProgrammerBase) player.openContainer).setActiveElementById(typeId, elementId);
        }
    }

}
