package org.yes.cart.web.page.component.util;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.PropertyModel;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.State;

import java.util.List;

/**
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */
public class StateModel implements IModel<State> {

    private State state;

    private final PropertyModel propertyModel;

    public StateModel(final PropertyModel propertyModel, List<State> stateList) {
        this.propertyModel = propertyModel;
        final String singleSelectedKey = (String) propertyModel.getObject();
        if (StringUtils.isNotBlank(singleSelectedKey)) {
            for (State c : stateList) {
                if (singleSelectedKey.equals(c.getStateCode())) {
                    state = c;
                    break;
                }
            }
        }
    }

    public State getObject() {
        return state;
    }

    public void setObject(final State state) {
        this.state = state;
        if (state == null) {
            propertyModel.setObject(null);
        } else {
            propertyModel.setObject(state.getStateCode());
        }
    }

    public void detach() {
        if (state instanceof IDetachable) {
            ((IDetachable)state).detach();
        }
    }
}
