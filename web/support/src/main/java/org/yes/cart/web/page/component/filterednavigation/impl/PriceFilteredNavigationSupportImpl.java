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

package org.yes.cart.web.page.component.filterednavigation.impl;

import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.NavigationContext;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.web.page.component.filterednavigation.PriceFilteredNavigationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13-09-28
 * Time: 1:12 AM
 */
public class PriceFilteredNavigationSupportImpl extends AbstractFilteredNavigationSupportImpl implements PriceFilteredNavigationSupport {

    private final CategoryService categoryService;
    private final ShopService shopService;
    private final PriceService priceService;

    public PriceFilteredNavigationSupportImpl(final LuceneQueryFactory luceneQueryFactory,
                                              final ProductService productService,
                                              final CategoryService categoryService,
                                              final ShopService shopService,
                                              final PriceService priceService) {
        super(luceneQueryFactory, productService);
        this.categoryService = categoryService;
        this.shopService = shopService;
        this.priceService = priceService;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "filteredNavigationSupport-priceFilteredNavigationRecords")
    public List<FilteredNavigationRecord> getFilteredNavigationRecords(final NavigationContext navigationContext,
                                                                       final Long categoryId,
                                                                       final String currency,
                                                                       final String locale,
                                                                       final String recordName) {

        final List<FilteredNavigationRecord> navigationList = new ArrayList<FilteredNavigationRecord>();

        if (!navigationContext.isGlobal() && !navigationContext.isFilteredBy(ProductSearchQueryBuilder.PRODUCT_PRICE)) {

            final Category category = categoryService.getById(categoryId);
            if (category == null) {
                return Collections.emptyList();
            }
            final PriceTierTree priceTierTree = category.getNavigationByPriceTree();
            final boolean filteredNavigationByPriceAllowed = (category.getNavigationByPrice() == null || priceTierTree == null)
                    ? false : category.getNavigationByPrice();

            if (!filteredNavigationByPriceAllowed) {
                return Collections.emptyList();
            }

            final Shop shop = shopService.getById(navigationContext.getShopId());
            final List<FilteredNavigationRecord> allNavigationRecords = priceService.getPriceNavigationRecords(
                    priceTierTree,
                    currency,
                    shop);

            for (FilteredNavigationRecord record : allNavigationRecords) {

                NavigationContext candidateQuery = getLuceneQueryFactory().getSnowBallQuery(
                        navigationContext, ProductSearchQueryBuilder.PRODUCT_PRICE, record.getValue()
                );
                int candidateResultCount = getProductService().getProductQty(candidateQuery.getProductQuery());
                if (candidateResultCount > 0) {
                    record.setName(recordName);
                    record.setCode(ProductSearchQueryBuilder.PRODUCT_PRICE);
                    record.setCount(candidateResultCount);
                    navigationList.add(record);
                }
            }


        }
        return navigationList;
    }


}
