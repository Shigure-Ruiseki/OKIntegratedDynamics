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
import ruiseki.integratedterminals.api.terminalstorage.TerminalClickType;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentServer;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorage;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a storage slot click event from client to server.
 *
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientSlotClickPacket<T> extends PacketCodec {

    @CodecField
    private String tabId;
    @CodecField
    private String ingredientName;
    @CodecField
    private int clickType;
    @CodecField
    private int channel;
    @CodecField
    private NBTTagCompound hoveringStorageInstanceData;
    @CodecField
    private int hoveredContainerSlot;
    @CodecField
    private long moveQuantityPlayerSlot;
    @CodecField
    private NBTTagCompound activeStorageInstanceData;
    @CodecField
    private boolean transferFullSelection;

    public TerminalStorageIngredientSlotClickPacket() {

    }

    public TerminalStorageIngredientSlotClickPacket(String tabId, IngredientComponent<T, ?> component,
        TerminalClickType clickType, int channel, T hoveringStorageInstance, int hoveredContainerSlot,
        long moveQuantityPlayerSlot, T activeStorageInstance, boolean transferFullSelection) {
        this.tabId = tabId;
        this.clickType = clickType.ordinal();
        this.ingredientName = component.getName()
            .toString();
        this.channel = channel;
        this.hoveringStorageInstanceData = new NBTTagCompound();
        IIngredientSerializer<T, ?> serializer = getComponent().getSerializer();
        this.hoveringStorageInstanceData.setTag("i", serializer.serializeInstance(hoveringStorageInstance));
        this.hoveredContainerSlot = hoveredContainerSlot;
        this.moveQuantityPlayerSlot = moveQuantityPlayerSlot;
        this.activeStorageInstanceData = new NBTTagCompound();
        this.activeStorageInstanceData.setTag("i", serializer.serializeInstance(activeStorageInstance));
        this.transferFullSelection = transferFullSelection;
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
        if (player.openContainer instanceof ContainerTerminalStorage) {
            ContainerTerminalStorage container = ((ContainerTerminalStorage) player.openContainer);
            TerminalStorageTabIngredientComponentServer<T, ?> tab = (TerminalStorageTabIngredientComponentServer<T, ?>) container
                .getTabServer(tabId);
            IIngredientSerializer<T, ?> serializer = getComponent().getSerializer();
            T hoveringStorageInstance = serializer.deserializeInstance(this.hoveringStorageInstanceData.getTag("i"));
            T activeInstance = serializer.deserializeInstance(this.activeStorageInstanceData.getTag("i"));
            tab.handleStorageSlotClick(
                container,
                player,
                getClickType(),
                getChannel(),
                hoveringStorageInstance,
                hoveredContainerSlot,
                moveQuantityPlayerSlot,
                activeInstance,
                transferFullSelection);
        }
    }

    public TerminalClickType getClickType() {
        return TerminalClickType.values()[this.clickType];
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
