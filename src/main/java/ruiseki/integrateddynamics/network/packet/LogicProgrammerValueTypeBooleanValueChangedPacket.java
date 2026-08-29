package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeBooleanLPElement;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for changing a boolean LP value.
 *
 * @author rubensworks
 *
 */
public class LogicProgrammerValueTypeBooleanValueChangedPacket extends PacketCodec {

    @CodecField
    private boolean checked;

    public LogicProgrammerValueTypeBooleanValueChangedPacket() {

    }

    public LogicProgrammerValueTypeBooleanValueChangedPacket(boolean checked) {
        this.checked = checked;
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
            ILogicProgrammerElement element = ((ContainerLogicProgrammerBase) player.openContainer).getActiveElement();
            if (element instanceof ValueTypeBooleanLPElement) {
                ((ValueTypeBooleanLPElement) element).getInnerGuiElement()
                    .setInputBoolean(checked);
                ((ContainerLogicProgrammerBase) player.openContainer).onDirty();
            }
        }
    }

}
