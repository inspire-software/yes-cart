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
import org.apache.lucene.document.Fieldable;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.entity.bridge.support.ShopWarehouseRelationshipSupport;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 *
 * Bridge to product sku price.
 *
* User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 * */
public class SkuWarehouseBridge implements TwoWayFieldBridge {

    private final DecimalFormat formatter = new DecimalFormat(Constants.INVENTORY_FORMAT_TOINDEX);

    /** {@inheritDoc} */
    public void set(final String proposedFiledName, final Object value, final Document document, final LuceneOptions luceneOptions) {

        final ShopWarehouseRelationshipSupport support = getShopWarehouseRelationshipSupport();

        if (value instanceof Collection) {

            for (Object obj : (Collection) value) {

                final ProductSku sku = (ProductSku) obj;

                final List<Shop> shops = support.getAll();

                for (final Shop shop : shops) {

                    final Set<Warehouse> warehouses = support.getShopWarehouses(shop);
                    final BigDecimal qtyForShop = sku.getQty(warehouses);

                    String rez = objectToString(shop.getShopId(), sku.getCode(), qtyForShop);
                    Field field = new Field(
                            proposedFiledName,
                            rez,
                            Field.Store.YES,
                            Field.Index.NOT_ANALYZED,
                            luceneOptions.getTermVector()
                    );
                    document.add(field);


                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object get(final String name, final Document document) {
        final Fieldable[] fields = document.getFieldables(name);
        final Map<Long, Map<String, BigDecimal>> qty = new HashMap<Long, Map<String, BigDecimal>>();
        for (final Fieldable field : fields) {
            final String[] value = field.stringValue().split("_");
            final Long shopId = Long.valueOf(value[0]);
            final String sku = value[1];
            final BigDecimal qtyValue = new BigDecimal(value[2]).movePointLeft(Constants.INVENTORY_SCALE);
            final Map<String, BigDecimal> skuQty;
            if (qty.containsKey(shopId)) {
                skuQty = qty.get(shopId);
            } else {
                skuQty = new HashMap<String, BigDecimal>();
                qty.put(shopId, skuQty);
            }
            skuQty.put(sku, qtyValue);
        }
        return qty;
    }

    /** {@inheritDoc} */
    @Override
    public String objectToString(final Object object) {
        return String.valueOf(object);
    }

    /**
     * Create index value for given shop currency and price.
     * @param shopId shop id
     * @param quantity quantity for this shop
     * @return index value in following format shopid_skuCode_quantity. All digital value will be left padded according to formatter.
     */
    public String objectToString(final long shopId, final String skuCode, final BigDecimal quantity) {
        StringBuilder stringBuilder = new StringBuilder();
        long price = quantity.movePointRight(Constants.INVENTORY_SCALE).longValue();
        stringBuilder.append(formatter.format(shopId));
        stringBuilder.append('_');
        stringBuilder.append(skuCode);
        stringBuilder.append('_');
        stringBuilder.append(formatter.format(price));
        return  stringBuilder.toString();
    }

    private ShopWarehouseRelationshipSupport getShopWarehouseRelationshipSupport() {
        return HibernateSearchBridgeStaticLocator.getShopWarehouseRelationshipSupport();
    }

}
