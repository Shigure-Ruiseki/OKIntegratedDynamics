package ruiseki.integratedtunnels.core.predicate;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterables;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.integratedtunnels.core.TunnelItemHelpers;
import ruiseki.okcore.helper.ItemHelpers;

/**
 * @author rubensworks
 */
public class IngredientPredicateItemStackList extends IngredientPredicate<ItemStack, Integer> {

    private final boolean blacklist;
    private final IValueTypeListProxy<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack> itemStacks;
    private final boolean checkStackSize;
    private final boolean checkItem;
    private final boolean checkDamage;
    private final boolean checkNbt;

    public IngredientPredicateItemStackList(boolean blacklist, int amount, boolean exactAmount,
        IValueTypeListProxy<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack> itemStacks,
        int matchFlags, boolean checkStackSize, boolean checkItem, boolean checkDamage, boolean checkNbt) {
        super(
            IngredientComponent.ITEMSTACK,
            Iterables.transform(
                Iterables.filter(itemStacks, itemStack -> !ItemHelpers.isEmpty(itemStack.getRawValue())),
                stack -> TunnelItemHelpers.prototypeWithCount(stack.getRawValue(), amount)),
            matchFlags,
            blacklist,
            false,
            amount,
            exactAmount);
        this.blacklist = blacklist;
        this.itemStacks = itemStacks;
        this.checkStackSize = checkStackSize;
        this.checkItem = checkItem;
        this.checkDamage = checkDamage;
        this.checkNbt = checkNbt;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        for (ValueObjectTypeItemStack.ValueItemStack itemStackValue : itemStacks) {
            ItemStack targetStack = itemStackValue.getRawValue();
            if (!ItemHelpers.isEmpty(targetStack)
                && TunnelItemHelpers.areItemStackEqual(input, targetStack, false, checkItem, checkDamage, checkNbt)) { // TODO:
                                                                                                                       // hardcoded
                                                                                                                       // 'false'
                                                                                                                       // may
                                                                                                                       // have
                                                                                                                       // to
                                                                                                                       // be
                                                                                                                       // removed
                                                                                                                       // when
                                                                                                                       // restoring
                                                                                                                       // exact
                                                                                                                       // amount
                return !blacklist;
            }
        }
        return blacklist;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IngredientPredicateItemStackList)) {
            return false;
        }
        IngredientPredicateItemStackList that = (IngredientPredicateItemStackList) obj;
        return super.equals(obj) && this.blacklist == that.blacklist
            && this.checkItem == that.checkItem
            && this.checkStackSize == that.checkStackSize
            && this.checkDamage == that.checkDamage
            && this.checkNbt == that.checkNbt
            && this.itemStacks.equals(that.itemStacks);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ (this.blacklist ? 1 : 0) << 1
            ^ (this.checkItem ? 1 : 0) << 2
            ^ (this.checkStackSize ? 1 : 0) << 3
            ^ (this.checkDamage ? 1 : 0) << 4
            ^ (this.checkNbt ? 1 : 0) << 5
            ^ this.itemStacks.hashCode();
    }
}
