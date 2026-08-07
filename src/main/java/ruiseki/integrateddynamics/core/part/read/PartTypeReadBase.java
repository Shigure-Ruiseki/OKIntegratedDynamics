package ruiseki.integrateddynamics.core.part.read;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.aspect.IAspectVariable;
import ruiseki.integrateddynamics.api.part.read.IPartStateReader;
import ruiseki.integrateddynamics.api.part.read.IPartTypeReader;
import ruiseki.integrateddynamics.client.gui.GuiPartReader;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartReader;
import ruiseki.integrateddynamics.core.part.PartTypeAspects;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * An abstract {@link IPartTypeReader}.
 *
 * @author rubensworks
 */
public abstract class PartTypeReadBase<P extends IPartTypeReader<P, S>, S extends IPartStateReader<P>>
    extends PartTypeAspects<P, S> implements IPartTypeReader<P, S> {

    public PartTypeReadBase(String name) {
        super(name, new PartRenderPosition(0.1875F, 0.3125F, 0.625F, 0.625F));
    }

    @Override
    public boolean isSolid(S state) {
        return true;
    }

    @Override
    public Class<? super P> getPartTypeClass() {
        return IPartTypeReader.class;
    }

    @Override
    public List<IAspectRead> getReadAspects() {
        return Aspects.REGISTRY.getReadAspects(this);
    }

    @Override
    public <V extends IValue, T extends IValueType<V>> IAspectVariable<V> getVariable(PartTarget target, S partState,
        IAspectRead<V, T> aspect) {
        if (!getAspects().contains(aspect)) {
            throw new IllegalArgumentException(
                String.format(
                    "Tried to get the variable for the aspect %s that did not exist within the " + "part type %s.",
                    aspect.getUnlocalizedName(),
                    this));
        }
        IAspectVariable<V> variable = partState.getVariable(aspect);
        if (variable == null) {
            variable = aspect.createNewVariable(target);
            partState.setVariable(aspect, variable);
        }
        return variable;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerPartReader.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Class<? extends GuiScreen> getGui() {
        return GuiPartReader.class;
    }

}
