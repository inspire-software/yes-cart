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

import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.bridge.support.SkuPriceRelationshipSupport;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.util.DomainApiUtils;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 *
 * Bridge to product sku price.
 *
* User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 * */
public class SkuPriceBridge implements FieldBridge {

    private final BigDecimalBridge moneyBridge = new BigDecimalBridge(Constants.DEFAULT_SCALE);

    /** {@inheritDoc} */
    public void set(final String proposedFiledName, final Object value, final Document document, final LuceneOptions luceneOptions) {

        if (!(value instanceof ProductSku)) {
            return;
        }

        final boolean isShowRoom = ((ProductSku) value).getProduct().getAvailability() == Product.AVAILABILITY_SHOWROOM;

        final SkuPriceRelationshipSupport support = getSkuPriceRelationshipSupport();
        final List<SkuPrice> allPrices = support.getSkuPrices(((ProductSku) value).getCode());

        Set<Long> availableIn = null;
        if (!allPrices.isEmpty()) {

            final Map<Long, Map<String, SkuPrice>> lowestQuantityPrice = new HashMap<Long, Map<String, SkuPrice>>();
            final long time = System.currentTimeMillis();
            for (Object obj : (Collection)allPrices) {
                SkuPrice skuPrice = (SkuPrice) obj;

                if (!DomainApiUtils.isObjectAvailableNow(true, skuPrice.getSalefrom(), skuPrice.getSaleto(), time)) {
                    continue; // This price is not active
                }

                final Map<String, SkuPrice> lowestQuantityPriceByShop = lowestQuantityPrice.get(skuPrice.getShop().getShopId());
                if (lowestQuantityPriceByShop == null) {
                    // if we do not have a "byShop" this is the new lowest price
                    final Map<String, SkuPrice> newLowestQuantity = new HashMap<String, SkuPrice>();
                    newLowestQuantity.put(skuPrice.getCurrency(), skuPrice);
                    lowestQuantityPrice.put(skuPrice.getShop().getShopId(), newLowestQuantity);
                } else {
                    final SkuPrice oldLowestQuantity = lowestQuantityPriceByShop.get(skuPrice.getCurrency());
                    if (oldLowestQuantity == null) {
                        // if we do not have the lowest for this shop for this currency just add it
                        lowestQuantityPriceByShop.put(skuPrice.getCurrency(), skuPrice);
                    } else {
                        final int compare = oldLowestQuantity.getQuantity().compareTo(skuPrice.getQuantity());
                        if (compare > 0 || (compare == 0 && MoneyUtils.isFirstBiggerThanSecond(
                                MoneyUtils.minPositive(oldLowestQuantity.getRegularPrice(), oldLowestQuantity.getSalePrice()),
                                MoneyUtils.minPositive(skuPrice.getRegularPrice(), skuPrice.getSalePrice())
                        ))) {
                            // if this sku price has lower quantity then this is probably better starting price
                            // if the quantity is the same lower price is more appealing to show
                            lowestQuantityPriceByShop.put(skuPrice.getCurrency(), skuPrice);
                        }
                    }
                }
            }

            if (!lowestQuantityPrice.isEmpty()) {
                availableIn = new HashSet<Long>();
                for (final Map.Entry<Long, Map<String, SkuPrice>> shop : lowestQuantityPrice.entrySet()) {
                    for (final Map.Entry<String, SkuPrice> currency : shop.getValue().entrySet()) {

                        BigDecimal price = MoneyUtils.minPositive(currency.getValue().getRegularPrice(), currency.getValue().getSalePrice());
                        Pair<String, String> rez = objectToString(shop.getKey(), currency.getKey(), price);

                        Field facetField = new Field(
                                rez.getFirst(),
                                rez.getSecond(),
                                Field.Store.NO,
                                Field.Index.NOT_ANALYZED,
                                luceneOptions.getTermVector()
                        );
                        document.add(facetField);

                        final Set<Shop> subs = support.getAllShopsAndSubs().get(shop.getKey());

                        if (CollectionUtils.isNotEmpty(subs)) {

                            for (final Shop subShop : subs) {

                                if (!subShop.isB2BStrictPriceActive()) {

                                    rez = objectToString(subShop.getShopId(), currency.getKey(), price);

                                    Field subFacetField = new Field(
                                            rez.getFirst(),
                                            rez.getSecond(),
                                            Field.Store.NO,
                                            Field.Index.NOT_ANALYZED,
                                            luceneOptions.getTermVector()
                                    );
                                    document.add(subFacetField);

                                    // Note that price is inherited for this sub shop
                                    availableIn.add(subShop.getShopId());

                                }
                            }

                        }

                    }

                    // Note that price is set for this shop
                    availableIn.add(shop.getKey());

                }
            }

        }

        if (isShowRoom) {
            for (final Shop shop : support.getAll()) {

                // Fill in PK's for all shops as showroom products are visible regardless of price.
                document.add(new Field(
                        ProductSearchQueryBuilder.PRODUCT_SHOP_HASPRICE_FIELD,
                        String.valueOf(shop.getShopId()),
                        Field.Store.NO,
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));

            }
        } else if (CollectionUtils.isNotEmpty(availableIn)) {

            for (final Long shopId : availableIn) {
                // Fill in PK's for all shops that have price entries.
                document.add(new Field(
                        ProductSearchQueryBuilder.PRODUCT_SHOP_HASPRICE_FIELD,
                        String.valueOf(shopId),
                        Field.Store.NO,
                        Field.Index.NOT_ANALYZED,
                        luceneOptions.getTermVector()
                ));
            }

        }
    }


    /**
     * Create index value for given shop currency and price.
     *
     * @param shopId shop id
     * @param currency currency code
     * @param regularPrice regular price
     *
     * @return pair where first is field name and second is the string representation of price. Field has format facet_price_shopid_currency.
     *         All digital value will be left padded according to formatter.
     */
    public Pair<String, String> objectToString(final long shopId, final String currency, final BigDecimal regularPrice) {
        return new Pair<String, String>("facet_price_" + shopId + "_" + currency, moneyBridge.objectToString(regularPrice));
    }

    private SkuPriceRelationshipSupport getSkuPriceRelationshipSupport() {
        return HibernateSearchBridgeStaticLocator.getSkuPriceRelationshipSupport();
    }

}
