package org.yes.cart.utils.impl;


import org.yes.cart.domain.entity.AttrValue;

import java.util.Comparator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttrValueRankComparator implements Comparator<AttrValue> {

    /** {@inheritDoc} */
    public int compare(final AttrValue attrValue1, final AttrValue attrValue2) {
        return (attrValue1.getAttribute().getRank() < attrValue2.getAttribute().getRank() ? -1 : (attrValue1.getAttribute().getRank() == attrValue2.getAttribute().getRank() ? 0 : 1));
    }
}
