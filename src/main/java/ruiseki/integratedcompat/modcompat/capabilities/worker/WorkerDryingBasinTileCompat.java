package ruiseki.integratedcompat.modcompat.capabilities.worker;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.capability.work.IWorker;
import ruiseki.commoncapabilities.capability.worker.WorkerConfig;
import ruiseki.integrateddynamics.tileentity.TileDryingBasin;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.SimpleCapabilityConstructor;

/**
 * Compatibility for drying basin worker capability.
 *
 * @author rubensworks
 */
public class WorkerDryingBasinTileCompat extends SimpleCapabilityConstructor<IWorker, TileDryingBasin> {

    @Override
    public Capability<IWorker> getCapability() {
        return WorkerConfig.CAPABILITY;
    }

    @Nullable
    @Override
    public ICapabilityProvider createProvider(TileDryingBasin host) {
        return new DefaultCapabilityProvider<>(() -> WorkerConfig.CAPABILITY, new Worker(host));
    }

    public static class Worker implements IWorker {

        private final TileDryingBasin provider;

        public Worker(TileDryingBasin provider) {
            this.provider = provider;
        }

        @Override
        public boolean hasWork() {
            return provider.getCurrentRecipe() != null;
        }

        @Override
        public boolean canWork() {
            return true;
        }
    }
}
