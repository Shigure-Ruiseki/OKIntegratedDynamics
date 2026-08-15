package ruiseki.integrateddynamics.core.network.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * An event that is posted in the Forge event bus.
 * 
 * @author rubensworks
 */
public class NetworkInitializedEvent extends Event {

    private final INetwork network;
    private final World world;
    private final BlockPos pos;
    private final EntityLivingBase placer;

    public NetworkInitializedEvent(INetwork network, World world, BlockPos pos, @Nullable EntityLivingBase placer) {
        this.network = network;
        this.world = world;
        this.pos = pos;
        this.placer = placer;
    }

    public INetwork getNetwork() {
        return network;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Nullable
    public EntityLivingBase getPlacer() {
        return placer;
    }
}
