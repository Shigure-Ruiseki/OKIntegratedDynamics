package ruiseki.integrateddynamics.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.configurable.ConfigurableItem;
import ruiseki.okcore.config.configurable.IConfigurable;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

/**
 * Config for the Input and Output Variable Transformer.
 * 
 * @author rubensworks
 *
 */
public class ItemVariableTransformerConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemVariableTransformerConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemVariableTransformerConfig() {
        super(IntegratedDynamics._instance, true, "variableTransformer", null, null);
    }

    @Override
    protected IConfigurable initSubInstance() {
        return (IConfigurable) new ConfigurableItem(this) {

            private IIcon input;
            private IIcon output;

            @Override
            public String getUnlocalizedName(ItemStack itemStack) {
                return super.getUnlocalizedName(itemStack) + (itemStack.getItemDamage() == 0 ? ".output" : ".input");
            }

            @Override
            public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
                super.getSubItems(itemIn, tab, subItems);
                subItems.add(new ItemStack(itemIn, 1, 1));
            }

            @Override
            public IIcon getIconFromDamage(int meta) {
                return meta == 0 ? output : input;
            }

            @Override
            public void registerIcons(IIconRegister register) {
                output = register.registerIcon(
                    getConfig().getMod()
                        .getModId() + ":" + getConfig().getNamedId() + "_output");
                input = register.registerIcon(
                    getConfig().getMod()
                        .getModId() + ":" + getConfig().getNamedId() + "_input");
            }
        }.setHasSubtypes(true)
            .setMaxDamage(0);
    }

}
