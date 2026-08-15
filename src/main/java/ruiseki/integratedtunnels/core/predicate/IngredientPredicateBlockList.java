package ruiseki.integratedtunnels.core.predicate;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeBlock;
import ruiseki.integratedtunnels.core.TunnelItemHelpers;
import ruiseki.okcore.helper.BlockHelpers;

/**
 * @author rubensworks
 */
public class IngredientPredicateBlockList extends IngredientPredicate<ItemStack, Integer> {

    private final boolean blacklist;
    private final IValueTypeListProxy<ValueObjectTypeBlock, ValueObjectTypeBlock.ValueBlock> blocks;
    private final boolean checkStackSize;
    private final boolean checkItem;
    private final boolean checkDamage;
    private final boolean checkNbt;

    public IngredientPredicateBlockList(boolean blacklist, int amount, boolean exactAmount,
        IValueTypeListProxy<ValueObjectTypeBlock, ValueObjectTypeBlock.ValueBlock> blocks, boolean checkStackSize,
        boolean checkItem, boolean checkDamage, boolean checkNbt) {
        super(IngredientComponent.ITEMSTACK, blacklist, false, amount, exactAmount);
        this.blacklist = blacklist;
        this.blocks = blocks;
        this.checkStackSize = checkStackSize;
        this.checkItem = checkItem;
        this.checkDamage = checkDamage;
        this.checkNbt = checkNbt;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        for (ValueObjectTypeBlock.ValueBlock block : blocks) {
            if (block.getRawValue()
                .isPresent()
                && TunnelItemHelpers.areItemStackEqual(
                    input,
                    BlockHelpers.getItemStackFromBlockState(
                        block.getRawValue()
                            .get()),
                    checkStackSize,
                    checkItem,
                    checkDamage,
                    checkNbt)) {
                return !blacklist;
            }
        }
        return blacklist;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IngredientPredicateBlockList)) {
            return false;
        }
        IngredientPredicateBlockList that = (IngredientPredicateBlockList) obj;
        return super.equals(obj) && this.blacklist == that.blacklist
            && this.checkItem == that.checkItem
            && this.checkStackSize == that.checkStackSize
            && this.checkDamage == that.checkDamage
            && this.checkNbt == that.checkNbt
            && this.blocks.equals(that.blocks);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ (this.blacklist ? 1 : 0) << 1
            ^ (this.checkItem ? 1 : 0) << 2
            ^ (this.checkStackSize ? 1 : 0) << 3
            ^ (this.checkDamage ? 1 : 0) << 4
            ^ (this.checkNbt ? 1 : 0) << 5
            ^ this.blocks.hashCode();
    }
}
