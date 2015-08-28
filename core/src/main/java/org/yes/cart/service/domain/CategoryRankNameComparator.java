package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Category;

import java.util.Comparator;

/**
 * User: denispavlov
 * Date: 28/08/2015
 * Time: 08:42
 */
public class CategoryRankNameComparator implements Comparator<Category> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Category cat1, final Category cat2) {
        int rank = Integer.compare(cat1.getRank(), cat2.getRank());
        if (rank == 0) {
            final String name1 = cat1.getName();
            final String name2 = cat2.getName();
            int name = name1 == null ? -1 : (name2 == null ? 1 : name1.toLowerCase().compareTo(name2.toLowerCase()));
            if (name == 0) {
                return Long.compare(cat1.getCategoryId(), cat2.getCategoryId());
            }
            return name;
        }
        return rank;
    }
}
