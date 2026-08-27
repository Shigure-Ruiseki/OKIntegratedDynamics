package ruiseki.integratedterminals.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCraftingCommon;
import ruiseki.integratedterminals.core.terminalstorage.button.TerminalButtonItemStackCraftingGridAutoRefill;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for telling the server the new autoRefill value.
 *
 * @author rubensworks
 *
 */
public class TerminalStorageIngredientItemStackCraftingGridSetAutoRefill extends PacketCodec {

    @CodecField
    private String tabId;
    @CodecField
    private int autoRefillType;

    public TerminalStorageIngredientItemStackCraftingGridSetAutoRefill() {

    }

    public TerminalStorageIngredientItemStackCraftingGridSetAutoRefill(String tabId,
        TerminalButtonItemStackCraftingGridAutoRefill.AutoRefillType autoRefill) {
        this.tabId = tabId;
        this.autoRefillType = autoRefill.ordinal();
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
        if (player.openContainer instanceof ContainerTerminalStorageBase) {
            ContainerTerminalStorageBase container = ((ContainerTerminalStorageBase) player.openContainer);
            ITerminalStorageTabCommon tabCommon = container.getTabCommon(tabId);
            if (tabCommon instanceof TerminalStorageTabIngredientComponentItemStackCraftingCommon) {
                ((TerminalStorageTabIngredientComponentItemStackCraftingCommon) tabCommon).setAutoRefill(
                    TerminalButtonItemStackCraftingGridAutoRefill.AutoRefillType.values()[this.autoRefillType]);
            }
        }
    }

}
