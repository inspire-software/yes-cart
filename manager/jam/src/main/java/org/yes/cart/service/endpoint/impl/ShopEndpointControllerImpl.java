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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.endpoint.ShopEndpointController;
import org.yes.cart.service.vo.*;

import java.util.List;

/**
 * Created by Igor_Azarny on 3/28/2016.
 */
@Component
public class ShopEndpointControllerImpl implements ShopEndpointController {


    private final VoShopService voShopService;
    private final VoShopCategoryService voShopCategoryService;
    private final VoShippingService voShippingService;
    private final VoFulfilmentService voFulfilmentService;
    private final VoPaymentGatewayService voPaymentGatewayService;
    private final VoContentService voContentService;

    @Autowired
    public ShopEndpointControllerImpl(final VoShopService voShopService,
                                      final VoShopCategoryService voShopCategoryService,
                                      final VoShippingService voShippingService,
                                      final VoFulfilmentService voFulfilmentService,
                                      final VoPaymentGatewayService voPaymentGatewayService,
                                      final VoContentService voContentService) {
        this.voShopCategoryService = voShopCategoryService;
        this.voShopService = voShopService;
        this.voShippingService = voShippingService;
        this.voFulfilmentService = voFulfilmentService;
        this.voPaymentGatewayService = voPaymentGatewayService;
        this.voContentService = voContentService;
    }

    @Override
    public @ResponseBody
    List<VoShop> getAll() throws Exception {
        return voShopService.getAll();
    }

    @Override
    public @ResponseBody
    List<VoShop> getAllSubs(@PathVariable("id") final long id) throws Exception {
        return voShopService.getAllSubs(id);
    }

    @Override
    public @ResponseBody
    VoShop getById(@PathVariable("id") final long id) throws Exception {
        return voShopService.getById(id);
    }

    @Override
    public @ResponseBody
    VoShop create(@RequestBody final VoShop voShop) throws Exception {
        return voShopService.create(voShop);
    }

    @Override
    public @ResponseBody
    VoShop createSub(@RequestBody final VoSubShop voShop) throws Exception {
        return voShopService.createSub(voShop);
    }

    @Override
    public @ResponseBody
    VoShop update(@RequestBody final VoShop voShop) throws Exception {
        return voShopService.update(voShop);
    }

    @Override
    public @ResponseBody
    void remove(@PathVariable("id") final long id) throws Exception {
        voShopService.remove(id);
    }

    @Override
    public @ResponseBody
    VoShopSummary getSummary(@PathVariable("id") final long id, @RequestParam("lang") final String lang) throws Exception {

        final VoShopSummary summary = new VoShopSummary();
        voShopService.fillShopSummaryDetails(summary, id, lang);
        voShippingService.fillShopSummaryDetails(summary, summary.getShopId(), lang);
        voShopCategoryService.fillShopSummaryDetails(summary, summary.getShopId(), lang);
        voFulfilmentService.fillShopSummaryDetails(summary, summary.getShopId(), lang);
        voPaymentGatewayService.fillShopSummaryDetails(summary, summary.getMasterCode() != null ? summary.getMasterCode() : summary.getCode(), lang);
        voContentService.fillShopSummaryDetails(summary, summary.getMasterId() != null ? summary.getMasterId() : summary.getShopId(), lang);
        return summary;

    }

    @Override
    public @ResponseBody
    VoShopSeo getLocalization(@PathVariable("id") final long shopId) throws Exception {
        return voShopService.getShopLocale(shopId);
    }

    @Override
    public @ResponseBody
    VoShopSeo update(@RequestBody final VoShopSeo voShopSeo) throws Exception {
        return voShopService.update(voShopSeo);
    }

    @Override
    public @ResponseBody
    VoShopUrl getUrl(@PathVariable("id") final long shopId) throws Exception {
        return voShopService.getShopUrls(shopId);
    }

    @Override
    public @ResponseBody
    VoShopUrl update(@RequestBody final VoShopUrl voShopUrl)  throws Exception {
        return voShopService.update(voShopUrl);
    }

    @Override
    public @ResponseBody
    VoShopAlias getAlias(@PathVariable("id") final long shopId) throws Exception {
        return voShopService.getShopAliases(shopId);
    }

    @Override
    public @ResponseBody
    VoShopAlias update(@RequestBody final VoShopAlias voShopAlias) throws Exception {
        return voShopService.update(voShopAlias);
    }

    @Override
    public @ResponseBody
    VoShopSupportedCurrencies getCurrency(@PathVariable("id") final long shopId) throws Exception {
        return voShopService.getShopCurrencies(shopId);
    }

    @Override
    public @ResponseBody
    VoShopSupportedCurrencies update(@RequestBody final VoShopSupportedCurrencies supportedCurrencies) throws Exception {
        return voShopService.update(supportedCurrencies);
    }

    @Override
    public @ResponseBody
    VoShopLanguages getLanguage(@PathVariable("id") final long shopId) throws Exception {
        return voShopService.getShopLanguages(shopId);
    }

    @Override
    public @ResponseBody
    VoShopLanguages update(@RequestBody final VoShopLanguages langs) throws Exception {
        return voShopService.update(langs);
    }

    @Override
    public @ResponseBody
    VoShopLocations getLocation(@PathVariable("id") final long shopId) throws Exception {
        return voShopService.getShopLocations(shopId);
    }

    @Override
    public @ResponseBody
    VoShopLocations update(@RequestBody final VoShopLocations vo) throws Exception {
        return voShopService.update(vo);
    }

    @Override
    public @ResponseBody
    List<VoCategory> getCategories(@PathVariable("id") final long shopId) throws Exception {
        return voShopCategoryService.getAllByShopId(shopId);
    }

    @Override
    public @ResponseBody
    List<VoCategory> update(@PathVariable("id") final long shopId, @RequestBody final List<VoCategory> voCategories) throws Exception {
        return voShopCategoryService.update(shopId, voCategories);
    }

    @Override
    public @ResponseBody
    VoShop updateDisabledFlag(@PathVariable("id") final long shopId, @PathVariable("state") final VoShopStatus state) throws Exception {
        return voShopService.updateDisabledFlag(shopId, state.isDisabled());
    }

    @Override
    public  @ResponseBody
    List<VoAttrValueShop> getShopAttributes(@PathVariable("id") final long shopId, @RequestParam(value = "includeSecure", required = false) final boolean includeSecure) throws Exception {
        return voShopService.getShopAttributes(shopId, false);
    }

    @Override
    public  @ResponseBody
    List<VoAttrValueShop> getShopAttributesSecure(@PathVariable("id") final long shopId, @RequestParam(value = "includeSecure", required = false) final boolean includeSecure) throws Exception {
        return voShopService.getShopAttributes(shopId, includeSecure);
    }

    @Override
    public  @ResponseBody
    List<VoAttrValueShop> update(@RequestParam(value = "includeSecure", required = false) final boolean includeSecure, @RequestBody final List<MutablePair<VoAttrValueShop, Boolean>> vo) throws Exception {
        return voShopService.update(vo, includeSecure);
    }

}
