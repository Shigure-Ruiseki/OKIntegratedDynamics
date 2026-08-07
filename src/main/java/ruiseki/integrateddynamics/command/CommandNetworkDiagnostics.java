package ruiseki.integrateddynamics.command;

import net.minecraft.command.ICommandSender;

import com.mojang.brigadier.context.CommandContext;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.network.diagnostics.GuiNetworkDiagnostics;
import ruiseki.integrateddynamics.network.packet.NetworkDiagnosticsSubscribePacket;
import ruiseki.okcore.command.CommandMod;
import ruiseki.okcore.init.ModBase;

public class CommandNetworkDiagnostics extends CommandMod {

    public static final String NAME = "networkdiagnostics";

    public CommandNetworkDiagnostics(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public int run(CommandContext<ICommandSender> context) {
        startDiagnosticsThread();
        return SINGLE_SUCCESS;
    }

    private void startDiagnosticsThread() {
        new Thread(() -> {
            try {
                GuiNetworkDiagnostics.clearNetworkData();

                IntegratedDynamics._instance.getPacketHandler()
                    .sendToServer(NetworkDiagnosticsSubscribePacket.subscribe());

                GuiNetworkDiagnostics gui = new GuiNetworkDiagnostics();
                gui.start();
            } catch (Exception e) {
                getMod().getLoggerHelper()
                    .getLogger()
                    .info("Failed to start Network Diagnostics GUI", e);
            }
        }, "IntegratedDynamics-NetworkDiagnostics").start();
    }
}
