package ruiseki.integrateddynamics.part;

import net.minecraft.block.Block;

import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.core.block.IgnoredBlockStatus;
import ruiseki.integrateddynamics.core.part.panel.PartTypePanelVariableDriven;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * A part that can display variables.
 * 
 * @author rubensworks
 */
public class PartTypePanelDisplay
    extends PartTypePanelVariableDriven<PartTypePanelDisplay, PartTypePanelDisplay.State> {

    public PartTypePanelDisplay(String name) {
        super(name);
    }

    @Override
    public Class<? super PartTypePanelDisplay> getPartTypeClass() {
        return PartTypePanelDisplay.class;
    }

    @Override
    protected Block createBlock(BlockConfig blockConfig) {
        return new IgnoredBlockStatus(blockConfig);
    }

    @Override
    public PartTypePanelDisplay.State constructDefaultState() {
        return new PartTypePanelDisplay.State();
    }

    @Override
    public int getConsumptionRate(State state) {
        return state.hasVariable() ? 2 : 1;
    }

    public static class State
        extends PartTypePanelVariableDriven.State<PartTypePanelDisplay, PartTypePanelDisplay.State> {

        @Override
        public Class<? extends IPartState> getPartStateClass() {
            return PartTypePanelDisplay.State.class;
        }

    }

}
