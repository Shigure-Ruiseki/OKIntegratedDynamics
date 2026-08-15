package ruiseki.integrateddynamics.core.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.item.IDelayVariableFacade;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Variable facade for variables determined by delays.
 * 
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DelayVariableFacade extends ProxyVariableFacade implements IDelayVariableFacade {

    public DelayVariableFacade(boolean generateId, int proxyId) {
        super(generateId, proxyId);
    }

    public DelayVariableFacade(int id, int proxyId) {
        super(id, proxyId);
    }

    protected LangHelpers.UnlocalizedString getProxyNotInNetworkError() {
        return new LangHelpers.UnlocalizedString(
            L10NValues.DELAY_ERROR_DELAYNOTINNETWORK,
            Integer.toString(getProxyId()));
    }

    protected LangHelpers.UnlocalizedString getProxyInvalidError() {
        return new LangHelpers.UnlocalizedString(L10NValues.DELAY_ERROR_DELAYINVALID, Integer.toString(getProxyId()));
    }

    protected LangHelpers.UnlocalizedString getProxyInvalidTypeError(IPartNetwork network,
        IValueType containingValueType, IValueType actualType) {
        return new LangHelpers.UnlocalizedString(
            L10NValues.DELAY_ERROR_DELAYINVALIDTYPE,
            new LangHelpers.UnlocalizedString(containingValueType.getUnlocalizedName()),
            new LangHelpers.UnlocalizedString(actualType.getUnlocalizedName()));
    }

    protected String getProxyTooltip() {
        return LangHelpers.localize(L10NValues.DELAY_TOOLTIP_DELAYID, getProxyId());
    }
}
