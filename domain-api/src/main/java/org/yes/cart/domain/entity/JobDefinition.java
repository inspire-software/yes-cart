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

package org.yes.cart.domain.entity;

/**
 * Job definition that can be used to schedule jobs in apps.
 *
 * User: inspiresoftware
 * Date: 18/01/2021
 * Time: 17:17
 */
public interface JobDefinition extends Auditable, Codable {

    /**
     * @return primary key
     */
    long getJobDefinitionId();

    /**
     * @param jobDefinitionId primary key
     */
    void setJobDefinitionId(long jobDefinitionId);

    /**
     * @return human readable name
     */
    String getJobName();

    /**
     * @param jobName human readable name
     */
    void setJobName(String jobName);

    /**
     * @return spring bean processor name
     */
    String getProcessor();

    /**
     * @param processor spring bean processor name
     */
    void setProcessor(String processor);

    /**
     * @return additional configuration for the processor
     */
    String getContext();

    /**
     * @param context additional configuration for the processor
     */
    void setContext(String context);

    /**
     * @return host application that should run this schedule
     */
    String getHostApplicationRegEx();

    /**
     * @param hostApplicationRegEx host application that should run this schedule
     */
    void setHostApplicationRegEx(String hostApplicationRegEx);

    /**
     * @return default cron schedule
     */
    String getDefaultCronExpression();

    /**
     * @param defaultCronExpression default schedule
     */
    void setDefaultCronExpression(String defaultCronExpression);

    /**
     * @return default cron schedule as runtime variable (used if {{@link #getDefaultCronExpression()}} is not defined)
     */
    String getDefaultCronExpressionKey();

    /**
     * @param defaultCronExpressionKey default schedule as runtime variable
     */
    void setDefaultCronExpressionKey(String defaultCronExpressionKey);

    /**
     * If job is created automatically during start up defined the paused state
     *
     * @return true if auto created jobs should be in paused state
     */
    Boolean getDefaultPaused();

    /**
     * @param defaultPaused set default paused state for auto created jobs
     */
    void setDefaultPaused(Boolean defaultPaused);

}
