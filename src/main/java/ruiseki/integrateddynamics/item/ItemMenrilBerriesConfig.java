package ruiseki.integrateddynamics.item;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.ConfigurableTypeCategory;
import ruiseki.okcore.config.configurable.ConfigurableItemFood;
import ruiseki.okcore.config.configurable.IConfigurable;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

/**
 * Config for the Menril Berries.
 *
 * @author rubensworks
 *
 */
public class ItemMenrilBerriesConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemMenrilBerriesConfig _instance;

    /**
     * If the berries should give the night vision effect when eaten.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.ITEM,
        comment = "If the berries should give the night vision effect when eaten.",
        requiresMcRestart = true)
    public static boolean nightVision = true;

    /**
     * Make a new instance.
     */
    public ItemMenrilBerriesConfig() {
        super(IntegratedDynamics._instance, true, "menril_berries", null, null);
    }

    @Override
    protected IConfigurable initSubInstance() {
        ConfigurableItemFood food = new ConfigurableItemFood(this, 4, 0.3F, false) {

            @Override
            public int getMaxItemUseDuration(ItemStack stack) {
                return 10;
            }
        };
        if (nightVision) {
            food = (ConfigurableItemFood) food.setPotionEffect(Potion.nightVision.id, 20, 1, 1);
        }
        return food;
    }

}
