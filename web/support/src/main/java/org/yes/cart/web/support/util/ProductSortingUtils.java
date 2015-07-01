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

package org.yes.cart.web.support.util;

import org.yes.cart.domain.entity.bridge.SkuPriceBridge;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 17/04/2015
 * Time: 23:35
 */
public class ProductSortingUtils {

    private static final SkuPriceBridge SKU_PRICE_BRIDGE = new SkuPriceBridge();

    public static class SupportedSorting {

        protected String labelKey;
        protected String sortFieldBase;

        private SupportedSorting(final String labelKey, final String sortFieldBase) {
            this.labelKey = labelKey;
            this.sortFieldBase = sortFieldBase;
        }

        public String resolveLabelKey(final long shopId, final String language, final String currency) {

            return labelKey;

        }

        public String resolveSortField(final long shopId, final String language, final String currency) {

            return this.sortFieldBase;

        }

    }

    private static final Map<String, SupportedSorting> SUPPORTED_MAPPING = new HashMap<String, SupportedSorting>() {{
        put("name", new SupportedSorting("byName", ProductSearchQueryBuilder.PRODUCT_NAME_SORT_FIELD));
        put("displayName", new SupportedSorting("byName", ProductSearchQueryBuilder.PRODUCT_DISPLAYNAME_SORT_FIELD) {
            public String resolveSortField(final long shopId, final String language, final String currency) {
                return this.sortFieldBase + language;
            }
        });
        put("basePrice", new SupportedSorting("byPrice", null) {
            public String resolveSortField(final long shopId, final String language, final String currency) {
                final Pair<String, String> priceSort = SKU_PRICE_BRIDGE.objectToString(shopId, currency, null);
                return priceSort.getFirst();
            }
        });
        put("productCode", new SupportedSorting("byCode", ProductSearchQueryBuilder.PRODUCT_CODE_FIELD));
        put("manufacturerCode", new SupportedSorting("byCode", ProductSearchQueryBuilder.PRODUCT_MANUFACTURER_CODE_FIELD));
        put("sku", new SupportedSorting("bySKU", ProductSearchQueryBuilder.SKU_PRODUCT_CODE_FIELD));
        put("brand", new SupportedSorting("byBrand", ProductSearchQueryBuilder.BRAND_FIELD));
        put("availability", new SupportedSorting("byAvailability", ProductSearchQueryBuilder.PRODUCT_AVAILABILITY_FIELD));
        put("created", new SupportedSorting("byCreation", ProductSearchQueryBuilder.PRODUCT_CREATED_FIELD));
    }};

    /**
     * Null object for sorting
     */
    public static final SupportedSorting NULL_SORT = new SupportedSorting("byName", "");


    /**
     * Get sort field configuration.
     *
     * @param sort sort name (as specified in category/shop attributes)
     *
     * @return sort mapping (or null if not supported)
     */
    public static SupportedSorting getConfiguration(final String sort) {
        return SUPPORTED_MAPPING.get(sort);
    }

    private ProductSortingUtils() {
        // no instance
    }
}
