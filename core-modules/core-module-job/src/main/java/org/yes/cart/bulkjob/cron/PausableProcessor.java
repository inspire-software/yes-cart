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

package org.yes.cart.bulkjob.cron;

/**
 * User: denispavlov
 * Date: 29/01/2017
 * Time: 19:00
 */
public interface PausableProcessor extends Runnable {

    /**
     * @return flag to denote that processor is on pause
     */
    boolean isPaused();

    /**
     * Set pause flag to skip the processing on next execution. Has no effect if the processing already started.
     *
     * @param paused pause flag
     */
    void setPaused(final boolean paused);

}
