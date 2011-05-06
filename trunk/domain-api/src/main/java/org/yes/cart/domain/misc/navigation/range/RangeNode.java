package org.yes.cart.domain.misc.navigation.range;

import org.yes.cart.domain.misc.Pair;

/**
 *
 * Hi and lo values for filtered navigation.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface RangeNode {

    /**
     * Get the values range.
     * @return {@link Pair} values range
     */
    Pair<String, String> getRange();

    /**
     * Set value range.
     * @param range {@link Pair} values range
     */
    void setRange(Pair<String, String> range);

}
