package ruiseki.integratedterminals.network.packet;

import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCrafting;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingOption;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a storage change event from server to client.
 *
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientCraftingOptionsPacket extends PacketCodec {

    @CodecField
    private String tabId;
    @CodecField
    private int channel;
    @CodecField
    private NBTTagCompound data;
    @CodecField
    private boolean reset;
    @CodecField
    private boolean firstChannel;

    public TerminalStorageIngredientCraftingOptionsPacket() {

    }

    public <T> TerminalStorageIngredientCraftingOptionsPacket(String tabId, int channel,
        Collection<HandlerWrappedTerminalCraftingOption<T>> craftingOptions, boolean reset, boolean firstChannel) {
        this.tabId = tabId;
        this.channel = channel;
        this.data = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (HandlerWrappedTerminalCraftingOption<?> option : craftingOptions) {
            list.appendTag(HandlerWrappedTerminalCraftingOption.serialize(option));
        }
        this.data.setTag("craftingOptions", list);
        this.reset = reset;
        this.firstChannel = firstChannel;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        if (player.openContainer instanceof ContainerTerminalStorageBase) {
            ContainerTerminalStorageBase container = ((ContainerTerminalStorageBase) player.openContainer);

            TerminalStorageTabIngredientComponentClient<?, ?> tab = (TerminalStorageTabIngredientComponentClient<?, ?>) container
                .getTabClient(tabId);
            IngredientComponent<?, ?> ingredientComponent = tab.getIngredientComponent();

            NBTTagList list = this.data.getTagList("craftingOptions", Constants.NBT.TAG_COMPOUND);
            List<HandlerWrappedTerminalCraftingOption<?>> craftingOptions = Lists
                .newArrayListWithExpectedSize(list.tagCount());
            for (int i = 0; i < list.tagCount(); i++) {
                HandlerWrappedTerminalCraftingOption<?> option = HandlerWrappedTerminalCraftingOption
                    .deserialize(ingredientComponent, list.getCompoundTagAt(i));
                craftingOptions.add(option);
            }

            tab.addCraftingOptions(channel, (List) craftingOptions, this.reset, this.firstChannel);

            // Hard-coded crafting tab
            // TODO: abstract this as "auxiliary" tabs
            if (tabId.equals(
                IngredientComponent.ITEMSTACK.getName()
                    .toString())) {
                TerminalStorageTabIngredientComponentClient<?, ?> tabCrafting = (TerminalStorageTabIngredientComponentClient<?, ?>) container
                    .getTabClient(TerminalStorageTabIngredientComponentItemStackCrafting.NAME.toString());
                tabCrafting.addCraftingOptions(channel, (List) craftingOptions, this.reset, this.firstChannel);
            }

            container.refreshChannelStrings();
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

}
