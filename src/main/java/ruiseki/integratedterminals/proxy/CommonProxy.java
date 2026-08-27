package ruiseki.integratedterminals.proxy;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.network.packet.CancelCraftingJobPacket;
import ruiseki.integratedterminals.network.packet.OpenCraftingJobsGuiPacket;
import ruiseki.integratedterminals.network.packet.OpenCraftingJobsPlanGuiPacket;
import ruiseki.integratedterminals.network.packet.PacketSetCraftingDataPart;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientChangeEventPacket;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientCraftingOptionsPacket;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridBalance;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridClear;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridSetAutoRefill;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridSetResult;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridShiftClickOutput;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientMaxQuantityPacket;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientOpenCraftingJobAmountGuiPacket;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientOpenCraftingPlanGuiPacket;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientPartOpenPacket;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientSlotClickPacket;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientUpdateActiveStorageIngredientPacket;
import ruiseki.integratedterminals.proxy.guiprovider.GuiProviders;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.network.PacketHandler;
import ruiseki.okcore.proxy.CommonProxyComponent;

/**
 * Proxy for server and client side.
 *
 * @author rubensworks
 *
 */
public class CommonProxy extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return IntegratedTerminals._instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);

        // Register packets.
        packetHandler.register(TerminalStorageIngredientPartOpenPacket.class);
        packetHandler.register(TerminalStorageIngredientChangeEventPacket.class);
        packetHandler.register(TerminalStorageIngredientCraftingOptionsPacket.class);
        packetHandler.register(TerminalStorageIngredientMaxQuantityPacket.class);
        packetHandler.register(TerminalStorageIngredientSlotClickPacket.class);
        packetHandler.register(TerminalStorageIngredientOpenCraftingPlanGuiPacket.class);
        packetHandler.register(TerminalStorageIngredientOpenCraftingJobAmountGuiPacket.class);
        packetHandler.register(PacketSetCraftingDataPart.class);
        packetHandler.register(TerminalStorageIngredientUpdateActiveStorageIngredientPacket.class);
        packetHandler.register(TerminalStorageIngredientItemStackCraftingGridClear.class);
        packetHandler.register(TerminalStorageIngredientItemStackCraftingGridBalance.class);
        packetHandler.register(TerminalStorageIngredientItemStackCraftingGridSetResult.class);
        packetHandler.register(TerminalStorageIngredientItemStackCraftingGridShiftClickOutput.class);
        packetHandler.register(TerminalStorageIngredientItemStackCraftingGridSetAutoRefill.class);
        packetHandler.register(OpenCraftingJobsPlanGuiPacket.class);
        packetHandler.register(OpenCraftingJobsGuiPacket.class);
        packetHandler.register(CancelCraftingJobPacket.class);

        IntegratedDynamics.clog("Registered packet handler.");
    }

    @Override
    public void registerRenderers() {
        super.registerRenderers();

        GuiProviders.register();
    }
}
