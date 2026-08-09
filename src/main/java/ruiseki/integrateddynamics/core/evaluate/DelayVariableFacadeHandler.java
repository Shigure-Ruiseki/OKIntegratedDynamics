package ruiseki.integrateddynamics.core.evaluate;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.integrateddynamics.api.item.IDelayVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandler;
import ruiseki.integrateddynamics.core.item.DelayVariableFacade;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Handler for delay variable facades.
 *
 * @author rubensworks
 */
public class DelayVariableFacadeHandler implements IVariableFacadeHandler<IDelayVariableFacade> {

    private static final IDelayVariableFacade INVALID_FACADE = new DelayVariableFacade(false, -1);
    private static DelayVariableFacadeHandler _instance;

    private DelayVariableFacadeHandler() {

    }

    public static DelayVariableFacadeHandler getInstance() {
        if (_instance == null) _instance = new DelayVariableFacadeHandler();
        return _instance;
    }

    @Override
    public String getTypeId() {
        return "delay";
    }

    @Override
    public IDelayVariableFacade getVariableFacade(int id, NBTTagCompound tag) {
        if (!tag.hasKey("partId", MinecraftHelpers.NBTTag_Types.NBTTagInt.ordinal())) {
            return INVALID_FACADE;
        }
        return new DelayVariableFacade(id, tag.getInteger("partId"));
    }

    @Override
    public void setVariableFacade(NBTTagCompound tag, IDelayVariableFacade variableFacade) {
        tag.setInteger("partId", variableFacade.getProxyId());
    }
}
