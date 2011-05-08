package org.yes.cart.domain.misc;

import org.yes.cart.domain.entity.Rankable;

import java.util.Comparator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public class RankableComparatorImpl implements Comparator<Rankable> {

    /**
     * {@inheritDoc}
     */
    public int compare(final Rankable object1, final Rankable object2) {
        return object1.getRank() - object2.getRank();
    }

}
