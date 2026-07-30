package ruiseki.integrateddynamics.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.core.persist.world.NetworkWorldStorage;

/**
 * Handles server ticks to delegate to networks.
 * 
 * @author rubensworks
 */
public final class TickHandler {

    private static TickHandler INSTANCE;

    private TickHandler() {

    }

    public static TickHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TickHandler();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (event.type == TickEvent.Type.SERVER && event.phase == TickEvent.Phase.END) {
            for (INetwork<?> network : NetworkWorldStorage.getInstance(IntegratedDynamics._instance)
                .getNetworks()) {
                network.update();
            }
        }
    }

}
