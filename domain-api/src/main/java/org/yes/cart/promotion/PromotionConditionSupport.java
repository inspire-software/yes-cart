/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.promotion;

import org.yes.cart.domain.entity.Brand;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;

/**
 * Promotion condition support allows to proxy some of the complex conditions to core
 * services
 *
 * User: denispavlov
 * Date: 28/02/2018
 * Time: 21:32
 */
public interface PromotionConditionSupport {

    /**
     * Get full product for given SKU code. Common usage is to retrieve the full product for
     * cart item to check some specific details.
     *
     * @param sku code
     *
     * @return product or null if does not exist
     */
    Product getProductBySkuCode(String sku);

    /**
     * Get full product SKU for given SKU code. Common usage is to retrieve the full product for
     * cart item to check some specific details.
     *
     * @param sku code
     *
     * @return product SKU or null if does not exist
     */
    ProductSku getProductSkuByCode(String sku);

    /**
     * Get product brand by SKU code.  Common usage is to retrieve the full brand for
     * cart item to check some specific details.
     *
     * @param sku code
     *
     * @return brand or null if product cannot be retrieved
     */
    Brand getProductBrand(String sku);


    /**
     * Determine if given SKU has certain attribute.
     *
     * @param sku code
     * @param attribute attribute code
     *
     * @return true if given SKU has attribute
     */
    boolean hasProductAttribute(String sku, String attribute);

    /**
     * Retrieve attribute for given SKU.
     *
     * @param sku code
     * @param attribute attribute code
     *
     * @return SKU attribute value
     */
    String getProductAttribute(String sku, String attribute);


    /**
     * Determine if given SKU is of certain brand.
     *
     * @param sku code
     * @param brandNames array of brand names
     *
     * @return true if given SKU represents a product linked to specified brand.
     */
    boolean isProductOfBrand(String sku, String ... brandNames);

    /**
     * Determine if given SKU is in certain category with fallback to parent reachable in specified
     * shop.
     *
     * @param sku code
     * @param shopId shop
     * @param categoryGUIDs array of category GUID
     *
     * @return true if given SKU is inside one of the categories
     */
    boolean isProductInCategory(String sku, Long shopId, String ... categoryGUIDs);

}
