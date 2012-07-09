package org.yes.cart.web.page.component.customer.dynaform;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.yes.cart.domain.misc.Pair;

import java.util.List;

/**
 *
  *
 * Pair model work with selected pair and list of available pairs on other side and
 * simple single string value on other side and perform two way conversions:
 *
 * Simple Value , Pairs
 *
 *                R-Red
 *       G <----> G-Green   (selected)
 *                B-Blue
 *
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/23/11
 * Time: 8:56 PM
 */
public class PairModel implements IModel<Pair<String,String>> {

    private Pair<String, String> pair;

    private final PropertyModel propertyModel;

    /**
     * Construct pair model.
     * @param propertyModel property model to perform converion.
     * @param options all available options.
     */
    public PairModel(final PropertyModel propertyModel, final List<Pair<String, String>> options) {
        this.propertyModel = propertyModel;
        final String singleSelectedKey = (String) propertyModel.getObject();
        if (StringUtils.isNotBlank(singleSelectedKey)) {
            for (Pair<String, String> option : options) {
                if (singleSelectedKey.equals(option.getFirst())) {
                    pair = option;
                    break;
                }
            }
        }
    }

    /**
     * Gets the model object.
     *
     * @return The model object
     */
    public Pair<String, String> getObject() {
        return pair;
    }

    /**
     * Sets the model object.
     *
     * @param object The model object
     */
    public void setObject(final Pair<String, String> object) {
        this.pair = object;
        propertyModel.setObject(object.getFirst());
    }

    /**
     * Detaches model after use. This is generally used to null out transient references that can be
     * re-attached later.
     */
    public void detach() {
        if (pair instanceof IDetachable) {
            ((IDetachable)pair).detach();
        }
    }

}
