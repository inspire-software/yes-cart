package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AddressDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class AddressDTOImpl implements AddressDTO {

    private static final long serialVersionUID = 20100717L;

    @DtoField(value = "addressId", readOnly = true)
    private long addressId;

    @DtoField(value = "city")
    private String city;

    @DtoField(value = "postcode")
    private String postcode;

    @DtoField(value = "addrline1")
    private String addrline1;

    @DtoField(value = "addrline2")
    private String addrline2;

    @DtoField(value = "addressType")
    private String addressType;

    @DtoField(value = "countryCode")
    private String countryCode;

    @DtoField(value = "stateCode")
    private String stateCode;


    @DtoField(value = "firstname")
    private String firstname;

    @DtoField(value = "lastname")
    private String lastname;

    @DtoField(value = "middlename")
    private String middlename;

    @DtoField(value = "defaultAddress")
    private boolean defaultAddress;

    @DtoField(value = "phoneList")
    private String phoneList;

    @DtoField(value = "customer.customerId", readOnly = true)
    private long customerId;


    /** {@inheritDoc} */
    public String getPhoneList() {
        return phoneList;
    }

    /** {@inheritDoc} */
    public void setPhoneList(final String phoneList) {
        this.phoneList = phoneList;
    }

    /** {@inheritDoc} */
    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    /** {@inheritDoc} */
    public void setDefaultAddress(final boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    /** {@inheritDoc} */
    public String getStateCode() {
        return stateCode;
    }

    /** {@inheritDoc} */
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /** {@inheritDoc} */
    public long getCustomerId() {
        return customerId;
    }

    /** {@inheritDoc} */
    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    /** {@inheritDoc} */
    public long getAddressId() {
        return addressId;
    }

    /** {@inheritDoc} */
    public long getId() {
        return addressId;
    }


    /** {@inheritDoc} */
    public void setAddressId(final long addressId) {
        this.addressId = addressId;
    }

    /** {@inheritDoc} */
    public String getCity() {
        return city;
    }

    /** {@inheritDoc} */
    public void setCity(final String city) {
        this.city = city;
    }

    /** {@inheritDoc} */
    public String getPostcode() {
        return postcode;
    }

    /** {@inheritDoc} */
    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    /** {@inheritDoc} */
    public String getAddrline1() {
        return addrline1;
    }

    /** {@inheritDoc} */
    public void setAddrline1(final String addrline1) {
        this.addrline1 = addrline1;
    }

    /** {@inheritDoc} */
    public String getAddrline2() {
        return addrline2;
    }

    /** {@inheritDoc} */
    public void setAddrline2(final String addrline2) {
        this.addrline2 = addrline2;
    }

    /** {@inheritDoc} */
    public String getAddressType() {
        return addressType;
    }

    /** {@inheritDoc} */
    public void setAddressType(final String addressType) {
        this.addressType = addressType;
    }

    /** {@inheritDoc} */
    public String getCountryCode() {
        return countryCode;
    }

    /** {@inheritDoc} */
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
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
}
