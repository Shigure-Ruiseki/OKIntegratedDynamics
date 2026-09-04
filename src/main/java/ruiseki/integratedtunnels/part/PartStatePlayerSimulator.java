package ruiseki.integratedtunnels.part;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedtunnels.core.ExtendedFakePlayer;
import ruiseki.integratedtunnels.core.ItemStoragePlayerWrapper;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;

/**
 * A part state for holding a temporary player inventory.
 *
 * @author rubensworks
 */
public class PartStatePlayerSimulator extends PartStateRoundRobin<PartTypePlayerSimulator> {

    private ExtendedFakePlayer player = null;

    public PartStatePlayerSimulator(int inventorySize) {
        super(inventorySize);
    }

    public @Nullable ExtendedFakePlayer getPlayer() {
        return player;
    }

    public void update(PartTarget target) {
        World world = target.getTarget()
            .getPos()
            .getWorld();
        if (!world.isRemote) {
            if (player == null) {
                player = new ExtendedFakePlayer((WorldServer) world);
            }
            ItemStoragePlayerWrapper.cancelDestroyingBlock(player);
        }
    }

    @Override
    protected int getDefaultUpdateInterval() {
        return 10;
    }
}
