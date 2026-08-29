package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.logicprogrammer.ILogicProgrammerElement;
import ruiseki.integrateddynamics.core.ingredient.ItemMatchProperties;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeRecipeLPElement;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a to the server if recipe slot properties have changed.
 * 
 * @author rubensworks
 *
 */
public class LogicProgrammerValueTypeRecipeSlotPropertiesChangedPacket extends PacketCodec {

    @CodecField
    private int slot;
    @CodecField
    private boolean nbt;
    @CodecField
    private String tag;
    @CodecField
    private int tagQuantity;

    public LogicProgrammerValueTypeRecipeSlotPropertiesChangedPacket() {

    }

    public LogicProgrammerValueTypeRecipeSlotPropertiesChangedPacket(int slot, boolean nbt, String tag,
        int tagQuantity) {
        this.slot = slot;
        this.nbt = nbt;
        this.tag = tag;
        this.tagQuantity = tagQuantity;
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
                ItemMatchProperties props = ((ValueTypeRecipeLPElement) element).getInputStacks()
                    .get(slot);
                props.setNbt(nbt);
                props.setItemTag(tag.isEmpty() ? null : tag);
                props.setTagQuantity(this.tagQuantity);
                ((ContainerLogicProgrammerBase) player.openContainer).onDirty();
            }
        }
    }

}
