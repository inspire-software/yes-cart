package org.yes.cart.domain.entity.bridge;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;


import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.StringBridge;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueProductSku;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class AttributeValueBridge implements FieldBridge {


    /**
     * {@inheritDoc}
     */
    public void set(final String name, final Object value, final Document document, final LuceneOptions luceneOptions) {

        if (value instanceof Collection) {
            for (Object obj : (Collection) value) {
                final AttrValue attrValue = (AttrValue) obj;
                final String prefix = (obj instanceof AttrValueProductSku) ? "sku." : "";

                if (StringUtils.isNotBlank(attrValue.getVal())) {
                    document.add(new Field(
                            prefix + "attribute.val",
                            attrValue.getVal(),
                            luceneOptions.getStore(),
                            Field.Index.NOT_ANALYZED,
                            luceneOptions.getTermVector()
                    ));
                    document.add(new Field(
                            prefix + "attribute.attrvalsearch",
                            attrValue.getVal(),
                            luceneOptions.getStore(),
                            Field.Index.ANALYZED,
                            luceneOptions.getTermVector()
                    ));

                }

                if (StringUtils.isNotBlank(attrValue.getAttribute().getCode())) {
                    document.add(new Field(
                            prefix + "attribute.attribute",
                            attrValue.getAttribute().getCode(),
                            luceneOptions.getStore(),
                            Field.Index.NOT_ANALYZED,
                            luceneOptions.getTermVector()
                    ));
                }

            }
        }

    }


}
