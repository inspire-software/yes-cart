package org.yes.cart.web.page.component;


import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


public class BaseComponent extends Panel {

    protected static final String HTML_CLASS = "class";
    protected static final String HTML_TITLE = "title";
    protected static final String HTML_ALT = "alt";


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