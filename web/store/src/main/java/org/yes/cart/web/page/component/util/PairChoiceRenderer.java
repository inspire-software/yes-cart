package org.yes.cart.web.page.component.util;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.yes.cart.domain.misc.Pair;

/**
 * Render an select option from {@link Pair} of key - value.
 * <p/>
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */

public class PairChoiceRenderer extends ChoiceRenderer {

    /** {@inheritDoc} */
    public Object getDisplayValue(final Object object) {
        return ((Pair<String, String>)object).getSecond();
    }

    /** {@inheritDoc} */
    @Override
    public String getIdValue(final Object object, final int i) {
        if (object instanceof Pair) {
            Pair<String, String> pair = (Pair<String, String>) object;
            return pair.getFirst();
        }
        return (String) object;
    }


}
