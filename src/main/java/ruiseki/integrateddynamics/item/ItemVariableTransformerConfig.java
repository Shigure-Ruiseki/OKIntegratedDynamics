package ruiseki.integrateddynamics.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.item.ItemBase;

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
        super(IntegratedDynamics._instance, true, "variable_transformer", null, config -> new ItemBase() {

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
                    ItemVariableTransformerConfig._instance.getMod()
                        .getModId() + ":" + ItemVariableTransformerConfig._instance.getNamedId() + "_output");
                input = register.registerIcon(
                    ItemVariableTransformerConfig._instance.getMod()
                        .getModId() + ":" + ItemVariableTransformerConfig._instance.getNamedId() + "_input");
            }
        }.setHasSubtypes(true)
            .setMaxDamage(0));
    }

}
