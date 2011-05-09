package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ManagerDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ManagerDTOImpl implements ManagerDTO {

    private static final long serialVersionUID = 20100421L;

    @DtoField(value = "email", readOnly = true)
    private String email;

    @DtoField(value = "firstname", readOnly = true)
    private String firstName;

    @DtoField(value = "lastname", readOnly = true)
    private String lastName;

    /**
     * {@inheritDoc}
     */
    public String getEmail() {
        return email;
    }

    /**
     * {@inheritDoc}
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * {@inheritDoc}
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * {@inheritDoc}
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * {@inheritDoc}
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * {@inheritDoc}
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ManagerDTOImpl that = (ManagerDTOImpl) o;

        if (!email.equals(that.email)) return false;
        if (!firstName.equals(that.firstName)) return false;
        if (!lastName.equals(that.lastName)) return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
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
