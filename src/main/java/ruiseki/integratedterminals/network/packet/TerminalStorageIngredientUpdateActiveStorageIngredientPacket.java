package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.commoncapabilities.api.ingredient.IIngredientSerializer;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending the currently active storage stack from server to client.
 *
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientUpdateActiveStorageIngredientPacket<T> extends PacketCodec {

    @CodecField
    private String tabId;
    @CodecField
    private String ingredientName;
    @CodecField
    private int channel;
    @CodecField
    private NBTTagCompound activeStorageInstanceData;

    public TerminalStorageIngredientUpdateActiveStorageIngredientPacket() {

    }

    public TerminalStorageIngredientUpdateActiveStorageIngredientPacket(String tabId,
        IngredientComponent<T, ?> component, int channel, T activeStorageInstance) {
        this.tabId = tabId;
        this.ingredientName = component.getName()
            .toString();
        this.channel = channel;
        IIngredientSerializer<T, ?> serializer = getComponent().getSerializer();
        this.activeStorageInstanceData = new NBTTagCompound();
        this.activeStorageInstanceData.setTag("i", serializer.serializeInstance(activeStorageInstance));
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
            TerminalStorageTabIngredientComponentClient<T, ?> tab = (TerminalStorageTabIngredientComponentClient<T, ?>) container
                .getTabClient(tabId);
            IIngredientSerializer<T, ?> serializer = getComponent().getSerializer();
            T activeInstance = serializer.deserializeInstance(this.activeStorageInstanceData.getTag("i"));
            tab.handleActiveIngredientUpdate(getChannel(), activeInstance);
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

    public IngredientComponent<T, ?> getComponent() {
        IngredientComponent<T, ?> ingredientComponent = (IngredientComponent<T, ?>) IngredientComponent.REGISTRY
            .getValue(new ResourceLocation(this.ingredientName));
        if (ingredientComponent == null) {
            throw new IllegalArgumentException(
                "No ingredient component with the given name was found: " + ingredientName);
        }
        return ingredientComponent;
    }

    public int getChannel() {
        return channel;
    }

}
