package ruiseki.integrateddynamics.part;

import com.google.common.collect.Sets;

import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.read.PartStateReaderBase;
import ruiseki.integrateddynamics.core.part.read.PartTypeReadBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integrateddynamics.part.aspect.read.redstone.IReadRedstoneComponent;
import ruiseki.integrateddynamics.part.aspect.read.redstone.ReadRedstoneComponent;

/**
 * A redstone reader part.
 * 
 * @author rubensworks
 */
public class PartTypeRedstoneReader
    extends PartTypeReadBase<PartTypeRedstoneReader, PartStateReaderBase<PartTypeRedstoneReader>> {

    private static final IReadRedstoneComponent READ_REDSTONE_COMPONENT = new ReadRedstoneComponent();

    public PartTypeRedstoneReader(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Sets.<IAspect>newHashSet(
                    Aspects.Read.Redstone.BOOLEAN_LOW,
                    Aspects.Read.Redstone.BOOLEAN_NONLOW,
                    Aspects.Read.Redstone.BOOLEAN_HIGH,
                    Aspects.Read.Redstone.BOOLEAN_CLOCK,
                    Aspects.Read.Redstone.INTEGER_VALUE,
                    Aspects.Read.Redstone.INTEGER_COMPARATOR));
    }

    @Override
    public PartStateReaderBase<PartTypeRedstoneReader> constructDefaultState() {
        return new PartStateReaderBase<PartTypeRedstoneReader>();
    }

    @Override
    public void onNetworkAddition(IPartNetwork network, PartTarget target,
        PartStateReaderBase<PartTypeRedstoneReader> state) {
        super.onNetworkAddition(network, target, state);
        READ_REDSTONE_COMPONENT.setAllowRedstoneInput(target, true);
    }

    @Override
    public void onNetworkRemoval(IPartNetwork network, PartTarget target,
        PartStateReaderBase<PartTypeRedstoneReader> state) {
        super.onNetworkRemoval(network, target, state);
        READ_REDSTONE_COMPONENT.setAllowRedstoneInput(target, false);
    }

}
