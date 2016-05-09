package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoCategory;
import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.service.endpoint.CategoryEndpointController;
import org.yes.cart.service.vo.VoCategoryService;
import org.yes.cart.service.vo.VoShopService;

import java.util.List;

/**
 * Created by Igor_Azarny on 4/13/2016.
 */
@Controller
@RequestMapping("/category")
public class CategoryEndpointControllerImpl implements CategoryEndpointController {

    private final VoCategoryService categoryService;

    @Autowired
    public CategoryEndpointControllerImpl(VoCategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    List<VoCategory> getAll() throws Exception {
        //todo filter
        return categoryService.getAll();
    }


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(method = RequestMethod.PUT,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    VoCategory create(@RequestBody VoCategory voCategory) throws Exception {
        return categoryService.create(voCategory);
    }

}
