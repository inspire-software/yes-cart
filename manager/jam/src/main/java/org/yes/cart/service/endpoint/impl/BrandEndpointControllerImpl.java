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
import org.yes.cart.domain.vo.VoAttrValueBrand;
import org.yes.cart.domain.vo.VoBrand;
import org.yes.cart.service.endpoint.BrandEndpointController;
import org.yes.cart.service.vo.VoBrandService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 21/08/2016
 * Time: 16:43
 */
@Component
public class BrandEndpointControllerImpl implements BrandEndpointController {

    private final VoBrandService voBrandService;

    @Autowired
    public BrandEndpointControllerImpl(final VoBrandService voBrandService) {
        this.voBrandService = voBrandService;
    }

    public @ResponseBody
    List<VoBrand> getFiltered(@RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
        return voBrandService.getFiltered(filter, max);
    }

    public @ResponseBody
    VoBrand getBrandById(@PathVariable("id") final long id) throws Exception {
        return voBrandService.getById(id);
    }

    public @ResponseBody
    VoBrand createBrand(@RequestBody final VoBrand vo) throws Exception {
        return voBrandService.create(vo);
    }

    public @ResponseBody
    VoBrand updateBrand(@RequestBody final VoBrand vo) throws Exception {
        return voBrandService.update(vo);
    }

    public @ResponseBody
    void removeBrand(@PathVariable("id") final long id) throws Exception {
       voBrandService.remove(id);
    }

    public @ResponseBody
    List<VoAttrValueBrand> getBrandAttributes(@PathVariable("brandId") final long brandId) throws Exception {
        return voBrandService.getBrandAttributes(brandId);
    }

    public @ResponseBody
    List<VoAttrValueBrand> update(@RequestBody final List<MutablePair<VoAttrValueBrand, Boolean>> vo) throws Exception {
        return voBrandService.update(vo);
    }
}
