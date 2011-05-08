package org.yes.cart.domain.entity.bridge;

import org.hibernate.search.bridge.StringBridge;

import java.text.DecimalFormat;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class RankBridge  implements StringBridge {

    private static final DecimalFormat formatter = new DecimalFormat("00000000");

    /** {@inheritDoc} */
    public String objectToString(final Object integerRankObject) {
        Integer integer = (Integer) integerRankObject;
        int rankToIndex = integer * 100;
        return formatter.format(rankToIndex);        
    }

}
