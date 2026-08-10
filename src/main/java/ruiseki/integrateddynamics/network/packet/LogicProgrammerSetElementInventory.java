package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeListLPElement;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for updating the element inventory inside the logic programmer.
 * 
 * @author rubensworks
 *
 */
public class LogicProgrammerSetElementInventory extends PacketCodec {

    @CodecField
    private String listValueType;
    @CodecField
    private int baseX;
    @CodecField
    private int baseY;

    public LogicProgrammerSetElementInventory() {

    }

    public LogicProgrammerSetElementInventory(IValueType listValueType, int baseX, int baseY) {
        this.listValueType = listValueType.getUnlocalizedName();
        this.baseX = baseX;
        this.baseY = baseY;
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
            ContainerLogicProgrammerBase container = (ContainerLogicProgrammerBase) player.openContainer;
            ILogicProgrammerElement element = container.getActiveElement();
            if (element instanceof ValueTypeListLPElement) {
                IValueType valueType = ValueTypes.REGISTRY.getValueType(this.listValueType);
                if (valueType != null) {
                    ((ContainerLogicProgrammerBase) player.openContainer)
                        .setElementInventory(valueType.createLogicProgrammerElement(), baseX, baseY);
                } else {
                    IntegratedDynamics.clog(
                        Level.WARN,
                        "Got an invalid LogicProgrammerSetElementInventory packet: " + this.listValueType);
                }
            }
        }
    }

}
