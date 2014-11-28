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

package org.yes.cart.service.federation.impl;

import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.Collection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 10/10/2014
 * Time: 14:43
 */
public abstract class AbstractImpexFederationFilterImpl implements FederationFilter {

    private final ShopFederationStrategy shopFederationStrategy;
    private final List<String> roles;

    protected AbstractImpexFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy,
                                                final List<String> roles) {
        this.shopFederationStrategy = shopFederationStrategy;
        this.roles = roles;
    }

    /**
     * {@inheritDoc}
     */
    public final void applyFederationFilter(final Collection list, final Class objectType) {
        throw new UnsupportedOperationException("Import does not support multi checks");
    }

    /**
     * Check that current manager has access roles.
     *
     * @return true if has access roles
     */
    protected final boolean hasAccessRole() {
        for (final String role : this.roles) {
            if (this.shopFederationStrategy.isCurrentUser(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if object is transient (i.e. new).
     *
     * @param object object to check
     *
     * @return true if object is new
     */
    protected final boolean isTransientEntity(final Object object) {
        return object instanceof Identifiable && ((Identifiable) object).getId() <= 0L;
    }
}
