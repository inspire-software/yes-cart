package org.yes.cart.domain.misc.navigation.price;

import java.util.List;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface PriceTierTree {

    /**
     * Get price tiers node for given currency.
     * @param currency given currency
     * @return price tier nodes.
     */
    List<PriceTierNode> getPriceTierNodes(String currency);

    /**
     *
     * Add price tier node.
     *
     * @param priceTierNodes node to add
     * @param currency given currency 
     * @return added node
     */
    List<PriceTierNode> addPriceTierNode(String currency, List<PriceTierNode> priceTierNodes);
}
