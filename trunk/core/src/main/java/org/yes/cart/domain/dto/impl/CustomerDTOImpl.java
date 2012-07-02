package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.entity.impl.AttrValueEntityCustomer;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */

@Dto
public class CustomerDTOImpl implements CustomerDTO {

    @DtoField(value = "customerId", readOnly = true)
    private long customerId;

    @DtoField(value = "email")
    private String email;

    @DtoField(value = "firstname")
    private String firstname;

    @DtoField(value = "lastname")
    private String lastname;

    @DtoField(value = "middlename")
    private String middlename;

    @DtoCollection(
            value="attribute",
            dtoBeanKey="org.yes.cart.domain.dto.AttrValueCustomerDTO",
            entityGenericType = AttrValueEntityCustomer.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = HashSet.class,
            dtoToEntityMatcher = AttrValueCustomerMatcher.class,
            readOnly = true
            )
    private Set<AttrValueCustomerDTO> attribute;


    /** {@inheritDoc} */
    public long getCustomerId() {
        return customerId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return customerId;
    }

    /** {@inheritDoc} */
    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    /** {@inheritDoc} */
    public String getEmail() {
        return email;
    }

    /** {@inheritDoc} */
    public void setEmail(final String email) {
        this.email = email;
    }
                        /** {@inheritDoc} */
    public String getFirstname() {
        return firstname;
    }

    /** {@inheritDoc} */
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    /** {@inheritDoc} */
    public String getLastname() {
        return lastname;
    }

    /** {@inheritDoc} */
    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    /** {@inheritDoc} */
    public String getMiddlename() {
        return middlename;
    }

    /** {@inheritDoc} */
    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    /** {@inheritDoc} */
    public Set<AttrValueCustomerDTO> getAttribute() {
        return attribute;
    }

    /** {@inheritDoc} */
    public void setAttribute(final Set<AttrValueCustomerDTO> attribute) {
        this.attribute = attribute;
    }
}
