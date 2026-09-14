package ruiseki.integratedrest.evaluate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandler;
import ruiseki.integratedrest.api.item.IHttpVariableFacade;
import ruiseki.integratedrest.item.HttpVariableFacade;

/**
 * Handler for http variable facades.
 *
 * @author rubensworks
 */
public class HttpVariableFacadeHandler implements IVariableFacadeHandler<IHttpVariableFacade> {

    private static final IHttpVariableFacade INVALID_FACADE = new HttpVariableFacade(false, -1);
    private static HttpVariableFacadeHandler _instance;

    private HttpVariableFacadeHandler() {

    }

    public static HttpVariableFacadeHandler getInstance() {
        if (_instance == null) _instance = new HttpVariableFacadeHandler();
        return _instance;
    }

    @Override
    public ResourceLocation getUniqueName() {
        return new ResourceLocation(Reference.MOD_ID, "http");
    }

    @Override
    public IHttpVariableFacade getVariableFacade(int id, NBTTagCompound tag) {
        if (!tag.hasKey("partId", Constants.NBT.TAG_INT)) {
            return INVALID_FACADE;
        }
        return new HttpVariableFacade(id, tag.getInteger("partId"));
    }

    @Override
    public void setVariableFacade(NBTTagCompound tag, IHttpVariableFacade variableFacade) {
        tag.setInteger("partId", variableFacade.getProxyId());
    }
}
