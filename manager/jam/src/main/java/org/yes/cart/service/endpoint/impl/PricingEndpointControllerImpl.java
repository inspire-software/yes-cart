/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.cluster.ProductAsyncSupport;
import org.yes.cart.service.endpoint.PricingEndpointController;
import org.yes.cart.service.vo.VoPriceService;
import org.yes.cart.service.vo.VoPromotionService;
import org.yes.cart.service.vo.VoTaxService;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 14:48
 */
@Component
public class PricingEndpointControllerImpl implements PricingEndpointController {

    private final VoPriceService voPriceService;
    private final VoTaxService voTaxService;
    private final VoPromotionService voPromotionService;
    private final ProductAsyncSupport productAsyncSupport;


    @Autowired
    public PricingEndpointControllerImpl(final VoPriceService voPriceService,
                                         final VoTaxService voTaxService,
                                         final VoPromotionService voPromotionService,
                                         final ProductAsyncSupport productAsyncSupport) {
        this.voPriceService = voPriceService;
        this.voTaxService = voTaxService;
        this.voPromotionService = voPromotionService;
        this.productAsyncSupport = productAsyncSupport;
    }

    @Override
    public @ResponseBody
    VoSearchResult<VoPriceList> getFilteredPriceLists(@RequestBody final VoSearchContext filter) throws Exception {
        return voPriceService.getFilteredPrices(filter);
    }

    @Override
    public @ResponseBody
    VoPriceList getPriceListById(@PathVariable("id") final long id) throws Exception {
        return voPriceService.getPriceById(id);
    }

    @Override
    public @ResponseBody
    VoPriceList createPriceList(@RequestBody final VoPriceList vo) throws Exception {
        final VoPriceList price = voPriceService.createPrice(vo);
        productAsyncSupport.asyncIndexSku(vo.getSkuCode());
        return price;
    }

    @Override
    public @ResponseBody
    VoPriceList updatePriceList(@RequestBody final VoPriceList vo) throws Exception {
        final VoPriceList price = voPriceService.updatePrice(vo);
        productAsyncSupport.asyncIndexSku(vo.getSkuCode());
        return price;
    }

    @Override
    public @ResponseBody
    void removePriceList(@PathVariable("id") final long id) throws Exception {
        final VoPriceList price = voPriceService.getPriceById(id);
        if (price != null) {
            voPriceService.removePrice(id);
            productAsyncSupport.asyncIndexSku(price.getSkuCode());
        }
    }


    @Override
    public @ResponseBody
    VoSearchResult<VoTax> getFilteredTax(@RequestBody final VoSearchContext filter) throws Exception {
        return voTaxService.getFilteredTax(filter);
    }

    @Override
    public @ResponseBody
    VoTax getTaxById(@PathVariable("id") final long id) throws Exception {
        return voTaxService.getTaxById(id);
    }

    @Override
    public @ResponseBody
    VoTax createTax(@RequestBody final VoTax vo) throws Exception {
        return voTaxService.createTax(vo);
    }

    @Override
    public @ResponseBody
    VoTax updateTax(@RequestBody final VoTax vo) throws Exception {
        return voTaxService.updateTax(vo);
    }

    @Override
    public @ResponseBody
    void removeTax(@PathVariable("id") final long id) throws Exception {
        voTaxService.removeTax(id);
    }

    @Override
    public @ResponseBody
    VoSearchResult<VoTaxConfig> getFilteredTaxConfig(@RequestBody final VoSearchContext filter) throws Exception {
        return voTaxService.getFilteredTaxConfig(filter);
    }

    @Override
    public @ResponseBody
    VoTaxConfig getTaxConfigById(@PathVariable("id") final long id) throws Exception {
        return voTaxService.getTaxConfigById(id);
    }

    @Override
    public @ResponseBody
    VoTaxConfig createTaxConfig(@RequestBody final VoTaxConfig vo) throws Exception {
        return voTaxService.createTaxConfig(vo);
    }

    @Override
    public @ResponseBody
    void removeTaxConfig(@PathVariable("id") final long id) throws Exception {
        voTaxService.removeTaxConfig(id);
    }

    @Override
    public @ResponseBody
    VoSearchResult<VoPromotion> getFilteredPromotion(@RequestBody final VoSearchContext filter) throws Exception {
        return voPromotionService.getFilteredPromotion(filter);
    }

    @Override
    public @ResponseBody
    VoPromotion getPromotionById(@PathVariable("id") final long id) throws Exception {
        return voPromotionService.getPromotionById(id);
    }

    @Override
    public @ResponseBody
    VoPromotion createPromotion(@RequestBody final VoPromotion vo) throws Exception {
        return voPromotionService.createPromotion(vo);
    }

    @Override
    public @ResponseBody
    VoPromotion updatePromotion(@RequestBody final VoPromotion vo) throws Exception {
        return voPromotionService.updatePromotion(vo);
    }

    @Override
    public @ResponseBody
    void removePromotion(@PathVariable("id") final long id) throws Exception {
        voPromotionService.removePromotion(id);
    }

    @Override
    public @ResponseBody
    void updatePromotionDisabledFlag(@PathVariable("id") final long id, @RequestBody final VoPromotionStatus state) throws Exception {
        voPromotionService.updateDisabledFlag(id, state.isDisabled());
    }

    @Override
    public @ResponseBody
    VoSearchResult<VoPromotionCoupon> getFilteredPromotionCoupons(@RequestBody final VoSearchContext filter) throws Exception {
        return voPromotionService.getFilteredPromotionCoupons(filter);
    }

    @Override
    public @ResponseBody
    void createPromotionCoupons(@RequestBody final VoPromotionCoupon vo) throws Exception {
        voPromotionService.createPromotionCoupons(vo);
    }

    @Override
    public @ResponseBody
    void removePromotionCoupon(@PathVariable("id") final long id) throws Exception {
        voPromotionService.removePromotionCoupon(id);
    }

    @Override
    public @ResponseBody
    VoCart testPromotions(@RequestBody final VoPromotionTest testData) throws Exception {
        return voPromotionService.testPromotions(testData.getShopCode(), testData.getCurrency(), testData);
    }

}
