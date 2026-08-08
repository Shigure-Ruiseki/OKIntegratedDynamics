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
import ruiseki.integrateddynamics.api.item.IProxyVariableFacade;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.tileentity.TileProxy;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.TileHelpers;

/**
 * Variable facade for variables determined by proxies.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProxyVariableFacade extends VariableFacadeBase implements IProxyVariableFacade {

    private final int proxyId;
    private boolean isValidatingVariable = false;
    private boolean isGettingVariable = false;

    public ProxyVariableFacade(boolean generateId, int proxyId) {
        super(generateId);
        this.proxyId = proxyId;
    }

    public ProxyVariableFacade(int id, int proxyId) {
        super(id);
        this.proxyId = proxyId;
    }

    protected TileProxy getProxy(IPartNetwork network) {
        DimPos dimPos = network.getProxy(proxyId);
        if (dimPos != null) {
            return TileHelpers.getSafeTile(dimPos.getWorld(), dimPos.getBlockPos(), TileProxy.class);
        }
        return null;
    }

    protected IVariable getTargetVariable(IPartNetwork network) {
        TileProxy tile = getProxy(network);
        if (tile != null) {
            IVariable variable = tile.getVariable(network);
            return variable;
        }
        return null;
    }

    @Override
    public <V extends IValue> IVariable<V> getVariable(IPartNetwork network) {
        if (isValid()) {
            // Check if we are entering an infinite recursion (e.g. proxies refering to each other)
            if (this.isGettingVariable) {
                throw new VariableRecursionException("Detected infinite recursion for variable references.");
            }
            this.isGettingVariable = true;
            IVariable<V> variable = getTargetVariable(network);
            this.isGettingVariable = false;
            return variable;
        }
        return null;
    }

    @Override
    public boolean isValid() {
        return proxyId >= 0;
    }

    @Override
    public void validate(IPartNetwork network, IValidator validator, IValueType containingValueType) {
        if (!isValid()) {
            validator.addError(new LangHelpers.UnlocalizedString(L10NValues.VARIABLE_ERROR_INVALIDITEM));
        } else if (network.getProxy(proxyId) == null) {
            validator.addError(
                new LangHelpers.UnlocalizedString(L10NValues.PROXY_ERROR_PROXYNOTINNETWORK, Integer.toString(proxyId)));
        } else if (getTargetVariable(network) == null) {
            validator.addError(
                new LangHelpers.UnlocalizedString(L10NValues.PROXY_ERROR_PROXYINVALID, Integer.toString(proxyId)));
        } else if (!ValueHelpers.correspondsTo(containingValueType, getTargetVariable(network).getType())) {
            validator.addError(
                new LangHelpers.UnlocalizedString(
                    L10NValues.PROXY_ERROR_PROXYINVALIDTYPE,
                    new LangHelpers.UnlocalizedString(containingValueType.getUnlocalizedName()),
                    new LangHelpers.UnlocalizedString(
                        getTargetVariable(network).getType()
                            .getUnlocalizedName())));
        }

        // Check if we are entering an infinite recursion (e.g. proxies refering to each other)
        if (this.isValidatingVariable) {
            throw new VariableRecursionException("Detected infinite recursion for variable references.");
        }
        this.isValidatingVariable = true;
        getVariable(network);
        this.isValidatingVariable = false;
    }

    @Override
    public IValueType getOutputType() {
        return ValueTypes.CATEGORY_ANY;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(List<String> list, EntityPlayer entityPlayer) {
        if (isValid()) {
            list.add(LangHelpers.localize(L10NValues.PROXY_TOOLTIP_PROXYID, proxyId));
        }
        super.addInformation(list, entityPlayer);
    }

    public static class VariableRecursionException extends IllegalArgumentException {

        public VariableRecursionException(String msg) {
            super(msg);
        }

    }
}
