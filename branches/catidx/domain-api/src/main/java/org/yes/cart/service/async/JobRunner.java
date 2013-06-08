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

package org.yes.cart.service.async;

import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobStatus;

/**
 * User: denispavlov
 * Date: 12-08-05
 * Time: 2:48 PM
 */
public interface JobRunner {

    /**
     * Check current job status by given token
     *
     * @param token job token
     * @return status
     */
    JobStatus getStatus(String token);

    /**
     * Run a job with given context
     * @param ctx context
     * @return token for this job ({@see #getStatus})
     */
    String doJob(JobContext ctx);
}
