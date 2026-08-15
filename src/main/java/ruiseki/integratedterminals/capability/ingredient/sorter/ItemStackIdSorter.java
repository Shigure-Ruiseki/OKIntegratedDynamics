package ruiseki.integratedterminals.capability.ingredient.sorter;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.integratedterminals.client.gui.image.Images;

/**
 * Sorts items by internal ID (Minecraft 1.7.10 compatible).
 * 
 * @author rubensworks
 */
public class ItemStackIdSorter extends IngredientInstanceSorterAdapter<ItemStack> {

    public ItemStackIdSorter() {
        super(Images.BUTTON_MIDDLE_ID, "itemstack", "id");
    }

    protected String getItemStackId(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItem() == null) {
            return "";
        }

        Item item = itemStack.getItem();

        GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(item);
        if (id != null) {
            return id + ":" + itemStack.getItemDamage();
        }

        String unlocalizedName = item.getUnlocalizedName(itemStack);
        return (unlocalizedName != null ? unlocalizedName : "") + ":" + itemStack.getItemDamage();
    }

    @Override
    public int compare(ItemStack o1, ItemStack o2) {
        if (o1 == o2) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        return getItemStackId(o1).compareTo(getItemStackId(o2));
    }
}
