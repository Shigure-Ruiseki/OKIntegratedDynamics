package ruiseki.integrateddynamics.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.model.IVariableModelProviderRegistry;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHolder;
import ruiseki.integrateddynamics.capability.variablefacade.VariableFacadeHolderConfig;
import ruiseki.integrateddynamics.capability.variablefacade.VariableFacadeHolderDefault;
import ruiseki.integrateddynamics.core.client.model.VariableModelProviders;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeVariableFacade;
import ruiseki.integrateddynamics.core.item.AspectVariableFacade;
import ruiseki.integrateddynamics.core.item.ProxyVariableFacade;
import ruiseki.integrateddynamics.core.item.VariableFacadeHandlerRegistry;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.config.configurable.ConfigurableItem;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

public class ItemVariable extends ConfigurableItem {

    private static ItemVariable _instance = null;

    public static ItemVariable getInstance() {
        return _instance;
    }

    public ItemVariable(ExtendedConfig eConfig) {
        super(eConfig);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (pass == 0) {
            return itemIcon;
        }

        IVariableFacade variableFacade = getVariableFacade(stack);
        if (variableFacade != null && variableFacade.isValid()) {
            IIcon icon = getOverlayIcon(variableFacade);
            return icon;
        }

        return itemIcon;
    }

    @SideOnly(Side.CLIENT)
    private IIcon getOverlayIcon(IVariableFacade variableFacade) {
        if (variableFacade instanceof AspectVariableFacade) {
            AspectVariableFacade facade = (AspectVariableFacade) variableFacade;
            return VariableModelProviders.ASPECT.getIcon(facade.getAspect());
        } else if (variableFacade instanceof ValueTypeVariableFacade<?>) {
            ValueTypeVariableFacade<?> facade = (ValueTypeVariableFacade<?>) variableFacade;
            return VariableModelProviders.VALUETYPE.getIcon(facade.getValueType());
        } else if (variableFacade instanceof ProxyVariableFacade) {
            return VariableModelProviders.PROXY.getIcon(null);
        }
        return null;
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

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new DefaultCapabilityProvider<>(
            () -> VariableFacadeHolderConfig.CAPABILITY,
            new VariableFacadeHolderDefault(stack));
    }

    public IVariableFacade getVariableFacade(ItemStack itemStack) {
        return CapabilityHelpers.getCapability(itemStack, VariableFacadeHolderConfig.CAPABILITY)
            .map(IVariableFacadeHolder::getVariableFacade)
            .orElse(VariableFacadeHandlerRegistry.DUMMY_FACADE);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);

        IVariableModelProviderRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableModelProviderRegistry.class);
        registry.registerIcons(register);
    }
}
