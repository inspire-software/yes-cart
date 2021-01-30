/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.bulkjob.cron;

import java.util.Map;

/**
 * User: inspiresoftware
 * Date: 19/01/2021
 * Time: 16:28
 */
public interface PausableCronJobProcessor extends CronJobProcessor {

    /**
     * @param context context
     *
     * @return flag to denote that processor is on pause
     */
    boolean isPaused(Map<String, Object> context);

    /**
     * Set pause flag to skip the processing on next execution. Has no effect if the processing already started.
     *
     * @param context context
     * @param paused pause flag
     */
    void setPaused(Map<String, Object> context, boolean paused);

}
