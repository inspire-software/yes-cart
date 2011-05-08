package org.yes.cart.domain.entity.bridge;

import org.hibernate.search.bridge.StringBridge;
import org.yes.cart.domain.entity.Shop;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class ShopBridge   implements StringBridge {
    /** {@inheritDoc} */
    public String objectToString(final Object shopObject) {
        return String.valueOf( ((Shop)shopObject).getShopId());
    }
}
