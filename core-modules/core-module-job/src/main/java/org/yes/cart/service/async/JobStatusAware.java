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

package org.yes.cart.service.async;

import org.yes.cart.service.async.model.JobStatus;

/**
 * Interface to mark jobs that are able to product status reports.
 * 
 * User: denispavlov
 * Date: 08/04/2018
 * Time: 12:27
 */
public interface JobStatusAware {

    /**
     * Check current job status by given token.
     *
     * @param token job token
     *
     * @return status
     */
    JobStatus getStatus(String token);

}
