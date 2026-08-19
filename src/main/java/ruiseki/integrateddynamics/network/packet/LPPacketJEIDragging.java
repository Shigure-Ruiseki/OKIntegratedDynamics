package ruiseki.integrateddynamics.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class LPPacketJEIDragging extends PacketCodec {

    @CodecField
    private int slotIndex;
    @CodecField
    private ItemStack itemStack;

    public LPPacketJEIDragging() {}

    public LPPacketJEIDragging(int slotIndex, ItemStack itemStack) {
        this.slotIndex = slotIndex;
        this.itemStack = itemStack;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer entityPlayer) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerLogicProgrammerBase) {
            IInventory temporaryInputSlots = ((ContainerLogicProgrammerBase) player.openContainer)
                .getTemporaryInputSlots();
            temporaryInputSlots.setInventorySlotContents(this.slotIndex, this.itemStack);
        }
    }
}
