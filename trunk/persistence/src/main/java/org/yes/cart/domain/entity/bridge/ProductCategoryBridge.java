package org.yes.cart.domain.entity.bridge;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.yes.cart.domain.entity.ProductCategory;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2/4/12
 * Time: 1:33 PM
 */
public class ProductCategoryBridge  implements FieldBridge {

    /** {@inheritDoc} */
    public void set(final String name, final Object value, final Document document, final LuceneOptions luceneOptions) {

         if (value instanceof Collection) {

            for (Object obj : (Collection) value) {

                ProductCategory productCategory = (ProductCategory) obj;

                document.add(new Field(
                        "productCategory.productCategoryId",
                        String.valueOf(productCategory.getProductCategoryId()),
                        luceneOptions.getStore(),
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));

                document.add(new Field(
                        "productCategory.category",
                        String.valueOf(productCategory.getCategory().getCategoryId()),
                        luceneOptions.getStore(),
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));

                document.add(new Field(
                        "productCategory.rank",
                        String.valueOf(productCategory.getRank()),
                        luceneOptions.getStore(),
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));

            }

         }

    }

}
