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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

import java.util.Collection;

/**
 * Shop manager DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ManagerDTO extends Identifiable {

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
     * Get the login mane, that equals to email.
     *
     * @return user email.
     */
    String getEmail();

    /**
     * Set email.
     *
     * @param email email.
     */
    void setEmail(String email);


    /**
     * Get first name.
     *
     * @return first name.
     */
    String getFirstName();

    /**
     * Set first name
     *
     * @param firstName new first name.
     */
    void setFirstName(String firstName);


    /**
     * Get last name
     *
     * @return last name.
     */
    String getLastName();

    /**
     * Set last name
     *
     * @param lastName lat name.
     */
    void setLastName(String lastName);

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
     * Company name for this person.
     *
     * @return company name
     */
    String getCompanyName1();

    /**
     * Company name for this person.
     *
     * @param companyName1 name
     */
    void setCompanyName1(String companyName1);

    /**
     * Company name for this person.
     *
     * @return company name
     */
    String getCompanyName2();

    /**
     * Company name for this person.
     *
     * @param companyName2 name
     */
    void setCompanyName2(String companyName2);


    /**
     * Company department for this person.
     *
     * @return company department
     */
    String getCompanyDepartment();

    /**
     * Company department for this person.
     *
     * @param companyDepartment department
     */
    void setCompanyDepartment(String companyDepartment);


    /**
     * Get assigned product suppliers
     *
     * @return product supplier codes
     */
    Collection<String> getProductSupplierCatalogs();

    /**
     * Set  assigned product suppliers
     *
     * @param productSupplierCatalogs  product supplier codes
     */
    void setProductSupplierCatalogs(Collection<String> productSupplierCatalogs);

    /**
     * Get assigned category catalogs
     *
     * @return category codes
     */
    Collection<String> getCategoryCatalogs();

    /**
     * Set assigned category catalogs
     *
     * @param categoryCatalogs category codes
     */
    void setCategoryCatalogs(Collection<String> categoryCatalogs);


}
