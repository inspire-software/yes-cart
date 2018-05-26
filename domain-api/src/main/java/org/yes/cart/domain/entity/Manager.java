/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.domain.entity;

import java.util.Collection;

/**
 * Managers table hold login, password and names
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Manager extends RegisteredPerson, Auditable {


    /**
     * Get assigned shops.
     *
     * @return shops
     */
    Collection<ManagerShop> getShops();

    /**
     * Set assigned shops.
     *
     * @param shops shops
     */
    void setShops(Collection<ManagerShop> shops);


    /**
     * Primary key.
     *
     * @return pk value.
     */
    long getManagerId();

    /**
     * Set pk.
     *
     * @param managerId pk value.
     */
    void setManagerId(long managerId);

    /**
     * Get dashboard widgets.
     *
     * @return selected widgets
     */
    String getDashboardWidgets();

    /**
     * Set dashboard widgets.
     *
     * @param dashboardWidgets widgets
     */
    void setDashboardWidgets(String dashboardWidgets);

    /**
     * Get enabled flag
     *
     * @return true if account is enabled
     */
    boolean getEnabled();

    /**
     * Set enabled flag
     *
     * @param enabled true if account is enabled
     */
    void setEnabled(boolean enabled);

}
