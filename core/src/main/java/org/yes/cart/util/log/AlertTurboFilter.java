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
package org.yes.cart.util.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.service.ws.AlertDirector;

/**
 * User: denispavlov
 * Date: 07/03/2017
 * Time: 17:57
 */
public class AlertTurboFilter extends TurboFilter implements ApplicationContextAware {

    private static AlertDirector ALERTS = null;

    @Override
    public FilterReply decide(final Marker marker, final Logger logger, final Level level, final String format, final Object[] params, final Throwable t) {
        if(!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        if(marker != null && marker.contains("alert") && ALERTS != null) {
            if (params == null || params.length == 0) {
                ALERTS.publish(
                        new Pair<String, String>(
                                format,
                                ShopCodeContext.getShopCode()
                        )
                );
            } else {
                ALERTS.publish(
                        new Pair<String, String>(
                                MessageFormatter.arrayFormat(format, params).getMessage(),
                                ShopCodeContext.getShopCode()
                        )
                );
            }
        }

        return FilterReply.NEUTRAL;

    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        ALERTS = applicationContext.getBean(AlertDirector.class);
    }



}
