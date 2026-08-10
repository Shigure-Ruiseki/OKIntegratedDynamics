package ruiseki.integrateddynamics.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.network.diagnostics.NetworkDiagnostics;
import ruiseki.integrateddynamics.core.persist.world.NetworkWorldStorage;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Handles server ticks to delegate to networks.
 *
 * @author rubensworks
 */
public final class TickHandler {

    private static TickHandler INSTANCE;
    private int tick = 0;
    private boolean shouldCrash = false;

    private TickHandler() {

    }

    public static TickHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TickHandler();
        }
        return INSTANCE;
    }

    public void setShouldCrash() {
        this.shouldCrash = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (event.type == TickEvent.Type.SERVER && event.phase == TickEvent.Phase.END && NetworkHelpers.shouldWork()) {
            if (shouldCrash) {
                throw new RuntimeException("Forcefully crashed the server.");
            }

            boolean isBeingDiagnozed = NetworkDiagnostics.getInstance()
                .isBeingDiagnozed();
            if (isBeingDiagnozed) {
                tick = (tick + 1) % MinecraftHelpers.SECOND_IN_TICKS;
            }
            boolean shouldSendTickDurationInfo = isBeingDiagnozed && tick == 0;
            for (INetwork network : NetworkWorldStorage.getInstance(IntegratedDynamics._instance)
                .getNetworks()) {
                if (isBeingDiagnozed && (shouldSendTickDurationInfo || network.hasChanged())) {
                    NetworkDiagnostics.getInstance()
                        .sendNetworkUpdate(network);
                    network.resetLastSecondDurations();
                }
                try {
                    if (!network.isCrashed()) {
                        network.update();
                    }
                } catch (Throwable e) {
                    network.setCrashed(true);
                    throw e;
                }
            }
        }
    }

}
