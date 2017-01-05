/*
 * Copyright 2009- 2016 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.domain.vo.VoCarrier;
import org.yes.cart.domain.vo.VoCarrierLocale;
import org.yes.cart.domain.vo.VoCarrierSla;
import org.yes.cart.domain.vo.VoShopCarrier;
import org.yes.cart.service.endpoint.ShippingEndpointController;
import org.yes.cart.service.vo.VoShippingService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 29/07/2016
 * Time: 10:48
 */
@Component
public class ShippingEndpointControllerImpl implements ShippingEndpointController {

    private final VoShippingService voShippingService;

    @Autowired
    public ShippingEndpointControllerImpl(final VoShippingService voShippingService) {
        this.voShippingService = voShippingService;
    }

    @Override
    public @ResponseBody List<VoCarrier> getAllCarriers() throws Exception {
        return voShippingService.getAllCarriers();
    }

    @Override
    public @ResponseBody List<VoShopCarrier> getShopCarriers(@PathVariable("id") final long shopId) throws Exception {
        return voShippingService.getShopCarriers(shopId);
    }

    @Override
    public @ResponseBody VoCarrier getCarrierById(@PathVariable("id") final long id) throws Exception {
        return voShippingService.getCarrierById(id);
    }

    @Override
    public @ResponseBody VoCarrier createCarrier(@RequestBody final VoCarrierLocale vo) throws Exception {
        return voShippingService.createCarrier(vo);
    }

    @Override
    public @ResponseBody VoCarrier createShopCarrier(@RequestBody final VoCarrierLocale vo, @PathVariable("id") final long shopId) throws Exception {
        return voShippingService.createShopCarrier(vo, shopId);
    }

    @Override
    public @ResponseBody VoCarrier updateCarrier(@RequestBody final VoCarrier vo) throws Exception {
        return voShippingService.updateCarrier(vo);
    }

    @Override
    public @ResponseBody List<VoShopCarrier> updateShopCarriers(@RequestBody final List<VoShopCarrier> vo) throws Exception {
        return voShippingService.updateShopCarriers(vo);
    }

    @Override
    public @ResponseBody void removeCarrier(@PathVariable("id") final long id) throws Exception {
        voShippingService.removeCarrier(id);
    }

    @Override
    public @ResponseBody List<VoCarrierSla> getCarrierSlas(@PathVariable("id") final long carrierId) throws Exception {
        return voShippingService.getCarrierSlas(carrierId);
    }

    @Override
    public @ResponseBody List<VoCarrierSla> getFilteredCarrierSlas(@RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
        return voShippingService.getFilteredCarrierSlas(filter, max);
    }

    @Override
    public @ResponseBody VoCarrierSla getCarrierSlaById(@PathVariable("id") final long id) throws Exception {
        return voShippingService.getCarrierSlaById(id);
    }

    @Override
    public @ResponseBody VoCarrierSla createCarrierSla(@RequestBody final VoCarrierSla vo) throws Exception {
        return voShippingService.createCarrierSla(vo);
    }

    @Override
    public @ResponseBody VoCarrierSla updateCarrierSla(@RequestBody final VoCarrierSla vo) throws Exception {
        return voShippingService.updateCarrierSla(vo);
    }

    @Override
    public @ResponseBody void removeCarrierSla(@PathVariable("id") final long id) throws Exception {
        voShippingService.removeCarrierSla(id);
    }
}
