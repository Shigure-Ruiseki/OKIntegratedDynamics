package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeRecipeLPElement;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a to the server if a recipe string value has changed.
 * 
 * @author rubensworks
 *
 */
public class LogicProgrammerValueTypeRecipeValueChangedPacket extends PacketCodec {

    @CodecField
    private String value;
    @CodecField
    private int type;

    public LogicProgrammerValueTypeRecipeValueChangedPacket() {

    }

    public LogicProgrammerValueTypeRecipeValueChangedPacket(String value, Type type) {
        this.value = value;
        this.type = type.ordinal();
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
            if (element instanceof ValueTypeRecipeLPElement) {
                Type type = Type.values()[this.type];
                switch (type) {
                    case INPUT_FLUID:
                        ((ValueTypeRecipeLPElement) element).setInputFluidAmount(value);
                        break;
                    case INPUT_ENERGY:
                        ((ValueTypeRecipeLPElement) element).setInputEnergy(value);
                        break;
                    case OUTPUT_FLUID:
                        ((ValueTypeRecipeLPElement) element).setOutputFluidAmount(value);
                        break;
                    case OUTPUT_ENERGY:
                        ((ValueTypeRecipeLPElement) element).setOutputEnergy(value);
                        break;
                }
                ((ContainerLogicProgrammerBase) player.openContainer).onDirty();
            }
        }
    }

    public static enum Type {
        INPUT_FLUID,
        INPUT_ENERGY,
        OUTPUT_FLUID,
        OUTPUT_ENERGY,
    }

}
