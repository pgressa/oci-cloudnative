package catalogue.controllers;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

/**
 * Lists the available categories
 */
@Introspected
public class CategoriesDTO {

    private final String[] categories;

    public CategoriesDTO(List<String> categories) {
        this.categories = categories.toArray(new String[0]);
    }

    /**
     * An array of category names
     *
     * @return An array of category names
     */
    public String[] getCategories() {
        return categories;
    }
}
