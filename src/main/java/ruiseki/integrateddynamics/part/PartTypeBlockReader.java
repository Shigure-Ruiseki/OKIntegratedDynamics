package ruiseki.integrateddynamics.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.read.PartStateReaderBase;
import ruiseki.integrateddynamics.core.part.read.PartTypeReadBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * An block reader part.
 *
 * @author rubensworks
 */
public class PartTypeBlockReader
    extends PartTypeReadBase<PartTypeBlockReader, PartStateReaderBase<PartTypeBlockReader>> {

    public PartTypeBlockReader(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    Aspects.Read.Block.BOOLEAN_BLOCK,
                    Aspects.Read.Block.INTEGER_DIMENSION,
                    Aspects.Read.Block.INTEGER_POSX,
                    Aspects.Read.Block.INTEGER_POSY,
                    Aspects.Read.Block.INTEGER_POSZ,
                    Aspects.Read.Block.BLOCK,
                    Aspects.Read.Block.NBT,
                    Aspects.Read.Block.STRING_BIOME,
                    Aspects.Read.Block.INTEGER_LIGHT));
    }

    @Override
    public PartStateReaderBase<PartTypeBlockReader> constructDefaultState() {
        return new PartStateReaderBase<PartTypeBlockReader>();
    }

}
