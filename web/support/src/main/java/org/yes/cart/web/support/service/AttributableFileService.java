/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.web.support.service;


import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.domain.misc.Pair;

import java.util.List;

/**
 * User: Denis Pavlov
 */
public interface AttributableFileService {


    /**
     * Get pair of file attributes name and file name for given language.
     *
     * @param attributable           given attributable
     * @param lang language
     *
     * @return files attributes names.
     */
    List<Pair<String, String>> getFileAttributeFileNames(Attributable attributable,
                                                         String lang);


    /**
     * Get the context file with file servlet url
     * and specified parameters.
     *
     * @param attributable           given attributable
     * @param httpServletContextPath http servlet request path
     * @param locale                 locale for file
     * @param attrName given file attribute name
     * @param attrVal optional attrValue, if it not provided service will try to get value from attributes
     *
     * @return default context file url.
     */
    String getFile(Attributable attributable,
                   String httpServletContextPath,
                   String locale,
                   String attrName,
                   String attrVal);

    /**
     * Get default file uri.
     *
     *
     * @param object             product/sku/category
     * @param servletContextPath http servlet request
     * @param locale             locale for file
     * @param fileName          name of file
     *
     * @return file uri.
     */
    String getFileURI(Object object,
                      String servletContextPath,
                      String locale,
                      String fileName);

}
