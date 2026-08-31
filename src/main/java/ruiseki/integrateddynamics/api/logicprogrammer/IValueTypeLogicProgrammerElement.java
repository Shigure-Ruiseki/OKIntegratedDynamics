package ruiseki.integrateddynamics.api.logicprogrammer;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.client.gui.subgui.IGuiInputElementValueType;
import ruiseki.integrateddynamics.api.client.gui.subgui.ISubGuiBox;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;

/**
 * An element instantiation of a value type inside the logic programmer.
 *
 * @param <G> The type of gui.
 * @param <C> The type of container.
 * @param <S> The sub gui box type.
 * @author rubensworks
 */
public interface IValueTypeLogicProgrammerElement<S extends ISubGuiBox, G extends Gui, C extends Container>
    extends ILogicProgrammerElement<S, G, C> {

    /**
     * @return The value type of this element.
     */
    public IValueType<?> getValueType();

    /**
     * @return The current value.
     */
    public IValue getValue();

    /**
     * @param value The new value.
     */
    public void setValue(IValue value);

    /**
     * @return Create an inner gui element for modifying the value, may be null if it doesn't apply.
     * @param <G2> The type of gui.
     * @param <C2> The type of container.
     */
    @Nullable
    public <G2 extends Gui, C2 extends Container> IGuiInputElementValueType<?, G2, C2> createInnerGuiElement();
}
