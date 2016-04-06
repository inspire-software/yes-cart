package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoShop;
import org.yes.cart.domain.vo.VoShopLocale;
import org.yes.cart.domain.vo.VoShopUrl;
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
    List<VoShop> getAll() throws Exception {
        return voShopService.getAll();
    }

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    VoShop getById(@PathVariable("id") long id) throws Exception {
        return voShopService.getById(id);
    }

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(method = RequestMethod.PUT,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    VoShop create(@RequestBody VoShop voShop) throws Exception {
        return voShopService.create(voShop);
    }

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    VoShop update(@RequestBody VoShop voShop) throws Exception {
        return voShopService.update(voShop);
    }

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(method = RequestMethod.DELETE,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    void remove(@PathVariable("id") long id) throws Exception {
        voShopService.remove(id);
    }

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/localization/{shopId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    VoShopLocale getLocalization(@PathVariable("shopId") long shopId) throws Exception {
        return voShopService.getShopLocale(shopId);
    }

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/localization", method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    VoShopLocale update(@RequestBody VoShopLocale voShopLocale) throws Exception {
        return voShopService.update(voShopLocale);
    }

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/urls/{shopId}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    VoShopUrl getUrl(@PathVariable("shopId") long shopId) throws Exception {
        return voShopService.getShopUrls(shopId);
    }


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN"})
    @RequestMapping(value = "/urls", method = RequestMethod.POST,  produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    VoShopUrl update(@RequestBody VoShopUrl voShopUrl)  throws Exception {
        return voShopService.update(voShopUrl);
    }



}
