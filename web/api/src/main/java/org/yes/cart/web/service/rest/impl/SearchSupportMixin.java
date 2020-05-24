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

package org.yes.cart.web.service.rest.impl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.PriceModel;
import org.yes.cart.domain.entity.ProductAvailabilityModel;
import org.yes.cart.domain.entity.QuantityModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.ro.*;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.support.service.CurrencySymbolService;
import org.yes.cart.web.support.service.ProductServiceFacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 14/09/2019
 * Time: 16:34
 */
@Service("searchSupportMixin")
public class SearchSupportMixin extends RoMappingMixin {

    @Autowired
    private ProductServiceFacade productServiceFacade;
    @Autowired
    private CurrencySymbolService currencySymbolService;

    public List<ProductSearchResultRO> map(final List<ProductSearchResultDTO> searchResults,
                                           final ShoppingCart cart) {

        final List<ProductSearchResultRO> ros = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(searchResults)) {

            final long customerShopId = cart.getShoppingContext().getCustomerShopId();

            final Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(cart.getCurrencyCode());

            final String symbolAbr = symbol.getFirst();
            final String symbolPos = symbol.getSecond() != null && symbol.getSecond() ? "after" : "before";

            for (final ProductSearchResultDTO hit : searchResults) {

                final ProductSearchResultRO ro = map(hit, ProductSearchResultRO.class, ProductSearchResultDTO.class);

                final ProductAvailabilityModel prodPam = productServiceFacade.getProductAvailability(hit, customerShopId);
                final ProductAvailabilityModelRO prodAmRo = map(prodPam, ProductAvailabilityModelRO.class, ProductAvailabilityModel.class);
                ro.setProductAvailabilityModel(prodAmRo);

                final PriceModel productPrice = productServiceFacade.getSkuPrice(
                        cart,
                        null,
                        prodPam.getFirstAvailableSkuCode(),
                        BigDecimal.ONE,
                        hit.getFulfilmentCentreCode()
                );

                final SkuPriceRO priceRo = map(productPrice, SkuPriceRO.class, PriceModel.class);
                priceRo.setSymbol(symbolAbr);
                priceRo.setSymbolPosition(symbolPos);

                ro.setPrice(priceRo);

                if (ro.getSkus() != null) {

                    final Map<Long, ProductSkuSearchResultDTO> dtoMap = hit.getSearchSkus().stream().collect(Collectors.toMap(ProductSkuSearchResultDTO::getId, dto -> dto));

                    for (final ProductSkuSearchResultRO roSku : ro.getSkus()) {

                        final ProductSkuSearchResultDTO dtoSku = dtoMap.get(roSku.getId());

                        final ProductAvailabilityModel skuPam = productServiceFacade.getProductAvailability(dtoSku, customerShopId);
                        final ProductAvailabilityModelRO skuAmRo = map(skuPam, ProductAvailabilityModelRO.class, ProductAvailabilityModel.class);
                        roSku.setSkuAvailabilityModel(skuAmRo);

                        final BigDecimal cartQty = cart.getProductSkuQuantity(dtoSku.getFulfilmentCentreCode(), dtoSku.getCode());
                        final QuantityModel wlQm = productServiceFacade.getProductQuantity(cartQty, dtoSku, customerShopId);
                        final ProductQuantityModelRO wlQmRo = map(wlQm, ProductQuantityModelRO.class, QuantityModel.class);
                        roSku.setSkuQuantityModel(wlQmRo);

                        final PriceModel skuPrice = productServiceFacade.getSkuPrice(
                                cart,
                                null,
                                roSku.getCode(),
                                BigDecimal.ONE,
                                skuPam.getSupplier()
                        );

                        final SkuPriceRO skuPriceRo = map(skuPrice, SkuPriceRO.class, PriceModel.class);
                        skuPriceRo.setSymbol(symbolAbr);
                        skuPriceRo.setSymbolPosition(symbolPos);

                        roSku.setPrice(skuPriceRo);

                    }
                }

                ros.add(ro);

            }
        }

        return ros;

    }


}
