package org.yes.cart.utils.impl;

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.utils.AttrValueDTOComparator;

/**
 *
 * Compare two attr values dto by attribute name.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttrValueDTOComparatorImpl implements AttrValueDTOComparator {

    /**
     * {@inheritDoc}
     */
    public int compare(final AttrValueDTO attrValueDTO1, final AttrValueDTO attrValueDTO2) {
        return attrValueDTO1.getAttributeDTO().getName().compareTo(attrValueDTO2.getAttributeDTO().getName());
    }
}
