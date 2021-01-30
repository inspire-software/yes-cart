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

import org.yes.cart.config.ActiveConfiguration;
import org.yes.cart.config.ActiveConfigurationDetector;
import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.config.impl.ActiveConfigurationImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 29/03/2018
 * Time: 19:29
 */
@Deprecated
public class ConfiguredPausableProcessorWrapperImpl extends PausableProcessorWrapperImpl
        implements Configuration, ActiveConfigurationDetector {

    private ConfigurationContext cfgContext;


    @Override
    public List<ActiveConfiguration> getActive() {
        final List<ActiveConfiguration> active = new ArrayList<>();

        if (!isPaused()) {

            active.add(
                    new ActiveConfigurationImpl(
                            this.cfgContext.getName(),
                            this.cfgContext.getCfgInterface(),
                            "cronjob"
                    )
            );

        }

        return active;

    }

    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }
}
