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

package org.yes.cart.service.vo.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.vo.VoValidationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * User: inspiresoftware
 * Date: 29/08/2016
 * Time: 15:31
 */
public class VoValidationServiceCustomerLoginImpl extends AbstractVoValidationServiceSubjectCodeFieldImpl implements VoValidationService {

    private final CustomerService customerService;

    public VoValidationServiceCustomerLoginImpl(final CustomerService customerService) {
        super(Pattern.compile(".{8,255}", Pattern.CASE_INSENSITIVE));
        this.customerService = customerService;
    }

    @Override
    protected Long getDuplicateId(final long currentId, final String valueToCheck, final Map<String, String> context) {
        final List<Pair<Customer, Shop>> customersAndShops = this.customerService.findCustomersByLogin(valueToCheck);
        if (customersAndShops.isEmpty()) {
            return null;
        }
        final List<String> shopCodes = extractShopCodes(context);
        for (final Pair<Customer, Shop> customerAndShop : customersAndShops) {
            if (customerAndShop.getFirst().getCustomerId() != currentId) {
                if (shopCodes.contains(customerAndShop.getSecond().getCode()) ||
                        (customerAndShop.getSecond().getMaster() != null && shopCodes.contains(customerAndShop.getSecond().getMaster().getCode()))) {
                    return customerAndShop.getFirst().getCustomerId();
                }
            }
        }

        return null;
    }

    private List<String> extractShopCodes(final Map<String, String> context) {
        if (context != null) {
            final String[] shopCodesArray = StringUtils.split(context.get("shopCodes"), ',');
            if (shopCodesArray != null) {
                return Arrays.asList(shopCodesArray);
            }
        }
        return Collections.emptyList();
    }
}
