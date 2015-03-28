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

package org.yes.cart.domain.dto.matcher.impl;

import com.inspiresoftware.lib.dto.geda.adapter.DtoToEntityMatcher;
import org.yes.cart.domain.entity.Identifiable;

/**
 * User: denispavlov
 * Date: 20/08/2014
 * Time: 10:08
 */
public class IdentifiableMatcher implements DtoToEntityMatcher<Identifiable, Identifiable> {

    @Override
    public boolean match(final Identifiable id1, final Identifiable id2) {
        return id1 != null && id2 != null && id1.getId() == id2.getId();
    }
}
