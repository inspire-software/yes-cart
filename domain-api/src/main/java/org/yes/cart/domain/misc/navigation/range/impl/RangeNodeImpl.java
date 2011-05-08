package org.yes.cart.domain.misc.navigation.range.impl;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.range.RangeNode;

/**
 * Value range implementation.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public class RangeNodeImpl implements RangeNode {

    private Pair<String, String> range;

    /**
     * {@inheritDoc}
     */
    public Pair<String, String> getRange() {
        return range;
    }

    /**
     * {@inheritDoc}
     */
    public void setRange(final Pair<String, String> range) {
        this.range = range;
    }

    /**
     * Construct value range.
     *
     * @param range value range.
     */
    public RangeNodeImpl(final Pair<String, String> range) {
        this.range = range;
    }

    /**
     * Construct value range.
     *
     * @param loValue lo value of range
     * @param hiValue hi value of range
     */
    public RangeNodeImpl(final String loValue, final String hiValue) {
        range = new Pair<String, String>(loValue, hiValue);
    }
}
