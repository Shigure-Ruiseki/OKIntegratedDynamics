package ruiseki.integrateddynamics.core.part.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;

/**
 * An event that is posted in the Forge event bus when a write aspect is enabled by a player.
 * 
 * @author rubensworks
 */
public class PartWriterAspectEvent<P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>, A extends IAspectWrite>
    extends PartAspectEvent<P, S, A> {

    private final ItemStack itemStack;

    public PartWriterAspectEvent(INetwork network, IPartNetwork partNetwork, PartTarget target, P partType, S partState,
        @Nullable EntityPlayer entityPlayer, A aspect, ItemStack itemStack) {
        super(network, partNetwork, target, partType, partState, entityPlayer, aspect);
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
