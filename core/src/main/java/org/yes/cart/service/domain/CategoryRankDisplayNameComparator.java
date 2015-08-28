package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;

import java.util.Comparator;

/**
 * User: denispavlov
 * Date: 28/08/2015
 * Time: 08:42
 */
public class CategoryRankDisplayNameComparator implements Comparator<Category> {

    private final String lang;

    public CategoryRankDisplayNameComparator(final String lang) {
        this.lang = lang;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Category cat1, final Category cat2) {
        int rank = Integer.compare(cat1.getRank(), cat2.getRank());
        if (rank == 0) {
            final String name1 = new FailoverStringI18NModel(cat1.getDisplayName(), cat1.getName()).getValue(lang);
            final String name2 = new FailoverStringI18NModel(cat2.getDisplayName(), cat2.getName()).getValue(lang);
            int name = name1 == null ? -1 : (name2 == null ? 1 : name1.toLowerCase().compareTo(name2.toLowerCase()));
            if (name == 0) {
                return Long.compare(cat1.getCategoryId(), cat2.getCategoryId());
            }
            return name;
        }
        return rank;
    }
}
