package org.yes.cart.util;

import org.yes.cart.domain.entity.AttrValue;

import java.util.Collection;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 * <p/>
 * Util class for domain objects.
 */
public class DomainApiUtil {

    /**
     * Get the value of attribute from attribute value map.
     *
     * @param attrName attribute name
     * @param values   map of attribute name and {@link AttrValue}
     * @return null if attribute not present in map, otherwise value of attribute
     */
    public static String getAttirbuteValue(final String attrName, final Map<String, AttrValue> values) {
        AttrValue attrValue = values.get(attrName);
        if (attrValue != null) {
            return attrValue.getVal();
        }
        return null;
    }

    /**
     * Get attribute value
     *
     * @param attrName   attribute name
     * @param attributes collection of attribute
     * @return value if fount otherwise null
     */
    public static String getAttirbuteValue(final String attrName, final Collection<? extends AttrValue> attributes) {
        for (AttrValue attrValue : attributes) {
            if (attrName.equals(attrValue.getAttribute().getName())) {
                return attrValue.getVal();
            }
        }
        return null;
    }
    
    private DomainApiUtil() {
        
    }
}
