package ruiseki.integrateddynamics.api.client.gui.subgui;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;

import com.google.common.base.Predicate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

    public void setValue(IValue value);

    /**
     * Set the currently stored value in the given sub gui.
     * This is useful when the gui is reused for multiple elements where the actual value is stored in this element.
     * 
     * @param subGui       The sub gui to put the currently stored value in.
     * @param sendToServer If the value must be sent to the server.
     */
    @SideOnly(Side.CLIENT)
    public void setValueInGui(S subGui, boolean sendToServer);
}
