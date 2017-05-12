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
package org.yes.cart.service.endpoint;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: denispavlov
 * Date: 11/05/2017
 * Time: 19:17
 */
@Controller
@RequestMapping("/filevault")
public interface FileVaultEndpointController {


    /**
     * Download given file as bytes.
     * @param fileName file name
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/image/{type}", method = RequestMethod.GET)
    void downloadImage(@PathVariable("type") String type, @RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException;

    /**
     * Download given file as bytes.
     * @param fileName file name
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/file/{type}", method = RequestMethod.GET)
    void downloadFile(@PathVariable("type") String type, @RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException;

    /**
     * Download given file as bytes.
     * @param fileName file name
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/sysfile/{type}", method = RequestMethod.GET)
    void downloadSysFile(@PathVariable("type") String type, @RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException;

}
