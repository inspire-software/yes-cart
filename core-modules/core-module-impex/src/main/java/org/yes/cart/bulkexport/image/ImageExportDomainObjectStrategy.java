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

package org.yes.cart.bulkexport.image;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusListener;

import java.util.List;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 22:16
 */
public interface ImageExportDomainObjectStrategy {

    /**
     * Check if given strategy supports import of specified image type.
     *
     * @param uriPattern pattern, same as image name strategy
     *
     * @return true if this uri is supported
     */
    boolean supports(String uriPattern);


    /**
     * Do necessary actions to export given image file with domain object.
     *
     * @param statusListener job status listener
     * @param objectType type
     * @param fileName file name (no path) which will be saved to domain object image custom attribute
     *
     * @return true if export was successful
     */
    boolean doImageExport(JobStatusListener statusListener,
                          String fileName);

}
