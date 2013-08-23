/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.ShopService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2/4/12
 * Time: 1:33 PM
 */
public class ProductCategoryBridge implements FieldBridge {

    /**
     * {@inheritDoc}
     */
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


            new TransactionTemplate(HibernateSearchBridgeStaticLocator.getTransactionManager()).execute(
                    new TransactionCallbackWithoutResult() {
                        public void doInTransactionWithoutResult(final TransactionStatus status) {

                            final List<Shop> allShop = HibernateSearchBridgeStaticLocator.getShopDao().findAll();

                            for (Shop shop : allShop) {

                                if (isIntersected(getShopCategories(shop), (Set<ProductCategory>) value)) {

                                    document.add(new Field(
                                            ProductSearchQueryBuilder.PRODUCT_SHOP_FIELD,
                                            String.valueOf(shop.getShopId()),
                                            luceneOptions.getStore(),
                                            Field.Index.NOT_ANALYZED,
                                            luceneOptions.getTermVector()
                                    ));

                                }

                            }


                        }
                    }

            );



        }

    }

    /**
     * Get all cat4egories, which are belong to shop.
     * @param shop given shop
     * @return all categories in the shop
     */
    Set<Category> getShopCategories(final Shop shop) {

        final Set<Category> result = new HashSet<Category>();

        for (ShopCategory shopCategory : shop.getShopCategory()) {

            loadChildCategoriesRecursiveInternal(result, shopCategory.getCategory().getCategoryId());

        }

        return result;
    }

    private void loadChildCategoriesRecursiveInternal(final Set<Category> result, final Long categoryId) {

        final GenericDAO<Category, Long> categoryDao =  HibernateSearchBridgeStaticLocator.getCategoryDao();

        result.add(categoryDao.findById(categoryId));

        final List<Category> categories = categoryDao.findByNamedQuery(
                "CATEGORIES.BY.PARENTID.WITHOUT.DATE.FILTERING",
                categoryId
        );

        result.addAll(categories);

        for (Category subCategory : categories) {

            loadChildCategoriesRecursiveInternal(result, subCategory.getCategoryId());

        }

    }





    /**
     * Detect intersection between given sets of categories, which are belong to particular shop and
     * categories where product is resided.
     * @param shopCategories  given shop categories
     * @param productCategories  given product categories
     * @return true in case of intersection
     */
    boolean isIntersected(final Set<Category> shopCategories, final Set<ProductCategory> productCategories) {
        for (ProductCategory productCategory : productCategories) {
            for (Category category : shopCategories) {
                if (productCategory.getCategory().getCategoryId() == category.getCategoryId()) {
                    return true;
                }
            }
        }
        return false;
    }


}
