package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCrafting;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a storage's quantity from server to client.
 *
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientMaxQuantityPacket extends PacketCodec {

    @CodecField
    private String tabId;
    @CodecField
    private String ingredientName;
    @CodecField
    private long maxQuantity;
    @CodecField
    private int channel;

    public TerminalStorageIngredientMaxQuantityPacket() {

    }

    public TerminalStorageIngredientMaxQuantityPacket(String tabId, IngredientComponent<?, ?> ingredientComponent,
        long maxQuantity, int channel) {
        this.tabId = tabId;
        this.ingredientName = ingredientComponent.getName()
            .toString();
        this.maxQuantity = maxQuantity;
        this.channel = channel;
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
            IngredientComponent<?, ?> ingredientComponent = IngredientComponent.REGISTRY
                .getValue(new ResourceLocation(this.ingredientName));
            if (ingredientComponent == null) {
                throw new IllegalArgumentException(
                    "No ingredient component with the given name was found: " + ingredientName);
            }
            TerminalStorageTabIngredientComponentClient<?, ?> tab = (TerminalStorageTabIngredientComponentClient<?, ?>) container
                .getTabClient(tabId);
            tab.setMaxQuantity(channel, maxQuantity);

            // Hard-coded crafting tab
            // TODO: abstract this as "auxiliary" tabs
            if (tabId.equals(
                IngredientComponent.ITEMSTACK.getName()
                    .toString())) {
                TerminalStorageTabIngredientComponentClient<?, ?> tabCrafting = (TerminalStorageTabIngredientComponentClient<?, ?>) container
                    .getTabClient(TerminalStorageTabIngredientComponentItemStackCrafting.NAME.toString());
                tabCrafting.setMaxQuantity(channel, maxQuantity);
            }
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

}
