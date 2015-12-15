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

package org.yes.cart.web.page.component.customer.dynaform;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.springframework.core.convert.TypeDescriptor;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.utils.impl.ExtendedConversionService;
import org.yes.cart.web.page.component.customer.dynaform.editor.BooleanEditor;
import org.yes.cart.web.page.component.customer.dynaform.editor.MultipleChoicesEditor;
import org.yes.cart.web.page.component.customer.dynaform.editor.SingleChoiceEditor;
import org.yes.cart.web.page.component.customer.dynaform.editor.StringEditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EditorFactory implements Serializable {

    private static final ExtendedConversionService CONVERSION_SERVICE = new ExtendedConversionService();

    private final static String VALUE_FILED = "val";

    public EditorFactory() {
    }

    /**
     * Get the particular editor for given attribute value. Type of editor depends from type of attribute value.
     *
     * @param id editor id
     * @param markupContainer markup component
     * @param attrValue give {@link org.yes.cart.domain.entity.AttrValue}
     * @param readOnly  if true this component is read only
     *
     * @return editor
     */
    public Component getEditor(final String id,
                               final MarkupContainer markupContainer,
                               final String language,
                               final AttrValue attrValue,
                               final Boolean readOnly) {

        final boolean notEditable = readOnly == null || readOnly;

        final I18NModel nameModel = new FailoverStringI18NModel(
                attrValue.getAttribute().getDisplayName(),
                attrValue.getAttribute().getName());

        final String prop = attrValue.getAttribute().getVal();

        final IModel<String> labelModel = new AbstractReadOnlyModel<String>() {

            private final I18NModel m = nameModel;

            @Override
            public String getObject() {
                final String lang = markupContainer.getLocale().getLanguage();
                final String name = m.getValue(lang);
                if (StringUtils.isNotBlank(name)) {
                    return name;
                }
                if (StringUtils.isNotBlank(prop)) {
                    return markupContainer.getLocalizer().getString(prop, markupContainer);
                }
                return null;
            }
        };
        final String bType = attrValue.getAttribute().getEtype().getBusinesstype();

        if ("CommaSeparatedList".equals(bType)) {

            final I18NModel choices = new FailoverStringI18NModel(
                    attrValue.getAttribute().getChoiceData(),
                    attrValue.getAttribute().getChoiceData());

            final IModel<List<Pair<String, String>>> enumChoices = new AbstractReadOnlyModel<List<Pair<String, String>>>() {

                private final I18NModel m = choices;

                public List<Pair<String, String>> getObject() {
                    final String lang = markupContainer.getLocale().getLanguage();
                    final List<Pair<String, String>> list = (List<Pair<String, String>>) CONVERSION_SERVICE.convert(
                            choices.getValue(lang),
                            TypeDescriptor.valueOf(String.class),
                            TypeDescriptor.valueOf(List.class)
                    );
                    if (list == null) {
                        return new ArrayList<Pair<String, String>>();
                    }
                    return list;
                }
            };
            if (attrValue.getAttribute().isAllowduplicate()) {
                final IModel model = new MultiplePairModel(new PropertyModel(attrValue, VALUE_FILED), enumChoices.getObject());
                return new MultipleChoicesEditor(id, markupContainer, model, labelModel, enumChoices, attrValue, notEditable);
            } else {
                final IModel model = new PairModel(new PropertyModel(attrValue, VALUE_FILED), enumChoices.getObject());
                return new SingleChoiceEditor(id, markupContainer, model, labelModel, enumChoices, attrValue, notEditable);
            }
        } else if ("Boolean".equals(bType)) {
            final IModel model = new PropertyModel(attrValue, VALUE_FILED);
            return new BooleanEditor(id, markupContainer, model, labelModel, attrValue, notEditable);
        } else {

            final String regexError = new FailoverStringI18NModel(
                    attrValue.getAttribute().getValidationFailedMessage(),
                    attrValue.getAttribute().getCode()).getValue(language);

            final IModel model = new PropertyModel(attrValue, VALUE_FILED);
            return new StringEditor(id, markupContainer, model, labelModel, new Model<String>(regexError), attrValue, notEditable);
        }
    }
}