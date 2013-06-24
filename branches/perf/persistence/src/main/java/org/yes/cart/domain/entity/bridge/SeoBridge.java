package org.yes.cart.domain.entity.bridge;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.StringBridge;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.domain.entity.impl.SeoEntity;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

/**
 * User: igora Igor Azarny
 * Date: 6/15/13
 * Time: 4:45 PM
 */
public class SeoBridge implements FieldBridge {

    /** {@inheritDoc} */

    public void set(final String name, final Object value, final Document document, final LuceneOptions luceneOptions) {
        
        if (value instanceof Seo) {
            
            final Seo seo = (Seo) value;


            document.add(new Field(
                    ProductSearchQueryBuilder.CATEGORY_SEO_URI_FIELD,
                    seo.getUri(),
                    luceneOptions.getStore(),
                    Field.Index.NOT_ANALYZED,
                    luceneOptions.getTermVector()
            ));
            document.add(new Field(
                    ProductSearchQueryBuilder.CATEGORY_SEO_TITLE_FIELD,
                    seo.getTitle(),
                    luceneOptions.getStore(),
                    Field.Index.NOT_ANALYZED,
                    luceneOptions.getTermVector()
            ));
            document.add(new Field(
                    ProductSearchQueryBuilder.CATEGORY_SEO_METAKEYWORDS_FIELD,
                    seo.getMetakeywords(),
                    luceneOptions.getStore(),
                    Field.Index.NOT_ANALYZED,
                    luceneOptions.getTermVector()
            ));
            document.add(new Field(
                    ProductSearchQueryBuilder.CATEGORY_SEO_METADESCRIPTION_FIELD,
                    seo.getMetadescription(),
                    luceneOptions.getStore(),
                    Field.Index.NOT_ANALYZED,
                    luceneOptions.getTermVector()
            ));

            
        }
        
    }
}
