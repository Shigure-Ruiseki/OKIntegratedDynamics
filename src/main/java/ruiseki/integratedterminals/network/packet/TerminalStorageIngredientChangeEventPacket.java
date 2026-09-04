package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.ingredient.IIngredientComponentStorageObservable;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCrafting;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.ingredient.collection.IIngredientCollection;
import ruiseki.okcore.ingredient.collection.IngredientArrayList;
import ruiseki.okcore.ingredient.collection.IngredientCollections;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a storage change event from server to client.
 *
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientChangeEventPacket extends PacketCodec {

    @CodecField
    private String tabId;
    @CodecField
    private NBTTagCompound changeData;
    @CodecField
    private int channel;
    @CodecField
    private boolean enabled;

    public TerminalStorageIngredientChangeEventPacket() {

    }

    public TerminalStorageIngredientChangeEventPacket(String tabId,
        IIngredientComponentStorageObservable.StorageChangeEvent<?, ?> event, boolean enabled) {
        this.tabId = tabId;
        IIngredientComponentStorageObservable.Change changeType = event.getChangeType();
        IIngredientCollection<?, ?> instances = event.getInstances();
        NBTTagCompound serialized = IngredientCollections.serialize(instances);
        serialized.setInteger("changeType", changeType.ordinal());
        this.changeData = serialized;
        this.channel = event.getChannel();
        this.enabled = enabled;
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
            IIngredientComponentStorageObservable.Change changeType = IIngredientComponentStorageObservable.Change
                .values()[changeData.getInteger("changeType")];
            IngredientArrayList ingredients = IngredientCollections.deserialize(changeData);

            TerminalStorageTabIngredientComponentClient<?, ?> tab = (TerminalStorageTabIngredientComponentClient<?, ?>) container
                .getTabClient(tabId);
            tab.onChange(channel, changeType, ingredients, enabled);

            // Hard-coded crafting tab
            // TODO: abstract this as "auxiliary" tabs
            if (tabId.equals(
                IngredientComponent.ITEMSTACK.getName()
                    .toString())) {
                TerminalStorageTabIngredientComponentClient<?, ?> tabCrafting = (TerminalStorageTabIngredientComponentClient<?, ?>) container
                    .getTabClient(TerminalStorageTabIngredientComponentItemStackCrafting.NAME.toString());
                tabCrafting.onChange(channel, changeType, ingredients, enabled);
            }

            container.refreshChannelStrings();
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

}
