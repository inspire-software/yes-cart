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

package org.yes.cart.service.async.model;

import org.yes.cart.service.async.JobStatusListener;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 12-08-05
 * Time: 2:42 PM
 */
public interface JobContext {

    /**
     * @return context parameters
     */
    Map<String, Object> getParameters();

    /**
     * @return listener for this job
     */
    JobStatusListener getListener();

    /**
     * @return true if this task need to be asynchronous
     */
    boolean isAsync();

}
