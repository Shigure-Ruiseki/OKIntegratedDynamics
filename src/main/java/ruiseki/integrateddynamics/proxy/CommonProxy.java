package ruiseki.integrateddynamics.proxy;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.network.packet.ActionLabelPacket;
import ruiseki.integrateddynamics.core.network.packet.AllLabelsPacket;
import ruiseki.integrateddynamics.network.packet.ItemStackRenamePacket;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerActivateElementPacket;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerLabelPacket;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerSetElementInventory;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeBooleanValueChangedPacket;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeIngredientsValueChangedPacket;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeListValueChangedPacket;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeOperatorValueChangedPacket;
import ruiseki.integrateddynamics.network.packet.LogicProgrammerValueTypeStringValueChangedPacket;
import ruiseki.integrateddynamics.network.packet.NetworkDiagnosticsNetworkPacket;
import ruiseki.integrateddynamics.network.packet.NetworkDiagnosticsOpenClient;
import ruiseki.integrateddynamics.network.packet.NetworkDiagnosticsSubscribePacket;
import ruiseki.integrateddynamics.network.packet.PlayerTeleportPacket;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.network.PacketHandler;
import ruiseki.okcore.proxy.CommonProxyComponent;

public class CommonProxy extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return IntegratedDynamics._instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);

        // Register packets.
        packetHandler.register(LogicProgrammerActivateElementPacket.class);
        packetHandler.register(LogicProgrammerValueTypeStringValueChangedPacket.class);
        packetHandler.register(ActionLabelPacket.class);
        packetHandler.register(AllLabelsPacket.class);
        packetHandler.register(ItemStackRenamePacket.class);
        packetHandler.register(LogicProgrammerValueTypeListValueChangedPacket.class);
        packetHandler.register(LogicProgrammerLabelPacket.class);
        packetHandler.register(LogicProgrammerValueTypeOperatorValueChangedPacket.class);
        packetHandler.register(NetworkDiagnosticsSubscribePacket.class);
        packetHandler.register(NetworkDiagnosticsNetworkPacket.class);
        packetHandler.register(NetworkDiagnosticsOpenClient.class);
        packetHandler.register(PlayerTeleportPacket.class);
        packetHandler.register(LogicProgrammerSetElementInventory.class);
        packetHandler.register(LogicProgrammerValueTypeIngredientsValueChangedPacket.class);
        packetHandler.register(LogicProgrammerValueTypeBooleanValueChangedPacket.class);

        IntegratedDynamics.clog("Registered packet handler.");
    }
}
