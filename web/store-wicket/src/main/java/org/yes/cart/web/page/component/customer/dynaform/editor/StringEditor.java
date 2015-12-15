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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;
import org.yes.cart.domain.entity.AttrValue;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 14/04/2015
 * Time: 16:23
 */
public class StringEditor extends Fragment {

    private final static String EDIT = "edit";

    /**
     * Construct simple text editor.
     *
     * @param id         editor id.
     * @param markupProvider markup object.
     * @param model      model.
     * @param labelModel label model
     * @param errorLabelModel error label model
     * @param attrValue  {@link org.yes.cart.domain.entity.AttrValue}
     * @param readOnly  if true this component is read only
     */
    public StringEditor(final String id,
                        final MarkupContainer markupProvider,
                        final IModel<String> model,
                        final IModel<String> labelModel,
                        final IModel<String> errorLabelModel,
                        final AttrValue attrValue,
                        final boolean readOnly) {

        super(id, "stringEditor", markupProvider);

        final TextField textField = new TextField(EDIT, model);
        textField.setLabel(labelModel);
        textField.setRequired(attrValue.getAttribute().isMandatory());
        textField.setEnabled(!readOnly);
        if (StringUtils.isNotBlank(attrValue.getAttribute().getRegexp())) {
            final PatternValidator patternValidator = new PatternValidator(attrValue.getAttribute().getRegexp()) {

                /** {@inheritDoc}*/
                public boolean validateOnNullValue() {
                    return attrValue.getAttribute().isMandatory();
                }

                /** {@inheritDoc}*/
                public void error(final IValidatable<String> validatable, final String resourceKey,
                                  final Map<String, Object> vars) {

                    if (validatable == null) {
                        throw new IllegalArgumentException("Argument [[validatable]] cannot be null");
                    }
                    if (vars == null) {
                        throw new IllegalArgumentException("Argument [[vars]] cannot be null");
                    }

                    ValidationError error = new ValidationError();
                    error.setMessage(errorLabelModel.getObject());
                    error.setVariables(vars);
                    validatable.error(error);
                }
            };

            textField.add(patternValidator);
        }
        textField.add(new AttributeModifier("placeholder", labelModel));
        add(textField);
    }

}
