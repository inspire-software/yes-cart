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

package org.yes.cart.web.support.i18n;

import org.yes.cart.domain.i18n.I18NModel;

/**
 * User: denispavlov
 * Date: 12-08-13
 * Time: 9:58 PM
 */
public interface I18NWebSupport {

    /**
     * Return i18n model from an i18n object.
     *
     * @param i18nObject i18n object
     * @return model
     */
    I18NModel getDefaultModel(Object i18nObject);

    /**
     * Return i18n model from an i18n object.
     *
     * @param i18nObject i18n object
     * @param failover default value
     * @return model
     */
    I18NModel getFailoverModel(Object i18nObject, String failover);

}
