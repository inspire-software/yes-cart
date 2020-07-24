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

package org.yes.cart.domain.i18n;

import org.yes.cart.domain.i18n.impl.Immutable18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.util.Collections;

/**
 * Date: 24/07/2020
 * Time: 17:26
 */
public final class I18NModels {

    private I18NModels() {
        // no instance
    }

    public static I18NModel AUDITEXPORT = new Immutable18NModel(new StringI18NModel(Collections.singletonMap(I18NModel.DEFAULT, "AUDITEXPORT")));

    public static I18NModel SUPPLIER = new Immutable18NModel(new StringI18NModel(Collections.singletonMap(I18NModel.DEFAULT, "SUPPLIER")));

    public I18NModel unmodifiable(I18NModel model) {
        if (model instanceof Immutable18NModel) {
            return model;
        }
        return new Immutable18NModel(model);
    }

}
