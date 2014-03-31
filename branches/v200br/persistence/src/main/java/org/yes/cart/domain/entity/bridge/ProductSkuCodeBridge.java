package org.yes.cart.domain.entity.bridge;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;
import org.yes.cart.domain.entity.ProductSku;

/**
 * User: denispavlov
 * Date: 13/11/2013
 * Time: 14:30
 */
public class ProductSkuCodeBridge implements TwoWayFieldBridge {

    /** {@inheritDoc} */
    @Override
    public Object get(String name, Document document) {
        final Fieldable field = document.getFieldable(name);
        if (field != null) {
            return field.stringValue();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String objectToString(Object object) {
        if (object instanceof ProductSku) {
            return ((ProductSku) object).getCode();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
        if (value instanceof ProductSku) {

            String rez = objectToString(value);
            Field field = new Field(
                    name,
                    rez,
                    Field.Store.YES,
                    Field.Index.NOT_ANALYZED,
                    luceneOptions.getTermVector()
            );
            document.add(field);

        }
    }
}
