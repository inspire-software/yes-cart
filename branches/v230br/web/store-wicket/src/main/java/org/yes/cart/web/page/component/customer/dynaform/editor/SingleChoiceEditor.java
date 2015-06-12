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
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.page.component.util.PairChoiceRenderer;

/**
 * User: denispavlov
 * Date: 14/04/2015
 * Time: 16:44
 */
public class SingleChoiceEditor extends Fragment {

    private final static String EDIT = "edit";

    /**
     * Construct drop down box to select single value.
     *
     * @param id         editor id.
     * @param markupProvider markup object.
     * @param model      model.
     * @param labelModel label model
     * @param attrValue  {@link org.yes.cart.domain.entity.AttrValue}
     * @param choices    list of strings {@link org.yes.cart.domain.misc.Pair}, that represent options to select one
     * @param readOnly  if true this component is read only
     */
    public SingleChoiceEditor(final String id,
                              final MarkupContainer markupProvider,
                              final IModel model,
                              final IModel<String> labelModel,
                              final IModel choices,
                              final AttrValue attrValue,
                              final boolean readOnly) {

        super(id, "singleChoiceEditor", markupProvider);

        final DropDownChoice<Pair<String, String>> dropDownChoice =
                new DropDownChoice<Pair<String, String>>(EDIT, model, choices);
        dropDownChoice.setLabel(labelModel);
        dropDownChoice.setEnabled(!readOnly);
        dropDownChoice.setRequired(attrValue.getAttribute().isMandatory());
        dropDownChoice.setChoiceRenderer(new PairChoiceRenderer());
        add(dropDownChoice);

    }

}
