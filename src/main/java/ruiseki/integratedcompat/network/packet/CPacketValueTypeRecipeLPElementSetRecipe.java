package ruiseki.integratedcompat.network.packet;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeRecipeLPElement;
import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * @author rubensworks
 */
public class CPacketValueTypeRecipeLPElementSetRecipe extends PacketCodec {

    @CodecField
    private int windowId;
    @CodecField
    private List<ItemStack> itemInputs;
    @CodecField
    private List<FluidStack> fluidInputs;
    @CodecField
    private List<ItemStack> itemOutputs;
    @CodecField
    private List<FluidStack> fluidOutputs;

    public CPacketValueTypeRecipeLPElementSetRecipe() {

    }

    public CPacketValueTypeRecipeLPElementSetRecipe(int windowId, List<ItemStack> itemInputs,
        List<FluidStack> fluidInputs, List<ItemStack> itemOutputs, List<FluidStack> fluidOutputs) {
        this.windowId = windowId;
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
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
        if (player.openContainer.windowId == windowId) {
            ContainerLogicProgrammerBase container = (ContainerLogicProgrammerBase) player.openContainer;
            ValueTypeRecipeLPElement element = (ValueTypeRecipeLPElement) container.getActiveElement();
            element.setRecipeGrid(container, itemInputs, fluidInputs, itemOutputs, fluidOutputs);
        }
    }

}
