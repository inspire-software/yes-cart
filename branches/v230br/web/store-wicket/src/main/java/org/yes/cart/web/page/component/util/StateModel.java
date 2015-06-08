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

package org.yes.cart.web.page.component.util;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
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
