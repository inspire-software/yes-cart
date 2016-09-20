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
import org.yes.cart.domain.vo.VoPriceList;
import org.yes.cart.domain.vo.VoTax;
import org.yes.cart.domain.vo.VoTaxConfig;
import org.yes.cart.service.endpoint.PricingEndpointController;
import org.yes.cart.service.vo.VoPriceService;
import org.yes.cart.service.vo.VoTaxService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 14:48
 */
@Component
public class PricingEndpointControllerImpl implements PricingEndpointController {

    private final VoPriceService voPriceService;
    private final VoTaxService voTaxService;

    @Autowired
    public PricingEndpointControllerImpl(final VoPriceService voPriceService,
                                         final VoTaxService voTaxService) {
        this.voPriceService = voPriceService;
        this.voTaxService = voTaxService;
    }

    @Override
    public @ResponseBody List<VoPriceList> getFilteredPriceLists(@PathVariable("shopId") final long shopId, @PathVariable("currency") final String currency, @RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
        return voPriceService.getFiltered(shopId, currency, filter, max);
    }

    @Override
    public @ResponseBody VoPriceList getPriceListById(@PathVariable("id") final long id) throws Exception {
        return voPriceService.getById(id);
    }

    @Override
    public @ResponseBody VoPriceList createPriceList(@RequestBody final VoPriceList vo) throws Exception {
        return voPriceService.create(vo);
    }

    @Override
    public @ResponseBody VoPriceList updatePriceList(@RequestBody final VoPriceList vo) throws Exception {
        return voPriceService.update(vo);
    }

    @Override
    public @ResponseBody void removePriceList(@PathVariable("id") final long id) throws Exception {
        voPriceService.remove(id);
    }


    @Override
    public @ResponseBody List<VoTax> getFilteredTax(@PathVariable("shopCode") final String shopCode, @PathVariable("currency") final String currency, @RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
        return voTaxService.getFilteredTax(shopCode, currency, filter, max);
    }

    @Override
    public @ResponseBody VoTax getTaxById(@PathVariable("id") final long id) throws Exception {
        return voTaxService.getTaxById(id);
    }

    @Override
    public @ResponseBody VoTax createTax(@RequestBody final VoTax vo) throws Exception {
        return voTaxService.createTax(vo);
    }

    @Override
    public @ResponseBody VoTax updateTax(@RequestBody final VoTax vo) throws Exception {
        return voTaxService.updateTax(vo);
    }

    @Override
    public @ResponseBody void removeTax(@PathVariable("id") final long id) throws Exception {
        voTaxService.removeTax(id);
    }

    @Override
    public @ResponseBody List<VoTaxConfig> getFilteredTaxConfig(@PathVariable("taxId") final long taxId, @RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
        return voTaxService.getFilteredTaxConfig(taxId, filter, max);
    }

    @Override
    public @ResponseBody VoTaxConfig getTaxConfigById(@PathVariable("id") final long id) throws Exception {
        return voTaxService.getTaxConfigById(id);
    }

    @Override
    public @ResponseBody VoTaxConfig createTaxConfig(@RequestBody final VoTaxConfig vo) throws Exception {
        return voTaxService.createTaxConfig(vo);
    }

    @Override
    public @ResponseBody void removeTaxConfig(@PathVariable("id") final long id) throws Exception {
        voTaxService.removeTaxConfig(id);
    }
}
