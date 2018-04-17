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

package org.yes.cart.promotion.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.promotion.PromotionCouponCodeGenerator;
import org.yes.cart.service.domain.ShopService;

/**
 * User: denispavlov
 * Date: 06/06/2014
 * Time: 10:11
 */
public class PromotionCouponCodeGeneratorImpl implements PromotionCouponCodeGenerator {

    private final int length;
    private final ShopService shopService;

    public PromotionCouponCodeGeneratorImpl(final int length, final ShopService shopService) {
        this.length = length;
        this.shopService = shopService;
    }

    /** {@inheritDoc} */
    @Override
    public String generate(final String shopCode) {

        final Shop shop = this.shopService.getShopByCode(shopCode);

        int size = determineCouponSize(shop);

        return shopCode.concat(RandomStringUtils.randomAlphanumeric(size)).toUpperCase();
    }


    private int determineCouponSize(final Shop shop) {

        if (shop != null) {
            final String av = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_COUPON_CODE_LENGTH);

            if (av != null && StringUtils.isNotBlank(av)) {
                int size = NumberUtils.toInt(av);
                if (size >= 5) {
                    return size;
                }
            }
        }
        return this.length;

    }



}
