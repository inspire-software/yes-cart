package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
        //todo filter
        return categoryService.getAll();
    }


    public @ResponseBody
    VoCategory create(@RequestBody VoCategory voCategory) throws Exception {
        return categoryService.create(voCategory);
    }

}
