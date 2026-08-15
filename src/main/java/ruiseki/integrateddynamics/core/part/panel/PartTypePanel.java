package ruiseki.integrateddynamics.core.part.panel;

import net.minecraft.block.Block;

import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.core.block.IgnoredBlockStatus;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * A base part that is flat and can be used to render things on.
 *
 * @author rubensworks
 */
public abstract class PartTypePanel<P extends PartTypePanel<P, S>, S extends IPartState<P>> extends PartTypeBase<P, S> {

    public PartTypePanel(String name) {
        super(name, new PartRenderPosition(0.125F, 0.1875F, 0.625F, 0.625F));
    }

    @Override
    protected Block createBlock(BlockConfig blockConfig) {
        return new IgnoredBlockStatus(blockConfig);
    }

}
