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

package org.yes.cart.web.page.component;


import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


public class BaseComponent extends Panel {

    public static final String FEEDBACK = "feedback";

    public static final String HTML_CLASS = "class";
    public static final String HTML_TITLE = "title";
    public static final String HTML_ALT = "alt";
    public static final String HTML_WIDTH = "width";
    public static final String HTML_HEIGHT = "height";


    private boolean panelVisible = true;


    /**
     * Construct panel.
     *
     * @param id panel id
     */
    public BaseComponent(final String id) {
        super(id);
    }

    /**
     * Construct panel.
     *
     * @param id    panel id
     * @param model model.
     */
    public BaseComponent(final String id, final IModel<?> model) {
        super(id, model);
    }


}