package ruiseki.integratedcompat.modcompat.capabilities.worker;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.capability.work.IWorker;
import ruiseki.commoncapabilities.capability.worker.WorkerConfig;
import ruiseki.integrateddynamics.block.BlockCoalGeneratorConfig;
import ruiseki.integrateddynamics.tileentity.TileCoalGenerator;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.SimpleCapabilityConstructor;

/**
 * Compatibility for coal generator worker capability.
 *
 * @author rubensworks
 */
public class WorkerCoalGeneratorTileCompat extends SimpleCapabilityConstructor<IWorker, TileCoalGenerator> {

    @Override
    public Capability<IWorker> getCapability() {
        return WorkerConfig.CAPABILITY;
    }

    @Nullable
    @Override
    public ICapabilityProvider createProvider(TileCoalGenerator host) {
        return new DefaultCapabilityProvider<>(() -> WorkerConfig.CAPABILITY, new Worker(host));
    }

    public static class Worker implements IWorker {

        private final TileCoalGenerator provider;

        public Worker(TileCoalGenerator provider) {
            this.provider = provider;
        }

        @Override
        public boolean hasWork() {
            return provider.getStackInSlot(TileCoalGenerator.SLOT_FUEL) != null || provider.isBurning();
        }

        @Override
        public boolean canWork() {
            return provider.canAddEnergy(BlockCoalGeneratorConfig.energyPerTick);
        }
    }
}
