package ruiseki.integratedterminals.network.packet;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalStorageTabIngredientCraftingHandler;
import ruiseki.integratedterminals.core.client.gui.CraftingJobGuiData;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingPlan;
import ruiseki.integratedterminals.core.terminalstorage.crafting.TerminalStorageTabIngredientCraftingHandlers;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalCraftingJobsPlan;
import ruiseki.integratedterminals.part.PartTypeTerminalCraftingJob;
import ruiseki.integratedterminals.part.TerminalPartTypes;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.PlayerHelpers;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.container.ContainerExtended;
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
        // Create common data holder
        ITerminalStorageTabIngredientCraftingHandler handler = getHandler();
        CraftingJobGuiData craftingJobGuiData = new CraftingJobGuiData(
            pos,
            side,
            channel,
            handler,
            handler.deserializeCraftingJobId(craftingJobId.getTag("id")));
        PartPos partPos = PartPos.of(world, pos, side);

        // Create temporary container provider
        IGuiConstructor containerProvider = new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers
                    .getContainerPartConstructionData(partPos);
                return new ContainerTerminalCraftingJobsPlan(
                    playerInventory,
                    data.getRight(),
                    Optional.of(data.getLeft()),
                    (PartTypeTerminalCraftingJob) data.getMiddle(),
                    craftingJobGuiData);
            }
        };

        // Trigger gui opening
        PlayerHelpers.openGui(player, containerProvider, packetBuffer -> {
            try {
                PacketCodec.getAction(PartPos.class)
                    .encode(partPos, packetBuffer);
                packetBuffer.writeString(
                    TerminalPartTypes.TERMINAL_CRAFTING_JOB.getUniqueName()
                        .toString());
                craftingJobGuiData.writeToPacketBuffer(packetBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
            craftingPlan.getCraftingPlanFlat()
                .getId());
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(new OpenCraftingJobsPlanGuiPacket(data));
    }

}
