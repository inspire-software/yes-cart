package org.yes.cart.utils.impl;

import org.yes.cart.domain.entity.Attribute;

import java.util.Comparator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttributeRankComparator implements Comparator<Attribute> {
    /**
     * {@inheritDoc}
     */
    public int compare(final Attribute attr1, final Attribute attr2) {
        return (attr1.getRank() < attr2.getRank() ? -1 : (attr1.getRank() == attr2.getRank() ? 0 : 1));
    }
}
