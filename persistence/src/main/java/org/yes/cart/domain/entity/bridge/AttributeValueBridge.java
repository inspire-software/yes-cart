package org.yes.cart.domain.entity.bridge;

import org.hibernate.search.bridge.StringBridge;
import org.yes.cart.domain.entity.AttrValue;

import java.util.Map;
import java.util.Map.Entry;

/**
  * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class AttributeValueBridge implements StringBridge {
    /** {@inheritDoc} */
    public String objectToString(final Object attributeValuesObject) {
        StringBuilder stringBuilder = new StringBuilder();
        Map codeValue = (Map) attributeValuesObject;
        for (Object entryObject : codeValue.entrySet()) {
            Entry entry = (Entry) entryObject;
            String key = (String) entry.getKey();
            AttrValue val = (AttrValue) entry.getValue();
            stringBuilder.append(key);
            stringBuilder.append('=');
            stringBuilder.append(val.getVal());
        }
        return stringBuilder.toString();
    }

}
