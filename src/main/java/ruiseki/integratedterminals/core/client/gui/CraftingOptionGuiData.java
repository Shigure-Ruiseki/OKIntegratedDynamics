package ruiseki.integratedterminals.core.client.gui;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingOption;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingPlan;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * @author rubensworks
 */
public class CraftingOptionGuiData<T, M> {

    private final BlockPos pos;
    private final ForgeDirection side;
    private final IngredientComponent<T, M> component;
    private final String tabName;
    private final int channel;
    @Nullable
    private final HandlerWrappedTerminalCraftingOption<T> craftingOption;
    private final int amount;
    @Nullable
    private final HandlerWrappedTerminalCraftingPlan craftingPlan;

    public CraftingOptionGuiData(BlockPos pos, ForgeDirection side, IngredientComponent<T, M> component, String tabName,
        int channel, @Nullable HandlerWrappedTerminalCraftingOption<T> craftingOption, int amount,
        HandlerWrappedTerminalCraftingPlan craftingPlan) {
        this.pos = pos;
        this.side = side;
        this.component = component;
        this.tabName = tabName;
        this.channel = channel;
        this.craftingOption = craftingOption;
        this.amount = amount;
        this.craftingPlan = craftingPlan;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ForgeDirection getSide() {
        return side;
    }

    public IngredientComponent<T, M> getComponent() {
        return component;
    }

    public String getTabName() {
        return tabName;
    }

    public int getChannel() {
        return channel;
    }

    @Nullable
    public HandlerWrappedTerminalCraftingOption<T> getCraftingOption() {
        return craftingOption;
    }

    public int getAmount() {
        return amount;
    }

    @Nullable
    public HandlerWrappedTerminalCraftingPlan getCraftingPlan() {
        return craftingPlan;
    }

    public static <T, M> CraftingOptionGuiData<T, M> copyWithAmount(CraftingOptionGuiData<T, M> craftingOptionGuiData,
        int amount) {
        return new CraftingOptionGuiData<>(
            craftingOptionGuiData.getPos(),
            craftingOptionGuiData.getSide(),
            craftingOptionGuiData.getComponent(),
            craftingOptionGuiData.getTabName(),
            craftingOptionGuiData.getChannel(),
            craftingOptionGuiData.getCraftingOption(),
            amount,
            craftingOptionGuiData.getCraftingPlan());
    }
}
