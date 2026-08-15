package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeIngredients;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeIngredientsLPElement;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a button packet for a change in current ingredients value.
 *
 * @author rubensworks
 *
 */
public class LogicProgrammerValueTypeIngredientsValueChangedPacket extends PacketCodec {

    @CodecField
    private String value;

    public LogicProgrammerValueTypeIngredientsValueChangedPacket() {

    }

    public LogicProgrammerValueTypeIngredientsValueChangedPacket(ValueObjectTypeIngredients.ValueIngredients value) {
        this.value = value.getType()
            .serialize(value);
    }

    protected ValueObjectTypeIngredients.ValueIngredients getValue() {
        return ValueHelpers.deserializeRaw(ValueTypes.OBJECT_INGREDIENTS, value);
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
            if (element instanceof ValueTypeIngredientsLPElement) {
                ((ValueTypeIngredientsLPElement) element).setServerValue(getValue());
                ((ContainerLogicProgrammerBase) player.openContainer).onDirty();
            }
        }
    }

}
