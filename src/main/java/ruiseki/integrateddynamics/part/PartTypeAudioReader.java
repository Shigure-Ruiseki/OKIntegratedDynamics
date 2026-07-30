package ruiseki.integrateddynamics.part;

import ruiseki.integrateddynamics.core.part.read.PartStateReaderBase;
import ruiseki.integrateddynamics.core.part.read.PartTypeReadBase;

/**
 * An audio reader part.
 *
 * @author rubensworks
 */
public class PartTypeAudioReader
    extends PartTypeReadBase<PartTypeAudioReader, PartStateReaderBase<PartTypeAudioReader>> {

    public PartTypeAudioReader(String name) {
        super(name);
        // TODO: add forInstrument
        // AspectRegistry.getInstance()
        // .register(
        // this,
        // Sets.<IAspect>newHashSet(
        // Aspects.Read.Audio.INTEGER_PIANO_NOTE,
        // Aspects.Read.Audio.INTEGER_BASSDRUM_NOTE,
        // Aspects.Read.Audio.INTEGER_SNARE_NOTE,
        // Aspects.Read.Audio.INTEGER_CLICKS_NOTE,
        // Aspects.Read.Audio.INTEGER_BASSGUITAR_NOTE));
    }

    @Override
    public PartStateReaderBase<PartTypeAudioReader> constructDefaultState() {
        return new PartStateReaderBase<PartTypeAudioReader>();
    }

}
