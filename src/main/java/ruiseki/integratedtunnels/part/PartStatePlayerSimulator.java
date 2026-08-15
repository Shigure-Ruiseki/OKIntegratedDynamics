package ruiseki.integratedtunnels.part;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integratedtunnels.core.FakePlayerHelpers;
import ruiseki.integratedtunnels.core.ItemStoragePlayerWrapper;
import ruiseki.integratedtunnels.core.part.PartStateRoundRobin;

/**
 * A part state for holding a temporary player inventory.
 * 
 * @author rubensworks
 */
public class PartStatePlayerSimulator extends PartStateRoundRobin<PartTypePlayerSimulator> {

    private FakePlayer player = null;

    public PartStatePlayerSimulator(int inventorySize) {
        super(inventorySize);
    }

    public @Nullable FakePlayer getPlayer() {
        return player;
    }

    public void update(PartTarget target) {
        World world = target.getTarget()
            .getPos()
            .getWorld();
        if (!world.isRemote) {
            if (player == null) {
                player = FakePlayerHelpers.initFakePlayer((WorldServer) world);
            }
            ItemStoragePlayerWrapper.cancelDestroyingBlock(player);
        }
    }

    @Override
    protected int getDefaultUpdateInterval() {
        return 10;
    }
}
