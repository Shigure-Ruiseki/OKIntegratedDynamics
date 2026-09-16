package ruiseki.integrateddynamics.core.evaluate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.item.IProxyVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandler;
import ruiseki.integrateddynamics.core.item.ProxyVariableFacade;

/**
 * Handler for proxy variable facades.
 *
 * @author rubensworks
 */
public class ProxyVariableFacadeHandler implements IVariableFacadeHandler<IProxyVariableFacade> {

    private static final IProxyVariableFacade INVALID_FACADE = new ProxyVariableFacade(false, -1);
    private static ProxyVariableFacadeHandler _instance;

    private ProxyVariableFacadeHandler() {

    }

    public static ProxyVariableFacadeHandler getInstance() {
        if (_instance == null) _instance = new ProxyVariableFacadeHandler();
        return _instance;
    }

    @Override
    public ResourceLocation getUniqueName() {
        return new ResourceLocation(Reference.MOD_ID, "proxy");
    }

    @Override
    public IProxyVariableFacade getVariableFacade(int id, NBTTagCompound tag) {
        if (!tag.hasKey("partId")) {
            return INVALID_FACADE;
        }
        return new ProxyVariableFacade(id, tag.getInteger("partId"));
    }

    @Override
    public void setVariableFacade(NBTTagCompound tag, IProxyVariableFacade variableFacade) {
        tag.setInteger("partId", variableFacade.getProxyId());
    }
}
