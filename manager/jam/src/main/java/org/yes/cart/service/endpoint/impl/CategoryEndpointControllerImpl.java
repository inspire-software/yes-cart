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
import org.yes.cart.domain.vo.VoCategory;
import org.yes.cart.service.endpoint.CategoryEndpointController;
import org.yes.cart.service.vo.VoCategoryService;

import java.util.List;

/**
 * Created by Igor_Azarny on 4/13/2016.
 */
@Component
public class CategoryEndpointControllerImpl implements CategoryEndpointController {

    private final VoCategoryService categoryService;

    @Autowired
    public CategoryEndpointControllerImpl(VoCategoryService categoryService) {
        this.categoryService = categoryService;
    }


    public @ResponseBody
    List<VoCategory> getAll() throws Exception {
        return categoryService.getAll();
    }

    public @ResponseBody
    List<VoCategory> getFiltered(@RequestBody final String filter,
                                 @PathVariable("max") final int max) throws Exception {
        return categoryService.getFiltered(filter, max);
    }

    public @ResponseBody
    VoCategory create(@RequestBody VoCategory voCategory) throws Exception {
        return categoryService.create(voCategory);
    }

}
