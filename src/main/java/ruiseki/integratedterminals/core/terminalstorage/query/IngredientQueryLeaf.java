package ruiseki.integratedterminals.core.terminalstorage.query;

import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Predicate;

import ruiseki.integratedterminals.api.ingredient.IIngredientComponentTerminalStorageHandler;

/**
 * @author rubensworks
 */
public class IngredientQueryLeaf<T> implements IIngredientQuery<T> {

    private final Predicate<T> tester;

    public IngredientQueryLeaf(String query, IIngredientComponentTerminalStorageHandler<T, ?> handler) {
        Pair<SearchMode, String> parsed = parseQuery(query);
        this.tester = handler.getInstanceFilterPredicate(parsed.getLeft(), parsed.getRight());
    }

    public static Pair<SearchMode, String> parseQuery(String query) {
        if (!query.isEmpty()) {
            char c = query.charAt(0);
            switch (c) {
                case '@':
                    return Pair.of(SearchMode.MOD, query.substring(1));
                case '#':
                    return Pair.of(SearchMode.TOOLTIP, query.substring(1));
                case '$':
                    return Pair.of(SearchMode.DICT, query.substring(1));
            }
        }
        return Pair.of(SearchMode.DEFAULT, query);
    }

    @Override
    public boolean apply(T t) {
        try {
            return this.tester.apply(t);
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

}
