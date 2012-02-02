package org.yes.cart.domain.entity.bridge;

import org.hibernate.search.bridge.StringBridge;
import org.yes.cart.domain.entity.Category;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class CategoryBridge  implements StringBridge {
    /** {@inheritDoc} */
    public String objectToString(final Object categoryObject) {
        if (categoryObject instanceof String) {
            return  (String) categoryObject;
        }
        return String.valueOf( ((Category)categoryObject).getCategoryId());
    }
}
