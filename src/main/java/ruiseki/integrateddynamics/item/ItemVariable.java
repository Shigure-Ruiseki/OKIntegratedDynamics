package ruiseki.integrateddynamics.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.okcore.config.configurable.ConfigurableItem;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * Item for storing variable references.
 * 
 * @author rubensworks
 */
public class ItemVariable extends ConfigurableItem {

    private static ItemVariable _instance = null;

    /**
     * Get the unique instance.
     * 
     * @return The instance.
     */
    public static ItemVariable getInstance() {
        return _instance;
    }

    /**
     * Make a new item instance.
     *
     * @param eConfig Config for this blockState.
     */
    public ItemVariable(ExtendedConfig eConfig) {
        super(eConfig);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        getVariableFacade(itemStack).addInformation(list, entityPlayer);
        super.addInformation(itemStack, entityPlayer, list, par4);
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        IVariableFacade variableFacade = getVariableFacade(itemStack);
        String label;
        if (variableFacade.isValid() && (label = variableFacade.getLabel()) != null) {
            return EnumChatFormatting.ITALIC + label;
        }
        return super.getItemStackDisplayName(itemStack);
    }

    public IVariableFacade getVariableFacade(ItemStack itemStack) {
        return IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class)
            .handle(itemStack);
    }

}
