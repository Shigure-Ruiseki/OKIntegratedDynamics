package ruiseki.integratedrest.item;

import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.core.item.ProxyVariableFacade;
import ruiseki.integratedrest.api.item.IHttpVariableFacade;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Variable facade for variables determined by http blocks.
 *
 * @author rubensworks
 */
public class HttpVariableFacade extends ProxyVariableFacade implements IHttpVariableFacade {

    public HttpVariableFacade(boolean generateId, int proxyId) {
        super(generateId, proxyId);
    }

    public HttpVariableFacade(int id, int proxyId) {
        super(id, proxyId);
    }

    @Override
    protected LangHelpers.UnlocalizedString getProxyInvalidTypeError(IPartNetwork network,
        IValueType containingValueType, IValueType actualType) {
        return new LangHelpers.UnlocalizedString(
            LangHelpers.localize(
                "http.integratedrest.error.http_invalid_type",
                LangHelpers.localize(containingValueType.getUnlocalizedName()),
                LangHelpers.localize(actualType.getUnlocalizedName())));
    }

    @Override
    protected LangHelpers.UnlocalizedString getProxyNotInNetworkError() {
        return new LangHelpers.UnlocalizedString(
            LangHelpers.localize("http.integratedrest.error.http_not_in_network", Integer.toString(getProxyId())));
    }

    @Override
    protected LangHelpers.UnlocalizedString getProxyInvalidError() {
        return new LangHelpers.UnlocalizedString(
            LangHelpers.localize("http.integratedrest.error.http_invalid", Integer.toString(getProxyId())));
    }

    protected String getProxyTooltip() {
        return LangHelpers.localize("http.integratedrest.tooltip.delay_id", getProxyId());
    }
}
