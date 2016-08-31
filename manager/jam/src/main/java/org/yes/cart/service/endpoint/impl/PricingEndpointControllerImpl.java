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
import org.yes.cart.service.endpoint.PricingEndpointController;
import org.yes.cart.service.vo.VoPriceService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 14:48
 */
@Component
public class PricingEndpointControllerImpl implements PricingEndpointController {

    private final VoPriceService voPriceService;

    @Autowired
    public PricingEndpointControllerImpl(final VoPriceService voPriceService) {
        this.voPriceService = voPriceService;
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
}
