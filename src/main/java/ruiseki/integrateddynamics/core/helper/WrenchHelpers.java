package ruiseki.integrateddynamics.core.helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.commoncapabilities.api.capability.wrench.IWrench;
import ruiseki.commoncapabilities.api.capability.wrench.WrenchTarget;
import ruiseki.commoncapabilities.capability.wrench.WrenchConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * Helper methods related to items.
 *
 * @author rubensworks
 */
public final class WrenchHelpers {

    /**
     * Checks if the given player can wrench something.
     *
     * @param player   The player.
     * @param heldItem The item the player is holding.
     * @param world    The world in which the wrenching is happening.
     * @param pos      The position that is being wrenched.
     * @param side     The side that is being wrenched.
     * @return If the wrenching can continue with the held item.
     */
    public static boolean isWrench(EntityPlayer player, ItemStack heldItem, World world, BlockPos pos,
        ForgeDirection side) {
        if (heldItem == null) return false;
        return CapabilityHelpers.getCapability(heldItem, WrenchConfig.CAPABILITY)
            .map(iWrench -> iWrench.canUse(player, WrenchTarget.forBlock(world, pos, side)))
            .orElse(false);
    }

    /**
     * Wrench a given position.
     * Requires the {@link WrenchHelpers#isWrench(EntityPlayer, ItemStack, World, BlockPos, ForgeDirection)}
     * to be passed.
     * Takes an extra parameter of any type that is forwarded to the wrench action.
     *
     * @param player    The player.
     * @param heldItem  The item the player is holding.
     * @param world     The world in which the wrenching is happening.
     * @param pos       The position that is being wrenched.
     * @param side      The side that is being wrenched.
     * @param action    The actual wrench action.
     * @param parameter An extra parameter that is forwarded to the action.
     * @param <P>       The type of parameter to pass.
     */
    public static <P> void wrench(EntityPlayer player, ItemStack heldItem, World world, BlockPos pos,
        ForgeDirection side, IWrenchAction<P> action, P parameter) {
        IWrench wrench = CapabilityHelpers.getCapability(heldItem, WrenchConfig.CAPABILITY)
            .getOrNull();
        if (wrench == null) return;
        WrenchTarget wrenchTarget = WrenchTarget.forBlock(world, pos, side);
        wrench.beforeUse(player, wrenchTarget);
        action.onWrench(player, pos, parameter);
        wrench.afterUse(player, wrenchTarget);
    }

    /**
     * Wrench a given position.
     * Requires the {@link WrenchHelpers#isWrench(EntityPlayer, ItemStack, World, BlockPos, ForgeDirection)}
     * to be passed.
     *
     * @param player   The player.
     * @param heldItem The item the player is holding.
     * @param pos      The position that is being wrenched.
     * @param action   The actual wrench action.
     */
    public static void wrench(EntityPlayer player, ItemStack heldItem, World world, BlockPos pos, ForgeDirection side,
        IWrenchAction<Void> action) {
        wrench(player, heldItem, world, pos, side, action, null);
    }

    /**
     * An action that can serve as wrenching.
     *
     * @param <P> The type of parameter that is passed.
     */
    public static interface IWrenchAction<P> {

        /**
         * Called for the actual wrenching action.
         *
         * @param player    The player.
         * @param pos       The position that is being wrenched.
         * @param parameter An extra parameter that is used to call this action.
         */
        public void onWrench(EntityPlayer player, BlockPos pos, P parameter);

    }

    /**
     * An action that can serve as wrenching.
     */
    public static abstract class SimpleWrenchAction implements IWrenchAction<Void> {

        /**
         * Called for the actual wrenching action.
         *
         * @param player    The player.
         * @param pos       The position that is being wrenched.
         * @param parameter An extra parameter that is used to call this action.
         */
        public void onWrench(EntityPlayer player, BlockPos pos, Void parameter) {
            onWrench(player, pos);
        }

        /**
         * Called for the actual wrenching action.
         *
         * @param player The player.
         * @param pos    The position that is being wrenched.
         */
        public abstract void onWrench(EntityPlayer player, BlockPos pos);

    }

}
