package ruiseki.integrateddynamics.part;

import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.core.part.panel.PartTypePanelVariableDriven;

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
