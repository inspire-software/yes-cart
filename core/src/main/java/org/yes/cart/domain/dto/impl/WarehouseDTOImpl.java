package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.WarehouseDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class WarehouseDTOImpl implements WarehouseDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "warehouseId", readOnly = true)
    private long warehouseId;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "countryCode")
    private String countryCode;

    @DtoField(value = "stateCode")
    private String stateCode;

    @DtoField(value = "city")
    private String city;

    @DtoField(value = "postcode")
    private String postcode;



    /** {@inheritDoc}*/
    public long getWarehouseId() {
        return warehouseId;
    }

    /** {@inheritDoc}*/
    public long getId() {
        return warehouseId;
    }

    /** {@inheritDoc}*/
    public void setWarehouseId(final long warehouseId) {
        this.warehouseId = warehouseId;
    }

    /** {@inheritDoc}*/
    public String getCode() {
        return code;
    }

    /** {@inheritDoc}*/
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc}*/
    public String getName() {
        return name;
    }

    /** {@inheritDoc}*/
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc}*/
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc}*/
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc}*/
    public String getCountryCode() {
        return countryCode;
    }

    /** {@inheritDoc}*/
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /** {@inheritDoc}*/
    public String getStateCode() {
        return stateCode;
    }

    /** {@inheritDoc}*/
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /** {@inheritDoc}*/
    public String getCity() {
        return city;
    }

    /** {@inheritDoc}*/
    public void setCity(final String city) {
        this.city = city;
    }

    /** {@inheritDoc}*/
    public String getPostcode() {
        return postcode;
    }

    /** {@inheritDoc}*/
    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }
}
