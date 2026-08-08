package ruiseki.integrateddynamics.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import com.mojang.brigadier.context.CommandContext;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.network.packet.NetworkDiagnosticsOpenClient;
import ruiseki.okcore.command.CommandMod;
import ruiseki.okcore.init.ModBase;

public class CommandNetworkDiagnostics extends CommandMod {

    public static final String NAME = "networkdiagnostics";

    public CommandNetworkDiagnostics(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public int run(CommandContext<ICommandSender> context) {
        if (context.getSource() instanceof EntityPlayerMP playerMP) {
            IntegratedDynamics._instance.getPacketHandler()
                .sendToPlayer(new NetworkDiagnosticsOpenClient(), playerMP);
        }
        return SINGLE_SUCCESS;
    }
}
