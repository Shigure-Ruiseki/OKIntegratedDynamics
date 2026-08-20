package ruiseki.integrateddynamics.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import com.gtnewhorizon.gtnhlib.color.RGBColor;
import com.gtnewhorizon.gtnhlib.itemrendering.IItemTexture;
import com.gtnewhorizon.gtnhlib.itemrendering.ItemTexture;
import com.gtnewhorizon.gtnhlib.itemrendering.ItemWithTextures;

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
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.ItemBase;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

public class ItemVariable extends ItemBase implements ItemWithTextures {

    public ItemVariable() {
        super();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemTexture[] getTextures(ItemStack stack) {
        List<IItemTexture> textures = new ArrayList<>();

        textures.add(new ItemTexture(this.itemIcon, RGBColor.WHITE));

        // Layer 1: Overlay Texture
        IVariableFacade variableFacade = getVariableFacade(stack);
        if (variableFacade != null && variableFacade.isValid()) {
            IIcon overlayIcon = getOverlayIcon(variableFacade);
            if (overlayIcon != null) {
                textures.add(new ItemTexture(overlayIcon, RGBColor.WHITE));
            }
        }

        return textures.toArray(new IItemTexture[0]);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        IVariableFacade variableFacade = getVariableFacade(stack);
        return variableFacade != null && variableFacade.isValid();
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        if (!hasContainerItem(itemStack)) return null;
        return itemStack;
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
        IVariableFacade variableFacade = getVariableFacade(itemStack);
        variableFacade.addInformation(list, entityPlayer);
        if (variableFacade != VariableFacadeHandlerRegistry.DUMMY_FACADE && entityPlayer.capabilities.isCreativeMode) {
            list.add(LangHelpers.localize("item.items.integrateddynamics.variable.warning"));
        }
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
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);

        IVariableModelProviderRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableModelProviderRegistry.class);
        registry.registerIcons(register);
    }
}
