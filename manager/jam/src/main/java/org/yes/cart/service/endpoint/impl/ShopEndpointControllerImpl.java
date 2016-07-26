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
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.endpoint.ShopEndpointController;
import org.yes.cart.service.vo.VoShopCategoryService;
import org.yes.cart.service.vo.VoShopService;

import java.util.List;

/**
 * Created by Igor_Azarny on 3/28/2016.
 */
@Component
public class ShopEndpointControllerImpl implements ShopEndpointController {


    private final VoShopService voShopService;
    private final VoShopCategoryService voShopCategoryService;

    @Autowired
    public ShopEndpointControllerImpl(final VoShopService voShopService,
                                      final VoShopCategoryService voShopCategoryService) {
        this.voShopCategoryService = voShopCategoryService;
        this.voShopService = voShopService;
    }

    public @ResponseBody
    List<VoShop> getAll() throws Exception {
        return voShopService.getAll();
    }

    public @ResponseBody
    VoShop getById(@PathVariable("id") long id) throws Exception {
        return voShopService.getById(id);
    }

    public @ResponseBody
    VoShop create(@RequestBody VoShop voShop) throws Exception {
        return voShopService.create(voShop);
    }

    public @ResponseBody
    VoShop update(@RequestBody VoShop voShop) throws Exception {
        return voShopService.update(voShop);
    }

    public @ResponseBody
    void remove(@PathVariable("id") long id) throws Exception {
        voShopService.remove(id);
    }

    public @ResponseBody
    VoShopLocale getLocalization(@PathVariable("shopId") long shopId) throws Exception {
        return voShopService.getShopLocale(shopId);
    }

    public @ResponseBody
    VoShopLocale update(@RequestBody VoShopLocale voShopLocale) throws Exception {
        return voShopService.update(voShopLocale);
    }

    public @ResponseBody
    VoShopUrl getUrl(@PathVariable("shopId") long shopId) throws Exception {
        return voShopService.getShopUrls(shopId);
    }

    public @ResponseBody
    VoShopUrl update(@RequestBody VoShopUrl voShopUrl)  throws Exception {
        return voShopService.update(voShopUrl);
    }

    public @ResponseBody
    VoShopSupportedCurrencies getCurrency(@PathVariable("shopId") long shopId) throws Exception {
        return voShopService.getShopCurrencies(shopId);
    }

    public @ResponseBody
    VoShopSupportedCurrencies update(@RequestBody VoShopSupportedCurrencies supportedCurrencies) throws Exception {
        return voShopService.update(supportedCurrencies);
    }

    public @ResponseBody
    VoShopLanguages getLanguage(@PathVariable("shopId") long shopId) throws Exception {
        return voShopService.getShopLanguages(shopId);
    }

    public @ResponseBody
    VoShopLanguages update(@RequestBody VoShopLanguages langs) throws Exception {
        return voShopService.update(langs);
    }

    public @ResponseBody
    List<VoCategory> getCategories(@PathVariable("shopId") long shopId) throws Exception {
        return voShopCategoryService.getAllByShopId(shopId);
    }

    public @ResponseBody
    List<VoCategory> update(@PathVariable("shopId") long shopId, @RequestBody List<VoCategory> voCategories) throws Exception {
        return voShopCategoryService.update(shopId, voCategories);
    }

    public @ResponseBody
    VoShop updateDisabledFlag(@PathVariable("shopId") final long shopId, @PathVariable("state") final boolean state) throws Exception {
        return voShopService.updateDisabledFlag(shopId, state);
    }
}
