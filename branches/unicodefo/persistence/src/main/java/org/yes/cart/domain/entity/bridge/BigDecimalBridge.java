package org.yes.cart.domain.entity.bridge;

import org.hibernate.search.bridge.StringBridge;
import org.yes.cart.constants.Constants;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 *
 * Need custom bridge for prices, because it must be in minor units and
 * must be padded to be used in lucene index.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01 *
 *
 */
public class BigDecimalBridge implements StringBridge {


    private final DecimalFormat formatter = new DecimalFormat(Constants.MONEY_FORMAT_TOINDEX);

    /**
     * {@inheritDoc}
     */
    public String objectToString(final Object bigDecimalPriceObject) {
        if (bigDecimalPriceObject instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) bigDecimalPriceObject;
            long toIndex = bigDecimal.movePointRight(2).longValue();
            return formatter.format(toIndex);
        }
        return null;
    }

}
