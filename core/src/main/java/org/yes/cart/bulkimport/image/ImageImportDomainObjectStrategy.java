/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.bulkimport.image;

import org.yes.cart.service.async.JobStatusListener;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 22:16
 */
public interface ImageImportDomainObjectStrategy {

    /**
     * Check if given strategy supports import of specified image type.
     *
     * @param uriPattern pattern, same as image name strategy
     *
     * @return true if this uri is supported
     */
    boolean supports(String uriPattern);

    /**
     * Do necessary actions to associate given image file with domain object.
     *
     * @param statusListener job status listener
     * @param fileName file name (no path) which will be saved to domain object image custom attribute
     * @param code     object code that will be used to look up the domain object
     * @param suffix   suffix identified the image file order (e.g. products can have more than one image)
     *
     * @return true if import was successful
     */
    boolean doImageImport(JobStatusListener statusListener,
                          String fileName,
                          String code,
                          String suffix);

}
