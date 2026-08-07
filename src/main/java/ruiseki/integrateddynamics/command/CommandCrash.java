package ruiseki.integrateddynamics.command;

import net.minecraft.command.ICommandSender;

import org.apache.logging.log4j.Level;

import com.mojang.brigadier.context.CommandContext;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.TickHandler;
import ruiseki.okcore.command.CommandMod;
import ruiseki.okcore.init.ModBase;

/**
 * A command to let the server crash.
 * 
 * @author rubensworks
 *
 */
public class CommandCrash extends CommandMod {

    public static final String NAME = "crash";

    public CommandCrash(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public int run(CommandContext<ICommandSender> context) {
        ICommandSender sender = context.getSource();
        IntegratedDynamics.clog(Level.WARN, sender.getCommandSenderName() + " initialized a server crash.");
        TickHandler.getInstance()
            .setShouldCrash();
        return SINGLE_SUCCESS;
    }
}
