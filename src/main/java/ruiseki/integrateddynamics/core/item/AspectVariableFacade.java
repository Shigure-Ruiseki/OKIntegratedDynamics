package ruiseki.integrateddynamics.core.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IAspectVariableFacade;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Variable facade for variables determined by part aspects.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AspectVariableFacade extends VariableFacadeBase implements IAspectVariableFacade {

    private final int partId;
    private final IAspect aspect;

    public AspectVariableFacade(boolean generateId, int partId, IAspect aspect) {
        super(generateId);
        this.partId = partId;
        this.aspect = aspect;
    }

    public AspectVariableFacade(int id, int partId, IAspect aspect) {
        super(id);
        this.partId = partId;
        this.aspect = aspect;
    }

    @Override
    public <V extends IValue> IVariable<V> getVariable(INetwork network, IPartNetwork partNetwork) {
        if (isValid() && getAspect() instanceof IAspectRead
            && partNetwork.hasPartVariable(getPartId(), (IAspectRead<IValue, ?>) getAspect())) {
            return partNetwork.getPartVariable(getPartId(), (IAspectRead) getAspect());
        }
        return null;
    }

    @Override
    public boolean isValid() {
        return getPartId() >= 0 && getAspect() != null;
    }

    @Override
    public void validate(INetwork network, IPartNetwork partNetwork, IValidator validator,
        IValueType containingValueType) {
        if (!isValid()) {
            validator.addError(new LangHelpers.UnlocalizedString(L10NValues.VARIABLE_ERROR_INVALIDITEM));
        } else if (!(getAspect() instanceof IAspectRead
            && partNetwork.hasPartVariable(getPartId(), (IAspectRead<IValue, ?>) getAspect()))) {
                validator.addError(
                    new LangHelpers.UnlocalizedString(
                        L10NValues.VARIABLE_ERROR_PARTNOTINNETWORK,
                        Integer.toString(getPartId())));
            } else if (!ValueHelpers.correspondsTo(containingValueType, getAspect().getValueType())) {
                validator.addError(
                    new LangHelpers.UnlocalizedString(
                        L10NValues.ASPECT_ERROR_INVALIDTYPE,
                        new LangHelpers.UnlocalizedString(containingValueType.getUnlocalizedName()),
                        new LangHelpers.UnlocalizedString(
                            getAspect().getValueType()
                                .getUnlocalizedName())));
            }
    }

    @Override
    public IValueType getOutputType() {
        IAspect aspect = getAspect();
        if (aspect == null) return null;
        return aspect.getValueType();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(List<String> list, EntityPlayer entityPlayer) {
        if (isValid()) {
            getAspect().loadTooltip(list, false);
            list.add(LangHelpers.localize(L10NValues.ASPECT_TOOLTIP_PARTID, getPartId()));
        }
        super.addInformation(list, entityPlayer);
    }
}
