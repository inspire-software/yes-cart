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
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.endpoint.CustomerEndpointController;
import org.yes.cart.service.vo.VoAddressBookService;
import org.yes.cart.service.vo.VoCustomerService;
import org.yes.cart.service.vo.VoShopService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 07/09/2016
 * Time: 09:06
 */
@Component
public class CustomerEndpointControllerImpl implements CustomerEndpointController {

    private final VoCustomerService voCustomerService;
    private final VoShopService voShopService;
    private final VoAddressBookService voAddressBookService;

    @Autowired
    public CustomerEndpointControllerImpl(final VoCustomerService voCustomerService,
                                          final VoShopService voShopService,
                                          final VoAddressBookService voAddressBookService) {
        this.voCustomerService = voCustomerService;
        this.voShopService = voShopService;
        this.voAddressBookService = voAddressBookService;
    }

    @Override
    public @ResponseBody
    List<VoCustomerInfo> getFilteredCustomer(@RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
        return voCustomerService.getFiltered(filter, max);
    }

    @Override
    public @ResponseBody
    List<MutablePair<String, String>> getCustomerTypes(@PathVariable("lang") final String lang) throws Exception {
        return voShopService.getAvailableShopsCustomerTypes(lang);
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

    @Override
    public @ResponseBody
    VoAddressBook getAddressBook(@PathVariable("id") final long customerId, @PathVariable("formattingShopId") final long formattingShopId, @PathVariable("lang") final String lang) throws Exception {
        return voAddressBookService.getAddressBook(customerId, formattingShopId, lang);
    }

    @Override
    public @ResponseBody
    VoAddress createAddress(@RequestBody final VoAddress vo) throws Exception {
        return voAddressBookService.create(vo);
    }

    @Override
    public @ResponseBody
    VoAddress updateAddress(@RequestBody final VoAddress vo) throws Exception {
        return voAddressBookService.update(vo);
    }

    @Override
    public @ResponseBody
    void removeAddress(@PathVariable("id") final long id) throws Exception {
        voAddressBookService.remove(id);
    }
}
