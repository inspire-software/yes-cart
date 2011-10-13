package org.yes.cart.web.page.component.util;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.yes.cart.domain.entity.State;

/**
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */
public class StateRenderer extends ChoiceRenderer<State> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDisplayValue(final State state) {
        return state.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdValue(final State state, final int i) {
        return state.getStateCode();
    }

}
