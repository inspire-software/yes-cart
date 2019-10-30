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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.web.support.service.ManagerServiceFacade;
import org.yes.cart.web.support.utils.HttpUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 27/10/2019
 * Time: 11:03
 */
public class ManagerServiceFacadeImpl implements ManagerServiceFacade {

    private final CustomerService customerService;
    private final ShopService shopService;


    public ManagerServiceFacadeImpl(final CustomerService customerService,
                                    final ShopService shopService) {
        this.customerService = customerService;
        this.shopService = shopService;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getItemsPerPageOptionsConfig(final long customerShopId) {

        return getCSVConfig(
                customerShopId,
                AttributeNamesKeys.Shop.SHOP_CATEGORY_ITEMS_PER_PAGE,
                Constants.DEFAULT_ITEMS_ON_PAGE);

    }

    /** {@inheritDoc} */
    @Override
    public List<String> getPageSortingOptionsConfig(final long customerShopId) {

        return getCSVConfig(
                customerShopId,
                AttributeNamesKeys.Shop.SHOP_CUSTOMER_SORT_OPTIONS,
                Constants.DEFAULT_PAGE_SORT_CUSTOMER);

    }


    private static final String[] SHOP_CUSTOMERLIST_IMAGE_SIZE =
            new String[] {
                    AttributeNamesKeys.Shop.SHOP_CUSTOMER_IMAGE_WIDTH,
                    AttributeNamesKeys.Shop.SHOP_CUSTOMER_IMAGE_HEIGHT
            };

    private static final Pair<String, String> DEFAULT_CUSTOMERLIST_IMAGE_SIZE =
            new Pair<>(Constants.DEFAULT_CUSTOMERLIST_IMAGE_SIZE[0], Constants.DEFAULT_CUSTOMERLIST_IMAGE_SIZE[1]);


    /** {@inheritDoc} */
    @Override
    public Pair<String, String> getCustomerListImageSizeConfig(final long customerShopId) {

        return getImageSizeConfig(customerShopId, SHOP_CUSTOMERLIST_IMAGE_SIZE, DEFAULT_CUSTOMERLIST_IMAGE_SIZE);

    }


    /** {@inheritDoc} */
    @Override
    public int getCustomerListColumnOptionsConfig(final long customerShopId) {

        return getLimitSizeConfig(customerShopId,
                AttributeNamesKeys.Shop.SHOP_CUSTOMER_RECORDS_COLUMNS,
                Constants.CUSTOMER_COLUMNS_SIZE);

    }

    /** {@inheritDoc} */
    @Override
    public SearchResult<Customer> getCustomers(final SearchContext searchContext) {

        final long shopId = NumberUtils.toLong(HttpUtil.getSingleValue(searchContext.getParameters().get("shopId")));

        final Map<String, Object> filter = searchContext.collectSingleValueParameters(
                "any", "email", "firstname", "lastname", "companyName1", "companyName2", "tag"
        );

        final int pageSize = Math.min(searchContext.getSize(), 50);
        final int startIndex = searchContext.getStart() * pageSize;

        final int count = customerService.findCustomerCount(Collections.singleton(shopId), filter);
        if (count > startIndex) {

            final List<Customer> customer = customerService.findCustomer(startIndex, pageSize, searchContext.getSortBy(), searchContext.isSortDesc(), Collections.singleton(shopId), filter);
            return new SearchResult<>(searchContext, customer, count);

        }
        return new SearchResult<>(searchContext, Collections.emptyList(), count);
    }


    private List<String> getCSVConfig(final long shopId,
                                      final String shopCsvAttribute,
                                      final List<String> defaultCsv) {

        final Shop shop = shopService.getById(shopId);
        if (shop != null) {
            final String attrValueShop = shop.getAttributeValueByCode(shopCsvAttribute);
            if (StringUtils.isNotBlank(attrValueShop)) {
                return Arrays.asList(StringUtils.split(attrValueShop, ','));
            }
            if (shop.getMaster() != null) {
                final Shop master = shopService.getById(shop.getMaster().getShopId());
                final String attrValueShopMaster = master.getAttributeValueByCode(shopCsvAttribute);
                if (StringUtils.isNotBlank(attrValueShopMaster)) {
                    return Arrays.asList(StringUtils.split(attrValueShopMaster, ','));
                }
            }
        }
        return defaultCsv;

    }


    private int getLimitSizeConfig(final long shopId,
                                   final String shopLimitAttribute,
                                   final int defaultLimit) {

        final Shop shop = shopService.getById(shopId);
        if (shop != null) {
            final String val = shop.getAttributeValueByCode(shopLimitAttribute);
            if (StringUtils.isNotBlank(val)) {
                return NumberUtils.toInt(val, defaultLimit);
            }
            if (shop.getMaster() != null) {
                final Shop master = shopService.getById(shop.getMaster().getShopId());
                final String valMaster = master.getAttributeValueByCode(shopLimitAttribute);
                if (StringUtils.isNotBlank(valMaster)) {
                    return NumberUtils.toInt(valMaster, defaultLimit);
                }
            }
        }

        return defaultLimit;
    }


    private Pair<String, String> getImageSizeConfig(final long shopId,
                                                    final String[] shopWidthAndHeightAttribute,
                                                    final Pair<String, String> defaultWidthAndHeight) {

        final Shop shop = shopService.getById(shopId);
        if (shop != null) {
            final String widthAttrValueShop = shop.getAttributeValueByCode(shopWidthAndHeightAttribute[0]);
            final String heightAttrValueShop = shop.getAttributeValueByCode(shopWidthAndHeightAttribute[1]);
            if (StringUtils.isNotBlank(widthAttrValueShop) && StringUtils.isNotBlank(heightAttrValueShop)) {
                return new Pair<>(widthAttrValueShop, heightAttrValueShop);
            }
            if (shop.getMaster() != null) {
                final Shop master = shopService.getById(shop.getMaster().getShopId());
                final String widthAttrValueShopMaster = master.getAttributeValueByCode(shopWidthAndHeightAttribute[0]);
                final String heightAttrValueShopMaster = master.getAttributeValueByCode(shopWidthAndHeightAttribute[1]);
                if (StringUtils.isNotBlank(widthAttrValueShopMaster) && StringUtils.isNotBlank(heightAttrValueShopMaster)) {
                    return new Pair<>(widthAttrValueShopMaster, heightAttrValueShopMaster);
                }
            }
        }

        return defaultWidthAndHeight;
    }

}
