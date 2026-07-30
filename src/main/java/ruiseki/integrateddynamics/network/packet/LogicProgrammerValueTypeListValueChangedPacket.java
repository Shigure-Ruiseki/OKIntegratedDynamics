package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeListElement;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammer;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a button packet for the exalted crafting.
 * 
 * @author rubensworks
 *
 */
public class LogicProgrammerValueTypeListValueChangedPacket extends PacketCodec {

    @CodecField
    private String value;

    public LogicProgrammerValueTypeListValueChangedPacket() {

    }

    public LogicProgrammerValueTypeListValueChangedPacket(ValueTypeList.ValueList value) {
        this.value = value.getType()
            .serialize(value);
    }

    protected ValueTypeList.ValueList getListValue() {
        return ValueTypes.LIST.deserialize(value);
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
            if (element instanceof ValueTypeListElement) {
                ((ValueTypeListElement) element).setServerValue(getListValue());
                ((ContainerLogicProgrammer) player.openContainer).onDirty();
            }
        }
    }

}
