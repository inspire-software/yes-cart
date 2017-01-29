/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.domain.entity.bridge;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.bridge.support.ShopCategoryRelationshipSupport;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Product category bridge determines allows to track immediate categories to which this product is
 * linked to.
 *
 * It also infers the relationship between product and shop if the product is reachable via
 * shop category assigned branch.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2/4/12
 * Time: 1:33 PM
 */
public class ProductCategoryBridge implements FieldBridge {

    private final DisplayNameBridge displayNameBridge = new DisplayNameBridge();

    /**
     * {@inheritDoc}
     */
    public void set(final String name, final Object value, final Document document, final LuceneOptions luceneOptions) {

        if (value instanceof Collection) {


            /*
             * We use service here as this is a heavy DB operation to scan all categories.
             * We cannot run it on every indexing of product, which happens every time we
             * update the product (including when it's inventory updates during ordering).
             * This is the result of performance testing and unless we find a smarter way
             * to flush/use cache we need this here.
             *
             * This should not cause any issues as product indexing should happen after
             * all categories updates took place, so it is highly unlikely that the cache
             * on getShopCategoriesIds(shop) will be out of sync.
             */

            final ShopCategoryRelationshipSupport support = getShopCategoryRelationshipSupport();
            final List<Shop> shops = support.getAll();

            for (Object obj : (Collection) value) {

                ProductCategory productCategory = (ProductCategory) obj;

                final Category category = support.getCategoryById(productCategory.getCategory().getCategoryId());

                addCategory(document, luceneOptions, category, support);

                for (final Shop shop : shops) {

                    if (support.getShopCategoriesIds(shop.getShopId()).contains(category.getCategoryId())) {
                        document.add(new Field(
                                ProductSearchQueryBuilder.PRODUCT_SHOP_FIELD,
                                String.valueOf(shop.getShopId()),
                                Field.Store.NO,
                                Field.Index.NOT_ANALYZED,
                                luceneOptions.getTermVector()
                        ));
                    }

                }

            }

        }

    }

    private void addCategory(final Document document,
                             final LuceneOptions luceneOptions,
                             final Category category,
                             final ShopCategoryRelationshipSupport support) {

        document.add(new Field(
                ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD,
                String.valueOf(category.getCategoryId()),
                Field.Store.NO,
                Field.Index.NOT_ANALYZED,
                luceneOptions.getTermVector()
        ));

        addCategoryName(document, luceneOptions, category);

        addCategoryParentIds(document, luceneOptions, category, support);

    }

    private void addCategoryName(final Document document,
                                 final LuceneOptions luceneOptions,
                                 final Category category) {

        document.add(new Field(
                ProductSearchQueryBuilder.PRODUCT_CATEGORYNAME_FIELD,
                category.getName(),
                Field.Store.NO,
                Field.Index.NOT_ANALYZED,
                luceneOptions.getTermVector()
        ));

        document.add(new Field(
                ProductSearchQueryBuilder.PRODUCT_CATEGORYNAME_STEM_FIELD,
                category.getName(),
                Field.Store.NO,
                Field.Index.ANALYZED,
                luceneOptions.getTermVector()
        ));

        displayNameBridge.set(
                ProductSearchQueryBuilder.PRODUCT_CATEGORYNAME_FIELD,
                category.getDisplayName(),
                document,
                luceneOptions
        );

    }

    private void addCategoryParentIds(final Document document,
                                      final LuceneOptions luceneOptions,
                                      final Category category,
                                      final ShopCategoryRelationshipSupport support) {

        document.add(new Field(
                ProductSearchQueryBuilder.PRODUCT_CATEGORY_INC_PARENTS_FIELD,
                String.valueOf(category.getCategoryId()),
                Field.Store.NO,
                Field.Index.NOT_ANALYZED,
                luceneOptions.getTermVector()
        ));

        final Set<Long> parentIds = support.getCategoryParentsIds(category.getCategoryId());
        for (final Long parentId : parentIds) {

            final Category parent = support.getCategoryById(parentId);

            addCategoryName(document, luceneOptions, parent);

            addCategoryParentIds(document, luceneOptions, parent, support);
        }

    }

    private ShopCategoryRelationshipSupport getShopCategoryRelationshipSupport() {
        return HibernateSearchBridgeStaticLocator.getShopCategoryRelationshipSupport();
    }

}
