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
import org.yes.cart.domain.vo.VoFulfilmentCentre;
import org.yes.cart.domain.vo.VoFulfilmentCentreInfo;
import org.yes.cart.domain.vo.VoInventory;
import org.yes.cart.domain.vo.VoShopFulfilmentCentre;
import org.yes.cart.service.endpoint.FulfilmentEndpointController;
import org.yes.cart.service.vo.VoFulfilmentService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 19/08/2016
 * Time: 11:57
 */
@Component
public class FulfilmentEndpointControllerImpl implements FulfilmentEndpointController {

    private final VoFulfilmentService voFulfilmentService;

    @Autowired
    public FulfilmentEndpointControllerImpl(final VoFulfilmentService voFulfilmentService) {
        this.voFulfilmentService = voFulfilmentService;
    }

    @Override
    public @ResponseBody List<VoFulfilmentCentre> getAllFulfilmentCentres() throws Exception {
        return voFulfilmentService.getAllFulfilmentCentres();
    }

    @Override
    public @ResponseBody List<VoShopFulfilmentCentre> getShopFulfilmentCentres(@PathVariable("id") final long shopId) throws Exception {
        return voFulfilmentService.getShopFulfilmentCentres(shopId);
    }

    @Override
    public @ResponseBody VoFulfilmentCentre getFulfilmentCentreById(@PathVariable("id") final long id) throws Exception {
        return voFulfilmentService.getFulfilmentCentreById(id);
    }

    @Override
    public @ResponseBody VoFulfilmentCentre createFulfilmentCentre(@RequestBody final VoFulfilmentCentreInfo vo) throws Exception {
        return voFulfilmentService.createFulfilmentCentre(vo);
    }

    @Override
    public @ResponseBody VoFulfilmentCentre createShopFulfilmentCentre(@RequestBody final VoFulfilmentCentreInfo vo,@PathVariable("id")  final long shopId) throws Exception {
        return voFulfilmentService.createShopFulfilmentCentre(vo, shopId);
    }

    @Override
    public @ResponseBody VoFulfilmentCentre updateFulfilmentCentre(@RequestBody final VoFulfilmentCentre vo) throws Exception {
        return voFulfilmentService.updateFulfilmentCentre(vo);
    }

    @Override
    public @ResponseBody List<VoShopFulfilmentCentre> updateShopFulfilmentCentres(@RequestBody final List<VoShopFulfilmentCentre> vo) throws Exception {
        return voFulfilmentService.updateShopFulfilmentCentres(vo);
    }

    @Override
    public @ResponseBody void removeFulfilmentCentre(@PathVariable("id") final long id) throws Exception {
        voFulfilmentService.removeFulfilmentCentre(id);
    }


    @Override
    public @ResponseBody List<VoInventory> getFilteredInventory(@PathVariable("id") final long centreId, @RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
        return voFulfilmentService.getFilteredInventory(centreId, filter, max);
    }

    @Override
    public @ResponseBody VoInventory getInventoryById(@PathVariable("id") final long id) throws Exception {
        return voFulfilmentService.getInventoryById(id);
    }

    @Override
    public @ResponseBody VoInventory createInventory(@RequestBody final VoInventory vo) throws Exception {
        return voFulfilmentService.createInventory(vo);
    }

    @Override
    public @ResponseBody VoInventory updateInventory(@RequestBody final VoInventory vo) throws Exception {
        return voFulfilmentService.updateInventory(vo);
    }

    @Override
    public @ResponseBody void removeInventory(@PathVariable("id") final long id) throws Exception {
        voFulfilmentService.removeInventory(id);
    }
}
