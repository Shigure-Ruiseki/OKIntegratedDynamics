package ruiseki.integrateddynamics.part;

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

    @Override
    public boolean forceLightTransparency(State state) {
        return true;
    }

    public static class State
        extends PartTypePanelVariableDriven.State<PartTypePanelDisplay, PartTypePanelDisplay.State> {

    }

}
