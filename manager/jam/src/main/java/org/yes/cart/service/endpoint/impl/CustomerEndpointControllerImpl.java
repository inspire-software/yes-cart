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
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoAttrValueCustomer;
import org.yes.cart.domain.vo.VoCustomer;
import org.yes.cart.domain.vo.VoCustomerInfo;
import org.yes.cart.service.endpoint.CustomerEndpointController;
import org.yes.cart.service.vo.VoCustomerService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 07/09/2016
 * Time: 09:06
 */
@Component
public class CustomerEndpointControllerImpl implements CustomerEndpointController {

    private final VoCustomerService voCustomerService;

    @Autowired
    public CustomerEndpointControllerImpl(final VoCustomerService voCustomerService) {
        this.voCustomerService = voCustomerService;
    }

    @Override
    public @ResponseBody
    List<VoCustomerInfo> getFilteredCustomer(@RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
        return voCustomerService.getFiltered(filter, max);
    }

    @Override
    public @ResponseBody
    VoCustomer getCustomerById(@PathVariable("id") final long id) throws Exception {
        return voCustomerService.getById(id);
    }

    @Override
    public @ResponseBody
    VoCustomer createCustomer(@RequestBody final VoCustomer vo) throws Exception {
        return voCustomerService.create(vo);
    }

    @Override
    public  @ResponseBody
    VoCustomer updateCustomer(@RequestBody final VoCustomer vo) throws Exception {
        return voCustomerService.update(vo);
    }

    @Override
    public @ResponseBody
    void removeCustomer(@PathVariable("id") final long id) throws Exception {
        voCustomerService.remove(id);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueCustomer> getCustomerAttributes(@PathVariable("customerId") final long customerId) throws Exception {
        return voCustomerService.getCustomerAttributes(customerId);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueCustomer> updateCustomer(@RequestBody final List<MutablePair<VoAttrValueCustomer, Boolean>> vo) throws Exception {
        return voCustomerService.update(vo);
    }

    @Override
    public @ResponseBody
    void resetPassword(@PathVariable("customerId") final long customerId, @PathVariable("shopId") final long shopId) throws Exception {
        voCustomerService.resetPassword(customerId, shopId);
    }
}
