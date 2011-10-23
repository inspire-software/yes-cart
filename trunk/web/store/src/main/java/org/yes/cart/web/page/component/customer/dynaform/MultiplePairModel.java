package org.yes.cart.web.page.component.customer.dynaform;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.yes.cart.domain.misc.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
*
 * Class to work with multiple selection from list of pairs. The
 * actual selection will be stored or retreived as comma separated list of
 * keys from selected pairs.
 *
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/23/11
 * Time: 8:58 PM
 */
public class MultiplePairModel   implements IModel<List<Pair<String,String>>> {

     /**
     * this is actual result of selection.
     */
    private List<Pair<String, String>> pairList = new ArrayList<Pair<String, String>>();

    private final PropertyModel propertyModel;

    /**
     * Construct model.
     * @param propertyModel property model to perform converion.
     * @param options all available options.
     */
    public MultiplePairModel(final PropertyModel propertyModel, final List<Pair<String, String>> options) {
        this.propertyModel = propertyModel;
        final String selectedKeysAsString = (String) propertyModel.getObject();
        if (StringUtils.isNotBlank(selectedKeysAsString)) {
            final List<String> selectedKeys = new ArrayList<String>(
                    Arrays.asList(selectedKeysAsString.split(",")));
            for (Pair<String, String> option : options) {
                if (selectedKeys.contains(option.getFirst())) {
                    pairList.add(option);
                }
            }
        }
    }

    /** {@inheritDoc}*/
    public List<Pair<String, String>> getObject() {
        return pairList;
    }

    /** {@inheritDoc}*/
    public void setObject(final List<Pair<String, String>> pairs) {

        pairList = pairs;

        if (pairs == null) {

            propertyModel.setObject(null);

        } else {

            final StringBuilder stringBuilder = new StringBuilder();

            for (int idx = 0; idx < pairList.size(); idx++) {
                stringBuilder.append(pairList.get(idx).getFirst());
                if (idx + 1 < pairList.size()) {
                    stringBuilder.append(',');
                }
            }

            propertyModel.setObject(stringBuilder.toString());

        }
    }

    /** {@inheritDoc}*/
    public void detach() {
        if (pairList instanceof IDetachable) {
            ((IDetachable)pairList).detach();
        }
    }

}
