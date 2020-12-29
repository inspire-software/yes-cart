/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ManagerDTO;

import java.time.Instant;
import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ManagerDTOImpl implements ManagerDTO {

    private static final long serialVersionUID = 20100421L;

    @DtoField(value = "managerId", readOnly = true)
    private long managerId;

    @DtoField(value = "login", readOnly = true)
    private String login;

    @DtoField(value = "email", readOnly = true)
    private String email;

    @DtoField(value = "firstname", readOnly = true)
    private String firstName;

    @DtoField(value = "lastname", readOnly = true)
    private String lastName;

    @DtoField(value = "enabled", readOnly = true)
    private boolean enabled;

    @DtoField(value = "dashboardWidgets", readOnly = true)
    private String dashboardWidgets;

    @DtoField(value = "companyName1")
    private String companyName1;

    @DtoField(value = "companyName2")
    private String companyName2;

    @DtoField(value = "companyDepartment")
    private String companyDepartment;

    @DtoField(value = "productSupplierCatalogs")
    private Collection<String> productSupplierCatalogs;

    @DtoField(value = "categoryCatalogs")
    private Collection<String> categoryCatalogs;


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getManagerId() {
        return managerId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return managerId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setManagerId(final long managerId) {
        this.managerId = managerId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLogin() {
        return login;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLogin(final String login) {
        this.login = login;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFirstName() {
        return firstName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLastName() {
        return lastName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getEnabled() {
        return enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDashboardWidgets() {
        return dashboardWidgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDashboardWidgets(final String dashboardWidgets) {
        this.dashboardWidgets = dashboardWidgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCompanyName1() {
        return companyName1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCompanyName1(final String companyName1) {
        this.companyName1 = companyName1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCompanyName2() {
        return companyName2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCompanyName2(final String companyName2) {
        this.companyName2 = companyName2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCompanyDepartment() {
        return companyDepartment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCompanyDepartment(final String companyDepartment) {
        this.companyDepartment = companyDepartment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getProductSupplierCatalogs() {
        return productSupplierCatalogs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProductSupplierCatalogs(final Collection<String> productSupplierCatalogs) {
        this.productSupplierCatalogs = productSupplierCatalogs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getCategoryCatalogs() {
        return categoryCatalogs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCategoryCatalogs(final Collection<String> categoryCatalogs) {
        this.categoryCatalogs = categoryCatalogs;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) {
            return true;
        }
        if (otherObj == null || getClass() != otherObj.getClass()) {
            return false;
        }

        final ManagerDTOImpl that = (ManagerDTOImpl) otherObj;

        if (!email.equals(that.email)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return email.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ManagerDTOImpl{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
