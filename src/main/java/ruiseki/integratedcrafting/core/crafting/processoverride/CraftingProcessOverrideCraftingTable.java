package ruiseki.integratedcrafting.core.crafting.processoverride;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.api.crafting.CraftingJob;
import ruiseki.integratedcrafting.api.crafting.ICraftingProcessOverride;
import ruiseki.integratedcrafting.api.crafting.ICraftingResultsSink;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.okcore.helper.CraftingHelpers;

/**
 * A crafting process override for crafting tables (Minecraft 1.7.10).
 *
 * @author rubensworks
 */
public class CraftingProcessOverrideCraftingTable implements ICraftingProcessOverride {

    private static final GameProfile PROFILE = new GameProfile(
        UUID.fromString("41C82C87-7AfB-4024-BB57-13D2C99CAE77"),
        "[IntegratedCrafting]");
    // Weak keys AND values: the FakePlayer value holds a reference to its ServerLevel key,
    // so a WeakHashMap (weak keys only) would keep the key alive forever and leak.
    private static final Map<WorldServer, FakePlayer> FAKE_PLAYERS = new MapMaker().weakKeys()
        .weakValues()
        .makeMap();

    public static FakePlayer getFakePlayer(WorldServer world) {
        FakePlayer fakePlayer = FAKE_PLAYERS.get(world);
        if (fakePlayer == null) {
            fakePlayer = new FakePlayer(world, PROFILE);
            FAKE_PLAYERS.put(world, fakePlayer);
        }
        return fakePlayer;
    }

    @Override
    public boolean isApplicable(PartPos target) {
        if (target == null || target.getPos() == null
            || target.getPos()
                .getWorld() == null) {
            return false;
        }
        return target.getPos()
            .getBlockPos()
            .getBlock(
                target.getPos()
                    .getWorld()) instanceof BlockWorkbench;
    }

    @Override
    public boolean craft(Function<IngredientComponent<?, ?>, PartPos> targetGetter, IMixedIngredients ingredients,
        IRecipeDefinition recipe, ICraftingResultsSink resultsSink, CraftingJob craftingJob, boolean simulate) {
        PartPos target = targetGetter.apply(IngredientComponent.ITEMSTACK);
        if (target == null || target.getPos() == null
            || target.getPos()
                .getWorld() == null) {
            return false;
        }

        CraftingGrid grid = new CraftingGrid(ingredients, 3, 3);
        IRecipe recipeCached = CraftingHelpers.findMatchingRecipeCached(
            grid,
            target.getPos()
                .getWorld(),
            true);

        if (recipeCached != null) {
            ItemStack result = recipeCached.getCraftingResult(grid);

            if (result == null) {
                return false;
            }

            if (!simulate) {
                EntityPlayer player = getFakePlayer(
                    (WorldServer) target.getPos()
                        .getWorld());

                result.onCrafting(
                    target.getPos()
                        .getWorld(),
                    player,
                    1);
                FMLCommonHandler.instance()
                    .firePlayerCraftingEvent(player, result, grid);

                resultsSink.addResult(IngredientComponent.ITEMSTACK, result.copy());

                for (int i = 0; i < grid.getSizeInventory(); i++) {
                    ItemStack stack = grid.getStackInSlot(i);
                    if (stack != null) {
                        if (stack.getItem()
                            .hasContainerItem(stack)) {
                            ItemStack containerStack = stack.getItem()
                                .getContainerItem(stack);
                            if (containerStack != null) {
                                craftingJob.addToIngredientsStorageBuffer(IngredientComponent.ITEMSTACK, stack);
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

}
