package ruiseki.integrateddynamics.core.logicprogrammer.event;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.item.IVariableFacade;

/**
 * A logic programmer event that is posted in the Forge event bus when a new variable is created.
 *
 * @author rubensworks
 */
public class LogicProgrammerVariableFacadeCreatedEvent extends LogicProgrammerEvent {

    private final IVariableFacade variableFacade;
    @Nullable
    private final Block block;

    public LogicProgrammerVariableFacadeCreatedEvent(EntityPlayer player, IVariableFacade variableFacade, Block block) {
        super(player);
        this.variableFacade = variableFacade;
        this.block = block;
    }

    public IVariableFacade getVariableFacade() {
        return variableFacade;
    }

    @Nullable
    public Block getBlock() {
        return block;
    }
}
