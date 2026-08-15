package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.integratedterminals.proxy.guiprovider.GuiProviders;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Packet for opening the crafting plan gui.
 * 
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientOpenCraftingPlanGuiPacket<T, M>
    extends TerminalStorageIngredientCraftingOptionDataPacketAbstract<T, M> {

    public TerminalStorageIngredientOpenCraftingPlanGuiPacket() {

    }

    public TerminalStorageIngredientOpenCraftingPlanGuiPacket(CraftingOptionGuiData<T, M> craftingOptionData) {
        super(craftingOptionData);
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        CraftingOptionGuiData<T, M> data = getCraftingOptionData();
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.CRAFTING_OPTION, Pair.of(data.getSide(), data));
        BlockPos cPos = data.getPos();
        player.openGui(
            IntegratedTerminals._instance,
            GuiProviders.ID_GUI_TERMINAL_STORAGE_CRAFTNG_PLAN,
            world,
            cPos.getX(),
            cPos.getY(),
            cPos.getZ());
    }

}
