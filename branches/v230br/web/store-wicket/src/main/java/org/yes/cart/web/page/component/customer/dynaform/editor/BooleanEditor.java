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

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.yes.cart.domain.entity.AttrValue;

/**
 * User: denispavlov
 * Date: 14/04/2015
 * Time: 16:16
 */
public class BooleanEditor extends Fragment {

    private final static String EDIT = "edit";

    private IModel<String> inner;

    /**
     * Construct simple check box editor.
     *
     * @param id         editor id.
     * @param markupProvider markup object.
     * @param model      model.
     * @param labelModel label model
     * @param attrValue  {@link org.yes.cart.domain.entity.AttrValue}
     * @param readOnly  if true this component is read only
     */
    public BooleanEditor(final String id,
                         final MarkupContainer markupProvider,
                         final IModel<String> model,
                         final IModel<String> labelModel,
                         final AttrValue attrValue,
                         final boolean readOnly) {

        super(id, "booleanEditor", markupProvider);

        inner = model;

        final CheckBox checkboxField = new CheckBox(EDIT, new PropertyModel<Boolean>(this, "innerValue"));
        checkboxField.setLabel(labelModel);
        checkboxField.setRequired(attrValue.getAttribute().isMandatory());
        checkboxField.setEnabled(!readOnly);
        add(checkboxField);
    }

    public Boolean getInnerValue() {
        return Boolean.valueOf(inner.getObject());
    }

    public void setInnerValue(final Boolean inner) {
        this.inner.setObject(Boolean.valueOf(inner != null && inner).toString());
    }
}

