package ruiseki.integratednbt.helpers;

import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.item.ItemVariableConfig;

public class VariableHelpers {

    public static boolean isVariable(ItemStack itemStack) {
        return itemStack.getItem() == ItemVariableConfig._instance.getInstance();
    }
}
