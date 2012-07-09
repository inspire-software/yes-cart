package org.yes.cart.domain.misc.navigation.price;

import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface PriceTierNode {

    Pair<BigDecimal, BigDecimal> getPriceRange();

    void setPriceRange(Pair<BigDecimal, BigDecimal> priceRange);

    List<PriceTierNode> getPriceSubRange();

    void setPriceSubRange(List<PriceTierNode> priceSubRange);

}
