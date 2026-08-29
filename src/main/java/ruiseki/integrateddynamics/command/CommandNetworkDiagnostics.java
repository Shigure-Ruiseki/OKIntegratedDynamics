package ruiseki.integrateddynamics.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.network.packet.NetworkDiagnosticsTriggerClient;
import ruiseki.okcore.command.CommandMod;
import ruiseki.okcore.command.argument.ArgumentTypeEnum;
import ruiseki.okcore.init.ModBase;

public class CommandNetworkDiagnostics extends CommandMod implements Command<ICommandSender> {

    public static final String NAME = "networkdiagnostics";

    public CommandNetworkDiagnostics(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public int run(CommandContext<ICommandSender> context) {
        context.getSource()
            .addChatMessage(
                new ChatComponentText("§cUsage: /integrateddynamics networkdiagnostics <start|stop> [port]"));
        return 0;
    }

    private int executeCommand(CommandContext<ICommandSender> context, boolean operationArg, boolean hasPortArg) {
        if (context.getSource() instanceof EntityPlayerMP playerMP) {
            StartStop operation = operationArg ? ArgumentTypeEnum.getValue(context, "operation", StartStop.class)
                : StartStop.START;
            int port = hasPortArg ? IntegerArgumentType.getInteger(context, "port")
                : GeneralConfig.diagnosticsWebServerPort;

            IntegratedDynamics._instance.getPacketHandler()
                .sendToPlayer(new NetworkDiagnosticsTriggerClient(operation == StartStop.START, port), playerMP);
        }
        return SINGLE_SUCCESS;
    }

    @Override
    public LiteralArgumentBuilder<ICommandSender> make() {
        return super.make().requires(commandSource -> commandSource.canCommandSenderUseCommand(2, getCommandName()))
            .then(
                RequiredArgumentBuilder
                    .<ICommandSender, StartStop>argument("operation", new ArgumentTypeEnum<>(StartStop.class))
                    .executes(context -> executeCommand(context, false, false))
                    .then(
                        RequiredArgumentBuilder.<ICommandSender, Integer>argument("port", IntegerArgumentType.integer())
                            .executes(context -> executeCommand(context, true, true))));
    }

    public enum StartStop {
        START,
        STOP
    }
}
