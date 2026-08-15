package ruiseki.integrateddynamics.part;

import com.google.common.collect.Sets;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.read.PartStateReaderBase;
import ruiseki.integrateddynamics.core.part.read.PartTypeReadBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * An audio reader part.
 *
 * @author rubensworks
 */
public class PartTypeAudioReader
    extends PartTypeReadBase<PartTypeAudioReader, PartStateReaderBase<PartTypeAudioReader>> {

    public PartTypeAudioReader(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Sets.<IAspect>newHashSet(
                    Aspects.Read.Audio.INTEGER_PIANO_NOTE,
                    Aspects.Read.Audio.INTEGER_BASSDRUM_NOTE,
                    Aspects.Read.Audio.INTEGER_SNARE_NOTE,
                    Aspects.Read.Audio.INTEGER_CLICKS_NOTE,
                    Aspects.Read.Audio.INTEGER_BASSGUITAR_NOTE));
    }

    @Override
    public PartStateReaderBase<PartTypeAudioReader> constructDefaultState() {
        return new PartStateReaderBase<PartTypeAudioReader>();
    }

    @Override
    public int getConsumptionRate(PartStateReaderBase<PartTypeAudioReader> state) {
        return GeneralConfig.audioReaderBaseConsumption;
    }
}
