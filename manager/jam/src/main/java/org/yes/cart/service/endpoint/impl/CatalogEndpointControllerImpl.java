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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.endpoint.CatalogEndpointController;
import org.yes.cart.service.vo.VoBrandService;
import org.yes.cart.service.vo.VoCategoryService;
import org.yes.cart.service.vo.VoProductTypeService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 21/08/2016
 * Time: 16:43
 */
@Component
public class CatalogEndpointControllerImpl implements CatalogEndpointController {

    private final VoBrandService voBrandService;
    private final VoProductTypeService voProductTypeService;
    private final VoCategoryService voCategoryService;

    @Autowired
    public CatalogEndpointControllerImpl(final VoBrandService voBrandService,
                                         final VoProductTypeService voProductTypeService,
                                         final VoCategoryService voCategoryService) {
        this.voBrandService = voBrandService;
        this.voProductTypeService = voProductTypeService;
        this.voCategoryService = voCategoryService;
    }

    @Override
    public @ResponseBody
    List<VoBrand> getFilteredBrands(@RequestBody(required = false) final String filter, @PathVariable("max") final int max) throws Exception {
        return voBrandService.getFilteredBrands(filter, max);
    }

    @Override
    public @ResponseBody
    VoBrand getBrandById(@PathVariable("id") final long id) throws Exception {
        return voBrandService.getBrandById(id);
    }

    @Override
    public @ResponseBody
    VoBrand createBrand(@RequestBody final VoBrand vo) throws Exception {
        return voBrandService.createBrand(vo);
    }

    @Override
    public @ResponseBody
    VoBrand updateBrand(@RequestBody final VoBrand vo) throws Exception {
        return voBrandService.updateBrand(vo);
    }

    @Override
    public @ResponseBody
    void removeBrand(@PathVariable("id") final long id) throws Exception {
       voBrandService.removeBrand(id);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueBrand> getBrandAttributes(@PathVariable("brandId") final long brandId) throws Exception {
        return voBrandService.getBrandAttributes(brandId);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueBrand> updateBrand(@RequestBody final List<MutablePair<VoAttrValueBrand, Boolean>> vo) throws Exception {
        return voBrandService.updateBrandAttributes(vo);
    }

    @Override
    public @ResponseBody
    List<VoProductTypeInfo> getFilteredProductTypes(@RequestBody(required = false) final String filter, @PathVariable("max") final int max) throws Exception {
        return voProductTypeService.getFilteredTypes(filter, max);
    }

    @Override
    public @ResponseBody
    VoProductType getProductTypeById(@PathVariable("id") final long id) throws Exception {
        return voProductTypeService.getTypeById(id);
    }

    @Override
    public @ResponseBody
    VoProductType createProductType(@RequestBody final VoProductType vo) throws Exception {
        return voProductTypeService.createType(vo);
    }

    @Override
    public @ResponseBody
    VoProductType updateProductType(@RequestBody final VoProductType vo) throws Exception {
        return voProductTypeService.updateType(vo);
    }

    @Override
    public @ResponseBody
    void removeProductType(@PathVariable("id") final long id) throws Exception {
        voProductTypeService.removeType(id);
    }

    @Override
    public @ResponseBody
    List<VoProductTypeAttr> getProductTypeAttributes(@PathVariable("typeId") final long typeId) throws Exception {
        return voProductTypeService.getTypeAttributes(typeId);
    }

    @Override
    public @ResponseBody
    List<VoProductTypeAttr> updateProductType(@RequestBody final List<MutablePair<VoProductTypeAttr, Boolean>> vo) throws Exception {
        return voProductTypeService.updateTypeAttributes(vo);
    }


    @Override
    public @ResponseBody
    List<VoCategory> getAllCategories() throws Exception {
        return voCategoryService.getAll();
    }

    @Override
    public @ResponseBody
    List<VoCategory> getBranchCategories(@PathVariable("branch") final long branch,
                                         @RequestParam(value = "expand", required = false) final String expand) throws Exception {
        return voCategoryService.getBranch(branch, determineBranchIds(expand));
    }

    @Override
    public @ResponseBody
    List<Long> getBranchesCategoriesPaths(@RequestParam(value = "expand", required = false) final String expand) throws Exception {
        return voCategoryService.getBranchesPaths(determineBranchIds(expand));
    }

    private List<Long> determineBranchIds(final @RequestParam(value = "expand", required = false) String expand) {
        List<Long> expandIds = new ArrayList<>(50);
        if (StringUtils.isNotBlank(expand)) {
            for (final String expandItem : StringUtils.split(expand, '|')) {
                final long id = NumberUtils.toLong(expandItem);
                if (id > 0L) {
                    expandIds.add(id);
                }
            }
        }
        return expandIds;
    }

    @Override
    public @ResponseBody
    List<VoCategory> getFilteredCategories(@RequestBody(required = false) final String filter,
                                           @PathVariable("max") final int max) throws Exception {
        return voCategoryService.getFilteredCategories(filter, max);
    }

    @Override
    public @ResponseBody
    VoCategory createCategory(@RequestBody VoCategory voCategory) throws Exception {
        return voCategoryService.createCategory(voCategory);
    }

    @Override
    public @ResponseBody
    VoCategory getCategoryById(@PathVariable("id") final long id) throws Exception {
        return voCategoryService.getCategoryById(id);
    }

    @Override
    public @ResponseBody
    VoCategory updateCategory(@RequestBody final VoCategory voCategory) throws Exception {
        return voCategoryService.updateCategory(voCategory);
    }

    @Override
    public @ResponseBody
    void removeCategory(@PathVariable("id") final long id) throws Exception {
        voCategoryService.removeCategory(id);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueCategory> getCategoryAttributes(@PathVariable("categoryId") final long categoryId) throws Exception {
        return voCategoryService.getCategoryAttributes(categoryId);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueCategory> updateCategory(@RequestBody final List<MutablePair<VoAttrValueCategory, Boolean>> vo) throws Exception {
        return voCategoryService.updateCategoryAttributes(vo);
    }
}
