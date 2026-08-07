package ruiseki.integrateddynamics.block;

import net.minecraft.init.Blocks;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.ConfigurableTypeCategory;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Menril Log.
 *
 * @author rubensworks
 *
 */
public class BlockMenrilLogConfig extends BlockConfig {

    /**
     * The 1/x chance at which a Menril Log will be filled with Menril Resin when generated.
     * TODO Add filledMenrilLogChance
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.WORLDGENERATION,
        comment = "The 1/x chance at which a Menril Log will be filled with Menril Resin when generated.",
        isCommandable = true)
    public static int filledMenrilLogChance = 10;

    /**
     * The unique instance.
     */
    public static BlockMenrilLogConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockMenrilLogConfig() {
        super(IntegratedDynamics._instance, true, "menrilLog", null, BlockMenrilLog.class);
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_WOODLOG;
    }

    @Override
    public void onRegistered() {
        Blocks.fire.setFireInfo(getBlockInstance(), 5, 20);
    }

}
