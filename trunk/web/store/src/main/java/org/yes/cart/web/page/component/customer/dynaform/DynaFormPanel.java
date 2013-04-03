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

package org.yes.cart.web.page.component.customer.dynaform;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.springframework.core.convert.TypeDescriptor;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.utils.impl.ExtendedConversionService;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.util.PairChoiceRenderer;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Dynamic form to work with different attribute values. Form fields and field editors
 * depends from attributes, that desribed for customers.
 * Panel can be refactored, in case if some dynamic behaviour will be need for other entities, that
 * has attributes. Just add callback to store particular entity when, attributes will be submited.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/23/11
 * Time: 8:53 PM
 */
public class DynaFormPanel extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String FORM = "form";
    private final static String SAVE_LINK = "saveLink";
    private final static String FIELDS = "fields";
    private final static String NAME = "name";
    private final static String EDITOR = "editor";
    private final static String EDIT = "edit";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    private final static String VALUE_FILED = "val";


    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;


    /**
     * Construct dynamic form.
     *
     * @param id component id.
     */
    public DynaFormPanel(final String id) {
        super(id);
    }

    /**
     * Construct dynamic form.
     *
     * @param id            component id.
     * @param customerModel customer model
     */
    public DynaFormPanel(final String id, final IModel<Customer> customerModel) {

        super(id);

        final Customer customer = customerModel.getObject();

        final List<? extends AttrValue> attrValueCollection = customerService.getRankedAttributeValues(customer);

        final Form form = new Form(FORM) {

            @Override
            protected void onSubmit() {
                final Logger log = ShopCodeContext.getLog(this);
                if (log.isDebugEnabled()) {
                    log.debug(
                            MessageFormat.format(
                                    "Attributes will be updated for customer [{0}]",
                                    customer.getEmail()
                            )
                    );

                    for (AttrValue attrValue : attrValueCollection) {
                        log.debug(
                                MessageFormat.format(
                                        "Attribute with code [{0}] has value [{1}]",
                                        attrValue.getAttribute().getCode(),
                                        attrValue.getVal()
                                )
                        );
                    }
                }

                customerService.update(customer);
            }
        };

        add(form);

        RepeatingView fields = new RepeatingView(FIELDS);

        form.add(fields);

        final String lang = getLocale().getLanguage();

        for (AttrValue attrValue : attrValueCollection) {

            WebMarkupContainer row = new WebMarkupContainer(fields.newChildId());

            row.add(getLabel(attrValue, lang));

            row.add(getEditor(attrValue));

            fields.add(row);

        }

        form.add( new SubmitLink(SAVE_LINK) );


    }

    private Label getLabel(final AttrValue attrValue, final String lang) {

        final Label rez = new Label(NAME,
                getI18NSupport().getFailoverModel(
                        attrValue.getAttribute().getDisplayName(),
                        attrValue.getAttribute().getName()).getValue(lang));

        if (StringUtils.isNotBlank(attrValue.getAttribute().getDescription())) {

            rez.add(new AttributeModifier(HTML_TITLE, attrValue.getAttribute().getDescription()));

        }

        return rez;
    }


    /**
     * Get the particular editor for given attribute value. Type of editor depends from type of attribute value.
     *
     * @param attrValue give {@link org.yes.cart.domain.entity.AttrValue}
     * @return editor;
     */
    protected Component getEditor(final AttrValue attrValue) {
        final String code = attrValue.getAttribute().getCode();
        final IModel<String> labelModel = new Model<String>(code);
        final String bType = attrValue.getAttribute().getEtype().getBusinesstype();
        final ExtendedConversionService conversionService = new ExtendedConversionService();


        if ("CommaSeparatedList".equals(bType)) {
            final IModel<List<Pair<String, String>>> enumChoices = new AbstractReadOnlyModel<List<Pair<String, String>>>() {
                public List<Pair<String, String>> getObject() {

                    return (List<Pair<String, String>>)  conversionService.convert(
                        attrValue.getAttribute().getChoiceData(),
                        TypeDescriptor.valueOf(String.class),
                        TypeDescriptor.valueOf(List.class)
                    );

                }
            };
            if (attrValue.getAttribute().isAllowduplicate()) {
                final IModel model = new MultiplePairModel(new PropertyModel(attrValue, VALUE_FILED), enumChoices.getObject());
                return new MultipleChoicesEditor(EDITOR, model, labelModel, enumChoices, attrValue);
            } else {
                final IModel model = new PairModel(new PropertyModel(attrValue, VALUE_FILED), enumChoices.getObject());
                return new SingleChoiceEditor(EDITOR, model, labelModel, enumChoices, attrValue);
            }
        } else {
            final IModel model = new PropertyModel(attrValue, VALUE_FILED);
            return new StringEditor(EDITOR, model, labelModel, attrValue);
        }
    }


    /**
     * Simple text editor.
     */
    private class StringEditor extends Fragment {

        /**
         * Construct simple text editor.
         *
         * @param id         editor id.
         * @param model      model.
         * @param labelModel label model
         * @param attrValue  {@link AttrValue}
         */
        public StringEditor(final String id,
                            final IModel<String> model,
                            final IModel labelModel,
                            final AttrValue attrValue) {

            super(id, "stringEditor", DynaFormPanel.this);

            final TextField textField = new TextField(EDIT, model);
            textField.setLabel(labelModel);
            textField.setRequired(attrValue.getAttribute().isMandatory());
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
                        error.setMessage(attrValue.getAttribute().getValidationFailedMessage());
                        error.setVariables(vars);
                        validatable.error(error);
                    }
                };

                textField.add(patternValidator);
            }
            add(textField);
        }

    }


    /**
     * Drop down box.
     */
    private class SingleChoiceEditor extends Fragment {

        /**
         * Constrcuct drow down box to select single value.
         *
         * @param id         editor id.
         * @param model      model.
         * @param labelModel label model
         * @param attrValue  {@link AttrValue}
         * @param choices    list of strings {@link Pair}, that represent options to select one
         */
        public SingleChoiceEditor(final String id,
                                  final IModel model,
                                  final IModel<String> labelModel,
                                  final IModel choices,
                                  final AttrValue attrValue) {

            super(id, "singleChoiceEditor", DynaFormPanel.this);

            final DropDownChoice<Pair<String, String>> dropDownChoice =
                    new DropDownChoice<Pair<String, String>>(EDIT, model, choices);
            dropDownChoice.setLabel(labelModel);
            dropDownChoice.setRequired(attrValue.getAttribute().isMandatory());
            dropDownChoice.setChoiceRenderer(new PairChoiceRenderer());
            add(dropDownChoice);

        }

    }

    /**
     * Multiple choises as set of checkboxes.
     */
    private class MultipleChoicesEditor extends Fragment {

        /**
         * Construct multiple choises editor
         *
         * @param id         editor id.
         * @param model      model.
         * @param labelModel label model
         * @param attrValue  {@link AttrValue}
         * @param choices    list of strings {@link Pair}, that represent options to select one
         */
        public MultipleChoicesEditor(final String id,
                                     final IModel model,
                                     final IModel<String> labelModel,
                                     final IModel choices,
                                     final AttrValue attrValue) {
            super(id, "multipleChoicesEditor", DynaFormPanel.this);
            final CheckBoxMultipleChoice<Pair<String, String>> checkBoxMultipleChoice =
                    new CheckBoxMultipleChoice<Pair<String, String>>(EDIT, model, choices);
            checkBoxMultipleChoice.setLabel(labelModel);
            checkBoxMultipleChoice.setRequired(attrValue.getAttribute().isMandatory());
            checkBoxMultipleChoice.setChoiceRenderer(new PairChoiceRenderer());
            add(checkBoxMultipleChoice);
        }

    }

}
