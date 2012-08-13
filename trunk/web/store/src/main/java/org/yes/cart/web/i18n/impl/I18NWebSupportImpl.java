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

package org.yes.cart.web.i18n.impl;

import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.web.i18n.I18NWebSupport;

/**
 * User: denispavlov
 * Date: 12-08-13
 * Time: 10:00 PM
 */
public class I18NWebSupportImpl implements I18NWebSupport {

    /**
     * {@inheritDoc}
     */
    public I18NModel getDefaultModel(final Object i18nObject) {
        return new StringI18NModel(String.valueOf(i18nObject));
    }

    /**
     * {@inheritDoc}
     */
    public I18NModel getFailoverModel(final Object i18nObject, final String failover) {
        return new FailoverStringI18NModel(String.valueOf(i18nObject), failover);
    }
}
