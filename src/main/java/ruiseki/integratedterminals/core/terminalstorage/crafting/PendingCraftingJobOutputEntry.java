package ruiseki.integratedterminals.core.terminalstorage.crafting;

import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.integratedterminals.api.terminalstorage.crafting.TerminalCraftingJobStatus;

/**
 * A single component-agnostic pending crafting job output, as it is sent from server to client.
 *
 * @param ingredient The pending output, where the quantity indicates how much is still expected to be crafted.
 * @param status     The status of the crafting jobs that will produce the ingredient.
 * @author rubensworks
 */
public record PendingCraftingJobOutputEntry(IPrototypedIngredient<?, ?> ingredient, TerminalCraftingJobStatus status) {}
