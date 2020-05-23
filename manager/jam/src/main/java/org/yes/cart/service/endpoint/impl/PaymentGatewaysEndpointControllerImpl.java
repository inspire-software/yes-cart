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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoPaymentGateway;
import org.yes.cart.domain.vo.VoPaymentGatewayInfo;
import org.yes.cart.domain.vo.VoPaymentGatewayParameter;
import org.yes.cart.domain.vo.VoPaymentGatewayStatus;
import org.yes.cart.service.endpoint.PaymentGatewaysEndpointController;
import org.yes.cart.service.vo.VoPaymentGatewayService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 01/08/2016
 * Time: 18:04
 */
@Component
public class PaymentGatewaysEndpointControllerImpl implements PaymentGatewaysEndpointController {

    private final VoPaymentGatewayService voPaymentGatewayService;

    @Autowired
    public PaymentGatewaysEndpointControllerImpl(final VoPaymentGatewayService voPaymentGatewayService) {
        this.voPaymentGatewayService = voPaymentGatewayService;
    }

    @Override
    public @ResponseBody List<VoPaymentGatewayInfo> getPaymentGateways(@RequestParam("lang") final String lang, @RequestParam("enabledOnly") final boolean enabledOnly) throws Exception {
        if (enabledOnly) {
            return voPaymentGatewayService.getAllowedPaymentGatewaysForShops(lang);
        }
        return voPaymentGatewayService.getPaymentGateways(lang);
    }

    @Override
    public @ResponseBody List<VoPaymentGatewayInfo> getPaymentGatewaysForShop(@RequestParam("lang") final String lang, @PathVariable("code") final String shopCode) throws Exception {
        return voPaymentGatewayService.getPaymentGatewaysForShop(lang, shopCode);
    }

    @Override
    public @ResponseBody List<VoPaymentGateway> getPaymentGatewaysWithParameters(@RequestParam("lang") final String lang, @RequestParam("includeSecure") final boolean includeSecure) throws Exception {
        return voPaymentGatewayService.getPaymentGatewaysWithParameters(lang, includeSecure);
    }

    @Override
    public @ResponseBody List<VoPaymentGateway> getPaymentGatewaysWithParametersForShop(@RequestParam("lang") final String lang, @PathVariable("code") final String shopCode, @RequestParam("includeSecure") final boolean includeSecure) throws Exception {
        return voPaymentGatewayService.getPaymentGatewaysWithParametersForShop(lang, shopCode, false);
    }

    @Override
    public @ResponseBody List<VoPaymentGateway> getPaymentGatewaysWithParametersForShopSecure(@RequestParam("lang") final String lang, @PathVariable("code") final String shopCode, @RequestParam("includeSecure") final boolean includeSecure) throws Exception {
        return voPaymentGatewayService.getPaymentGatewaysWithParametersForShop(lang, shopCode, true);
    }

    @Override
    public @ResponseBody List<VoPaymentGatewayParameter> update(@PathVariable("label") final String pgLabel, @RequestParam("includeSecure") final boolean includeSecure, @RequestBody final List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo) throws Exception {
        return voPaymentGatewayService.updateParameters(pgLabel, vo, includeSecure);
    }

    @Override
    public @ResponseBody List<VoPaymentGatewayParameter> update(@PathVariable("code") final String shopCode, @PathVariable("label") final String pgLabel, @RequestParam("includeSecure") final boolean includeSecure, @RequestBody final List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo) throws Exception {
        return voPaymentGatewayService.updateParameters(shopCode, pgLabel, vo, includeSecure);
    }

    @Override
    public @ResponseBody void updateDisabledFlag(@PathVariable("label") final String pgLabel, @RequestBody final VoPaymentGatewayStatus status) throws Exception {
        voPaymentGatewayService.updateDisabledFlag(pgLabel, status.isDisabled());
    }

    @Override
    public @ResponseBody void updateDisabledFlag(@PathVariable("code") final String shopCode, @PathVariable("label") final String pgLabel, @RequestBody final VoPaymentGatewayStatus status) throws Exception {
        voPaymentGatewayService.updateDisabledFlag(shopCode, pgLabel, status.isDisabled());
    }
}
