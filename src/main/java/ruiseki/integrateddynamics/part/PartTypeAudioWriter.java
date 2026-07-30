package ruiseki.integrateddynamics.part;

import com.google.common.collect.Sets;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.write.PartStateWriterBase;
import ruiseki.integrateddynamics.core.part.write.PartTypeWriteBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * An audio writer part.
 * 
 * @author rubensworks
 */
public class PartTypeAudioWriter
    extends PartTypeWriteBase<PartTypeAudioWriter, PartStateWriterBase<PartTypeAudioWriter>> {

    public PartTypeAudioWriter(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Sets.<IAspect>newHashSet(
                    Aspects.Write.Audio.INTEGER_PIANO_NOTE,
                    Aspects.Write.Audio.INTEGER_BASSDRUM_NOTE,
                    Aspects.Write.Audio.INTEGER_SNARE_NOTE,
                    Aspects.Write.Audio.INTEGER_CLICKS_NOTE,
                    Aspects.Write.Audio.INTEGER_BASSGUITAR_NOTE,
                    Aspects.Write.Audio.STRING_SOUND));
    }

    @Override
    public PartStateWriterBase<PartTypeAudioWriter> constructDefaultState() {
        return new PartStateWriterBase<PartTypeAudioWriter>(
            Aspects.REGISTRY.getAspects(this)
                .size());
    }

}
