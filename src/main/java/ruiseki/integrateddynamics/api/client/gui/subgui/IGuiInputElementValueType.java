package ruiseki.integrateddynamics.api.client.gui.subgui;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;

import com.google.common.base.Predicate;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;

/**
 * A value type element inside the logic programmer.
 *
 * @param <G> The type of gui.
 * @param <C> The type of container.
 * @param <S> The sub gui box type.
 * @author rubensworks
 */
public interface IGuiInputElementValueType<S extends ISubGuiBox, G extends Gui, C extends Container>
    extends IGuiInputElement<S, G, C> {

    public void setValidator(Predicate<IValue> validator);

    public IValue getValue();

    void setValue(IValue value, S propertyConfigPattern);
}
