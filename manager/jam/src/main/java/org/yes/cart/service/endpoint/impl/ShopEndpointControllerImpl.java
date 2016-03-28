package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.endpoint.ShopEndpointController;
import org.yes.cart.service.vo.VoShopService;

import java.util.List;

/**
 * Created by Igor_Azarny on 3/28/2016.
 */
@Controller
@RequestMapping("/shop")
public class ShopEndpointControllerImpl implements ShopEndpointController {


    private final VoShopService voShopService;

    @Autowired
    public ShopEndpointControllerImpl(VoShopService voShopService) {
        this.voShopService = voShopService;
    }

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/all", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    List<VoShop> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return voShopService.getAll();
    }


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    VoShop getById(@PathVariable long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return voShopService.getById(id);
    }

}
