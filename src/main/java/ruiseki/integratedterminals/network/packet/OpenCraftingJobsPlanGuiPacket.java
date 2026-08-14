package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalStorageTabIngredientCraftingHandler;
import ruiseki.integratedterminals.core.client.gui.CraftingJobGuiData;
import ruiseki.integratedterminals.core.client.gui.ExtendedGuiHandler;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingPlan;
import ruiseki.integratedterminals.core.terminalstorage.crafting.TerminalStorageTabIngredientCraftingHandlers;
import ruiseki.integratedterminals.proxy.guiprovider.GuiProviders;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for opening a live crafting plan gui.
 * 
 * @author rubensworks
 *
 */
public class OpenCraftingJobsPlanGuiPacket extends PacketCodec {

    @CodecField
    private BlockPos pos;
    @CodecField
    private ForgeDirection side;
    @CodecField
    private int channel;
    @CodecField
    private String craftingPlanHandler;
    @CodecField
    private NBTTagCompound craftingJobId;

    public OpenCraftingJobsPlanGuiPacket() {

    }

    public OpenCraftingJobsPlanGuiPacket(CraftingJobGuiData craftingPlanGuiData) {
        this.pos = craftingPlanGuiData.getPos();
        this.side = craftingPlanGuiData.getSide();
        this.channel = craftingPlanGuiData.getChannel();
        this.craftingPlanHandler = craftingPlanGuiData.getHandler()
            .getId()
            .toString();
        this.craftingJobId = new NBTTagCompound();
        this.craftingJobId.setTag(
            "id",
            craftingPlanGuiData.getHandler()
                .serializeCraftingJobId(craftingPlanGuiData.getCraftingJob()));
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        ITerminalStorageTabIngredientCraftingHandler handler = getHandler();
        CraftingJobGuiData data = new CraftingJobGuiData(
            pos,
            side,
            channel,
            handler,
            handler.deserializeCraftingJobId(craftingJobId.getTag("id")));
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.CRAFTING_PLAN, Pair.of(side, data));
        player.openGui(
            IntegratedTerminals._instance,
            GuiProviders.ID_GUI_TERMINAL_CRAFTING_JOBS_PLAN,
            world,
            pos.getX(),
            pos.getY(),
            pos.getZ());
    }

    protected ITerminalStorageTabIngredientCraftingHandler getHandler() {
        return TerminalStorageTabIngredientCraftingHandlers.REGISTRY
            .getHandler(new ResourceLocation(this.craftingPlanHandler));
    }

    public static void send(BlockPos pos, ForgeDirection side, int channel,
        HandlerWrappedTerminalCraftingPlan craftingPlan) {
        CraftingJobGuiData data = new CraftingJobGuiData(
            pos,
            side,
            channel,
            craftingPlan.getHandler(),
            craftingPlan.getCraftingPlan()
                .getId());
        IntegratedTerminals._instance.getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.CRAFTING_PLAN, Pair.of(side, data));
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new OpenCraftingJobsPlanGuiPacket(data));
    }

}
