package ruiseki.integrateddynamics.block;

import net.minecraft.init.Blocks;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Menril Wood.
 *
 * @author rubensworks
 *
 */
public class BlockMenrilLogFilledConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockMenrilLogFilledConfig _instance;

    /**
     * The 1/x chance at which Menril Wood will be filled with Menril Resin when generated, the higher this value, the
     * lower the chance.
     */
    @ConfigurableProperty(
        category = "world_generation",
        comment = "The 1/x chance at which Menril Wood will be filled with Menril Resin when generated, the higher this value, the lower the chance.",
        isCommandable = true,
        minimalValue = 0)
    public static int filledMenrilLogChance = 10;

    /**
     * Make a new instance.
     */
    public BlockMenrilLogFilledConfig() {
        super(IntegratedDynamics._instance, true, "menril_log_filled", null, config -> new BlockMenrilLogFilled());
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_WOODLOG;
    }

    @Override
    public void onRegistered() {
        Blocks.fire.setFireInfo(getInstance(), 5, 20);
    }

}
