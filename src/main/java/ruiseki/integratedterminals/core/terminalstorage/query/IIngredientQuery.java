package ruiseki.integratedterminals.core.terminalstorage.query;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.common.base.Predicate;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedterminals.api.ingredient.IIngredientComponentTerminalStorageHandler;
import ruiseki.integratedterminals.capability.ingredient.IngredientComponentTerminalStorageHandlerConfig;

/**
 * @author rubensworks
 */
public interface IIngredientQuery<T> extends Predicate<T> {

    public static <T, M> IIngredientQuery<T> parse(IngredientComponent<T, M> ingredientComponent, String query) {
        if (query.contains(" ")) {
            String[] conjunctions = query.split(" ");
            return new IngredientQueryConjunctive<>(
                Arrays.stream(conjunctions)
                    .map(c -> parse(ingredientComponent, c))
                    .collect(Collectors.toList()));
        } else if (query.contains("|")) {
            String[] disjunctions = query.split("\\|");
            return new IngredientQueryDisjunctive<>(
                Arrays.stream(disjunctions)
                    .map(c -> parse(ingredientComponent, c))
                    .collect(Collectors.toList()));
        } else {
            IIngredientComponentTerminalStorageHandler<T, M> handler = ingredientComponent
                .getCapability(IngredientComponentTerminalStorageHandlerConfig.CAPABILITY)
                .getOrNull();
            return new IngredientQueryLeaf<>(query, handler);
        }
    }

}
