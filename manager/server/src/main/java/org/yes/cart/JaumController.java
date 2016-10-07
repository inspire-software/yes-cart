/*
 * Copyright 2009-2016 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Created by igor on 06.12.2015.
 */
@Controller
@RequestMapping("/pages")
public class JaumController {


  @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
  public String homePage(ModelMap model) {
    return "index";
  }

  @RequestMapping(value = {"/shop"}, method = RequestMethod.GET)
  public String shopPage(ModelMap model) {
    return "shop";
  }


    @RequestMapping(value = {"/store"}, method = RequestMethod.GET)
    public String shopStore(ModelMap model) {
        return "store";
    }

  @RequestMapping(value = {"/warehouse"}, method = RequestMethod.GET)
  public String warehousePage(ModelMap model) {
    return "warehouse";
  }

  @RequestMapping(value = {"/login", "/login/"}, method = RequestMethod.GET)
  public String loginPage() {
    return "login";
  }


   /*

    @RequestMapping(value="/loginError", method = RequestMethod.GET)
    public String loginerror(ModelMap model) {
        model.addAttribute("error", "true");
        return "login";

    }*/


}
