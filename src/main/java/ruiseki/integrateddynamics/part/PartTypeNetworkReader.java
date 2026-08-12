package ruiseki.integrateddynamics.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.read.PartStateReaderBase;
import ruiseki.integrateddynamics.core.part.read.PartTypeReadBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * A reader part that can read aspects from the network it is contained in.
 *
 * @author rubensworks
 */
public class PartTypeNetworkReader
    extends PartTypeReadBase<PartTypeNetworkReader, PartStateReaderBase<PartTypeNetworkReader>> {

    public PartTypeNetworkReader(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    Aspects.Read.Network.BOOLEAN_APPLICABLE,
                    Aspects.Read.Network.INTEGER_ELEMENT_COUNT,
                    Aspects.Read.Network.INTEGER_ENERGY_BATTERY_COUNT,
                    Aspects.Read.Network.INTEGER_ENERGY_STORED,
                    Aspects.Read.Network.INTEGER_ENERGY_MAX,
                    Aspects.Read.Network.INTEGER_ENERGY_CONSUMPTION_RATE,
                    Aspects.Read.Network.ANY_VALUE));
    }

    @Override
    public PartStateReaderBase<PartTypeNetworkReader> constructDefaultState() {
        return new PartStateReaderBase<PartTypeNetworkReader>();
    }

    @Override
    public int getConsumptionRate(PartStateReaderBase<PartTypeNetworkReader> state) {
        return GeneralConfig.networkReaderBaseConsumption;
    }
}
