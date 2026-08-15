package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeStringLPElement;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a button packet for the exalted crafting.
 *
 * @author rubensworks
 *
 */
public class LogicProgrammerValueTypeStringValueChangedPacket extends PacketCodec {

    @CodecField
    private String value;

    public LogicProgrammerValueTypeStringValueChangedPacket() {

    }

    public LogicProgrammerValueTypeStringValueChangedPacket(String value) {
        this.value = value;
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
        if (player.openContainer instanceof ContainerLogicProgrammerBase container) {
            ILogicProgrammerElement element = container.getActiveElement();
            if (element instanceof ValueTypeStringLPElement valueTypeElement) {
                valueTypeElement.getInnerGuiElement()
                    .setInputString(value);
                container.onDirty();
            }
        }
    }

}
