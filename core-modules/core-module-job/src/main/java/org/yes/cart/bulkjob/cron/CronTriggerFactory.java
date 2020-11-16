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

import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.yes.cart.utils.RuntimeConstants;

import java.text.ParseException;

/**
 * User: inspiresoftware
 * Date: 15/11/2020
 * Time: 21:02
 */
public class CronTriggerFactory extends CronTriggerFactoryBean {

    private String cronExpressionKey;

    private RuntimeConstants config;

    /**
     * Set key for cronExpression configuration.
     *
     * @param cronExpressionKey key
     */
    public void setCronExpressionKey(final String cronExpressionKey) {
        this.cronExpressionKey = cronExpressionKey;
    }

    /**
     * Configurations.
     *
     * @param config configs
     */
    public void setConfig(final RuntimeConstants config) {
        this.config = config;
    }

    @Override
    public void afterPropertiesSet() throws ParseException {

        if (this.cronExpressionKey != null) {
            final String cronExpression = this.config.getConstant(this.cronExpressionKey);
            setCronExpression(cronExpression);
        }

        super.afterPropertiesSet();
        
    }
}
