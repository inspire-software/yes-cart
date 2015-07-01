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
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.page.component.util.PairChoiceRenderer;

/**
 * User: denispavlov
 * Date: 14/04/2015
 * Time: 16:48
 */
public class MultipleChoicesEditor extends Fragment {

    private final static String EDIT = "edit";

    /**
     * Construct multiple choice editor
     *
     * @param id         editor id.
     * @param markupProvider markup object.
     * @param model      model.
     * @param labelModel label model
     * @param attrValue  {@link org.yes.cart.domain.entity.AttrValue}
     * @param choices    list of strings {@link org.yes.cart.domain.misc.Pair}, that represent options to select one
     * @param readOnly  if true this component is read only
     */
    public MultipleChoicesEditor(final String id,
                                 final MarkupContainer markupProvider,
                                 final IModel model,
                                 final IModel<String> labelModel,
                                 final IModel choices,
                                 final AttrValue attrValue,
                                 final boolean readOnly) {
        super(id, "multipleChoicesEditor", markupProvider);
        final CheckBoxMultipleChoice<Pair<String, String>> checkBoxMultipleChoice =
                new CheckBoxMultipleChoice<Pair<String, String>>(EDIT, model, choices);
        checkBoxMultipleChoice.setLabel(labelModel);
        checkBoxMultipleChoice.setRequired(attrValue.getAttribute().isMandatory());
        checkBoxMultipleChoice.setChoiceRenderer(new PairChoiceRenderer());
        add(checkBoxMultipleChoice);
    }

}
