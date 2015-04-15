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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.util.HashMap;
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
    private final static String CONTENT = "dynaformContent";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerService;

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    private final EditorFactory editorFactory = new EditorFactory();


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

        super(id, customerModel);

        final Shop shop = ApplicationDirector.getCurrentShop();
        final Customer customer = (Customer) getDefaultModelObject();

        final List<Pair<AttrValueCustomer, Boolean>> attrValueCollection = customerService.getCustomerProfileAttributes(shop, customer);

        final Form form = new Form(FORM) {

            @Override
            protected void onSubmit() {
                final Logger log = ShopCodeContext.getLog(this);
                log.debug("Attributes will be updated for customer [{}]", customer.getEmail());

                final Map<String, String> values = new HashMap<String, String>();
                for (Pair<? extends AttrValue, Boolean> av : attrValueCollection) {
                    log.debug("Attribute with code [{}] has value [{}], readonly [{}]",
                            new Object[] {
                                    av.getFirst().getAttribute().getCode(),
                                    av.getFirst().getVal(),
                                    av.getSecond()
                            });
                    if (av.getSecond() != null && !av.getSecond()) {
                        values.put(av.getFirst().getAttribute().getCode(), av.getFirst().getVal());
                    }
                }

                customerService.updateCustomerAttributes(ApplicationDirector.getCurrentShop(), customer, values);
            }
        };

        addOrReplace(form);

        RepeatingView fields = new RepeatingView(FIELDS);

        form.add(fields);

        final String lang = getLocale().getLanguage();

        for (Pair<? extends AttrValue, Boolean> attrValue : attrValueCollection) {

            WebMarkupContainer row = new WebMarkupContainer(fields.newChildId());

            row.add(getLabel(attrValue.getFirst(), lang));

            row.add(getEditor(attrValue.getFirst(), attrValue.getSecond()));

            fields.add(row);

        }

        form.add( new SubmitLink(SAVE_LINK) );

        form.add(new Label(CONTENT, ""));

    }

    @Override
    protected void onBeforeRender() {

        final long shopId = ShopCodeContext.getShopId();
        final String lang = getLocale().getLanguage();

        String dynaformInfo = getContentInclude(shopId, "selfcare_dynaform_content_include", lang);
        get(FORM).get(CONTENT).replaceWith(new Label(CONTENT, dynaformInfo).setEscapeModelStrings(false));


        super.onBeforeRender();
    }

    private String getContentInclude(long shopId, String contentUri, String lang) {
        String content = contentServiceFacade.getContentBody(contentUri, shopId, lang);
        if (content == null) {
            content = "";
        }
        return content;
    }


    private Label getLabel(final AttrValue attrValue, final String lang) {

        final I18NModel model = getI18NSupport().getFailoverModel(
                attrValue.getAttribute().getDisplayName(),
                attrValue.getAttribute().getName());

        final Label rez = new Label(NAME, new AbstractReadOnlyModel<String>() {

            private final I18NModel m = model;

            @Override
            public String getObject() {
                final String lang = getLocale().getLanguage();
                return m.getValue(lang);
            }
        });

        return rez;
    }


    /**
     * Get the particular editor for given attribute value. Type of editor depends from type of attribute value.
     *
     * @param attrValue give {@link org.yes.cart.domain.entity.AttrValue}
     * @param readOnly  if true this component is read only
     *
     * @return editor
     */
    protected Component getEditor(final AttrValue attrValue, final Boolean readOnly) {

        return editorFactory.getEditor(EDITOR, this, getLocale().getLanguage(), attrValue, readOnly);
    }


}
