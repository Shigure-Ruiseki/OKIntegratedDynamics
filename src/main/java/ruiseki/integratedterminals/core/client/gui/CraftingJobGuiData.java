package ruiseki.integratedterminals.core.client.gui;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalStorageTabIngredientCraftingHandler;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * @author rubensworks
 */
public class CraftingJobGuiData {

    private final BlockPos pos;
    private final ForgeDirection side;
    private final int channel;
    private final ITerminalStorageTabIngredientCraftingHandler handler;
    private final Object craftingJob;

    public CraftingJobGuiData(BlockPos pos, ForgeDirection side, int channel,
        ITerminalStorageTabIngredientCraftingHandler handler, Object craftingJob) {
        this.pos = pos;
        this.side = side;
        this.channel = channel;
        this.handler = handler;
        this.craftingJob = craftingJob;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ForgeDirection getSide() {
        return side;
    }

    public int getChannel() {
        return channel;
    }

    public ITerminalStorageTabIngredientCraftingHandler getHandler() {
        return handler;
    }

    public Object getCraftingJob() {
        return craftingJob;
    }
}
