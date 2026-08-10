package ruiseki.integrateddynamics.api;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.datastructure.DimPos;

/**
 * A runtime exception that can be thrown when a part is in an invalid state.
 *
 * @author rubensworks
 */
public class PartStateException extends IllegalArgumentException {

    public PartStateException(DimPos dimPos, ForgeDirection side) {
        super(
            String.format(
                "No part state for part at position %s side %s was found."
                    + "\nWorld loaded: %s\nChunk loaded: %s\nPart container: %s\nParts: %s",
                dimPos,
                side,
                DimensionManager.getWorld(dimPos.getDimensionId()) != null,
                dimPos.isLoaded(),
                dimPos.isLoaded() ? PartHelpers.getPartContainer(dimPos, side) : null,
                dimPos.isLoaded() && PartHelpers.getPartContainer(dimPos, side) != null
                    ? PartHelpers.getPartContainer(dimPos, side)
                        .getParts()
                    : null));
    }

}
