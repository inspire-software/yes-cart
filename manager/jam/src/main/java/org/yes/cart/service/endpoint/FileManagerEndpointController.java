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

import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yes.cart.domain.misc.MutablePair;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/09/2016
 * Time: 11:38
 */
@Controller
@RequestMapping("/filemanager")
public interface FileManagerEndpointController {


    /**
     * List available files
     *
     * @return pair of absolute and human friendly paths
     * @throws IOException
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/list/{mode}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<MutablePair<String, String>> list(@PathVariable("mode") String mode) throws IOException;

    /**
     * Download given file as bytes.
     * @param fileName file name
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    void download(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException;

    /**
     * Store given bytes as file.
     * @param file file body.
     * @return file name, including path, on server side.
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    String upload(@RequestParam("file") MultipartFile file) throws IOException;

    /**
     * Delete given file.
     * @param fileName file name
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMCONTENTADMIN"})
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    void delete(@RequestParam("fileName") String fileName) throws IOException;


}
