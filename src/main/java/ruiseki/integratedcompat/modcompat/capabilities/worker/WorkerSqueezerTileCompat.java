package ruiseki.integratedcompat.modcompat.capabilities.worker;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.capability.work.IWorker;
import ruiseki.commoncapabilities.capability.worker.WorkerConfig;
import ruiseki.integrateddynamics.tileentity.TileSqueezer;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.SimpleCapabilityConstructor;

/**
 * Compatibility for squeezer worker capability.
 *
 * @author rubensworks
 */
public class WorkerSqueezerTileCompat extends SimpleCapabilityConstructor<IWorker, TileSqueezer> {

    @Override
    public Capability<IWorker> getCapability() {
        return WorkerConfig.CAPABILITY;
    }

    @Nullable
    @Override
    public ICapabilityProvider createProvider(TileSqueezer host) {
        return new DefaultCapabilityProvider<>(() -> WorkerConfig.CAPABILITY, new Worker(host));
    }

    public static class Worker implements IWorker {

        private final TileSqueezer provider;

        public Worker(TileSqueezer provider) {
            this.provider = provider;
        }

        @Override
        public boolean hasWork() {
            return provider.getCurrentRecipe() != null;
        }

        @Override
        public boolean canWork() {
            return false;
        }
    }
}
