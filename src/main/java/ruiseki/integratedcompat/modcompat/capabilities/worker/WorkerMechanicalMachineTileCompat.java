package ruiseki.integratedcompat.modcompat.capabilities.worker;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.capability.work.IWorker;
import ruiseki.commoncapabilities.capability.worker.WorkerConfig;
import ruiseki.integrateddynamics.core.tileentity.TileMechanicalMachine;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.SimpleCapabilityConstructor;

/**
 * Compatibility for a mechanical machine worker capability.
 *
 * @author rubensworks
 */
public class WorkerMechanicalMachineTileCompat<T extends TileMechanicalMachine<?, ?>>
    extends SimpleCapabilityConstructor<IWorker, T> {

    @Override
    public Capability<IWorker> getCapability() {
        return WorkerConfig.CAPABILITY;
    }

    @Nullable
    @Override
    public ICapabilityProvider createProvider(T host) {
        return new DefaultCapabilityProvider<>(() -> WorkerConfig.CAPABILITY, new Worker<>(host));
    }

    public static class Worker<T extends TileMechanicalMachine<?, ?>> implements IWorker {

        private final T provider;

        public Worker(T provider) {
            this.provider = provider;
        }

        @Override
        public boolean hasWork() {
            return provider.hasWork();
        }

        @Override
        public boolean canWork() {
            return provider.canWork();
        }
    }
}
