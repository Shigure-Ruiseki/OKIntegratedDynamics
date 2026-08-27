package ruiseki.integratedterminals.api.terminalstorage.location;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * Indicates the location of a storage terminal.
 * 
 * @param <L> The location type
 * @author rubensworks
 */
public interface ITerminalStorageLocation<L> {

    public ResourceLocation getName();

    public <T, M> void openContainerFromClient(CraftingOptionGuiData<T, M, L> craftingOptionGuiData);

    public <T, M> void openContainerFromServer(CraftingOptionGuiData<T, M, L> craftingOptionGuiData, World world,
        EntityPlayerMP player);

    public <T, M> void openContainerCraftingPlan(CraftingOptionGuiData<T, M, L> craftingOptionGuiData, World world,
        EntityPlayerMP player);

    public <T, M> void openContainerCraftingOptionAmount(CraftingOptionGuiData<T, M, L> craftingOptionGuiData,
        World world, EntityPlayerMP player);

    public void writeToPacketBuffer(ExtendedBuffer packetBuffer, L location);

    public L readFromPacketBuffer(ExtendedBuffer packetBuffer);

}
