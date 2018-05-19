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

package org.yes.cart.web.page.component.customer.dynaform.editor;

import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;

/**
 * User: denispavlov
 * Date: 17/05/2018
 * Time: 08:02
 */
public class CustomPatternValidator extends CachedPatternValidator {

    private final IModel<String> errorLabelModel;

    public CustomPatternValidator(final String pattern, final IModel<String> errorLabelModel) {
        super(pattern);
        this.errorLabelModel = errorLabelModel;
    }

    @Override
    protected IValidationError decorate(final IValidationError error, final IValidatable<String> validatable) {
        final String value = validatable.getValue();
        return (IValidationError) messageSource -> errorLabelModel.getObject().replace("${input}", value);
    }


}
