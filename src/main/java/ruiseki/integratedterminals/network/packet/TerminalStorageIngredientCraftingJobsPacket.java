package ruiseki.integratedterminals.network.packet;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.commoncapabilities.IngredientComponents;
import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;
import ruiseki.integratedterminals.api.terminalstorage.crafting.TerminalCraftingJobStatus;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCrafting;
import ruiseki.integratedterminals.core.terminalstorage.crafting.PendingCraftingJobOutput;
import ruiseki.integratedterminals.core.terminalstorage.crafting.PendingCraftingJobOutputEntry;
import ruiseki.integratedterminals.core.terminalstorage.crafting.PendingCraftingJobOutputs;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending the pending outputs of all running crafting jobs from server to client.
 *
 * This is used to indicate the ingredients that are being crafted in the storage terminal.
 *
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientCraftingJobsPacket extends PacketCodec {

    @CodecField
    private String tabId;
    @CodecField
    private int channel;
    @CodecField
    private NBTTagCompound data;

    public TerminalStorageIngredientCraftingJobsPacket() {

    }

    public <T, M> TerminalStorageIngredientCraftingJobsPacket(String tabId,
        PendingCraftingJobOutputs<T, M> pendingCraftingJobOutputs) {
        this.tabId = tabId;
        this.channel = pendingCraftingJobOutputs.getChannel();
        this.data = new NBTTagCompound();

        IIngredientMatcher<T, M> matcher = pendingCraftingJobOutputs.getIngredientComponent()
            .getMatcher();
        NBTTagList list = new NBTTagList();
        for (PendingCraftingJobOutput<T> output : pendingCraftingJobOutputs.getOutputs()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag(
                "ingredient",
                IPrototypedIngredient.serialize(
                    new PrototypedIngredient<>(
                        pendingCraftingJobOutputs.getIngredientComponent(),
                        output.getInstance(),
                        matcher.getExactMatchNoQuantityCondition())));
            tag.setInteger(
                "status",
                output.getStatus()
                    .ordinal());
            list.appendTag(tag);
        }
        this.data.setTag("craftingJobOutputs", list);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        NBTTagList list = this.data.getTagList("craftingJobOutputs", Constants.NBT.TAG_COMPOUND);
        List<PendingCraftingJobOutputEntry> outputs = Lists.newArrayListWithExpectedSize(list.tagCount());
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            outputs.add(
                new PendingCraftingJobOutputEntry(
                    IPrototypedIngredient.deserialize(tag.getCompoundTag("ingredient")),
                    TerminalCraftingJobStatus.values()[tag.getInteger("status")]));
        }

        // Run the following code in the render thread, since this packet runs in a different thread. (isAsync is true)
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                if (player.openContainer instanceof ContainerTerminalStorageBase container) {
                    TerminalStorageTabIngredientComponentClient<?, ?> tab = (TerminalStorageTabIngredientComponentClient<?, ?>) container
                        .getTabClient(tabId);
                    if (tab != null) {
                        tab.setPendingCraftingJobOutputs(channel, outputs);
                    }

                    // Hard-coded crafting tab
                    // TODO: abstract this as "auxiliary" tabs
                    if (tabId.equals(
                        IngredientComponents.ITEMSTACK.getName()
                            .toString())) {
                        TerminalStorageTabIngredientComponentClient<?, ?> tabCrafting = (TerminalStorageTabIngredientComponentClient<?, ?>) container
                            .getTabClient(TerminalStorageTabIngredientComponentItemStackCrafting.NAME.toString());
                        if (tabCrafting != null) {
                            tabCrafting.setPendingCraftingJobOutputs(channel, outputs);
                        }
                    }
                }
            });
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

}
