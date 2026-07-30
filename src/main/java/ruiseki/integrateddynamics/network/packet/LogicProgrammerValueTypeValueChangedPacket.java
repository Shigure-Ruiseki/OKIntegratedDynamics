package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeGuiElement;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeElement;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammer;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a button packet for the exalted crafting.
 * 
 * @author rubensworks
 *
 */
public class LogicProgrammerValueTypeValueChangedPacket extends PacketCodec {

    @CodecField
    private String value;

    public LogicProgrammerValueTypeValueChangedPacket() {

    }

    public LogicProgrammerValueTypeValueChangedPacket(String value) {
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
        if (player.openContainer instanceof ContainerLogicProgrammer) {
            ILogicProgrammerElement element = ((ContainerLogicProgrammer) player.openContainer).getActiveElement();
            if (element instanceof ValueTypeGuiElement<?, ?>) {
                ((ValueTypeElement) element).getInnerGuiElement()
                    .setInputString(value);
                ((ContainerLogicProgrammer) player.openContainer).onDirty();
            }
        }
    }

}
