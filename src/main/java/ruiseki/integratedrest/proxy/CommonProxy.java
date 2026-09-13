package ruiseki.integratedrest.proxy;

import ruiseki.integratedrest.IntegratedRest;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.CommonProxyComponent;

/**
 * Proxy for server and client side.
 * 
 * @author rubensworks
 *
 */
public class CommonProxy extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return IntegratedRest._instance;
    }

}
