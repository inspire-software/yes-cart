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

package org.yes.cart.bulkjob.cron;

import org.slf4j.Logger;
import org.yes.cart.service.domain.RuntimeAttributeService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.ShopCodeContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Processor that depends on the time it was last executed.
 *
 * Last time this job runs is stored in system preferences: JOB_ORD_PROD_IDX_LAST_RUN
 * So that next run we only scan orders that have changed since last job run.
 *
 * User: denispavlov
 * Date: 27/04/2015
 * Time: 15:42
 */
public abstract class AbstractLastRunDependentProcessorImpl implements Runnable {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final SystemService systemService;
    private final RuntimeAttributeService runtimeAttributeService;

    private boolean lastRunInitialised = false;
    private Date lastRun;


    public AbstractLastRunDependentProcessorImpl(final SystemService systemService,
                                                 final RuntimeAttributeService runtimeAttributeService) {
        this.systemService = systemService;
        this.runtimeAttributeService = runtimeAttributeService;
    }

    /** {@inheritDoc} */
    @Override
    public final void run() {

        final Logger log = ShopCodeContext.getLog(this);

        final Date now = new Date();

        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        final String lastRunPreferenceAttributeName = getLastRunPreferenceAttributeName();

        if (!lastRunInitialised) {
            if (!systemService.getAttributeValues().keySet().contains(lastRunPreferenceAttributeName)) {
                runtimeAttributeService.create(lastRunPreferenceAttributeName, "SYSTEM", "Date");
            } else {
                final String pref = systemService.getAttributeValue(lastRunPreferenceAttributeName);
                try {
                    lastRun = dateFormat.parse(pref);
                } catch (ParseException e) {
                    log.error("Unable to parse last job run date {} using format {}", pref, DATE_FORMAT);
                }
            }
            lastRunInitialised = true;
        }

        doRun(lastRun);

        lastRun = now;
        systemService.updateAttributeValue(lastRunPreferenceAttributeName, dateFormat.format(now));

    }

    /**
     * Attribute used to store last run date.
     *
     * @return name of the attribute
     */
    protected abstract String getLastRunPreferenceAttributeName();

    /**
     * Extension hook for implementor jobs.
     *
     * @param lastRun last run date
     */
    protected abstract void doRun(final Date lastRun);


}
