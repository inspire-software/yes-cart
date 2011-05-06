package org.yes.cart.domain.misc.navigation.range;

import java.io.Serializable;
import java.util.List;

/**
 *
 * List of range values.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface RangeList<RangeNode> extends List<RangeNode>, Serializable {

    public static final String RANGE_NODE_ALIAS = "range";
    public static final String RANGE_LIST_ALIAS = "rangeList";



}
