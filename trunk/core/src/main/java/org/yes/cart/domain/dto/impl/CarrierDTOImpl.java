package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.CarrierDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class CarrierDTOImpl implements CarrierDTO {

    @DtoField(value = "carrierId", readOnly = true)
    private long carrierId;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "worldwide")
    private boolean worldwide;

    @DtoField(value = "country")
    private boolean country;

    @DtoField(value = "state")
    private boolean state;

    @DtoField(value = "local")
    private boolean local;

    /** {@inheritDoc} */
    public long getCarrierId() {
        return carrierId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return carrierId;
    }

    /** {@inheritDoc} */
    public void setCarrierId(final long carrierId) {
        this.carrierId = carrierId;
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    public boolean isWorldwide() {
        return worldwide;
    }

    /** {@inheritDoc} */
    public void setWorldwide(final boolean worldwide) {
        this.worldwide = worldwide;
    }

    /** {@inheritDoc} */
    public boolean isCountry() {
        return country;
    }

    /** {@inheritDoc} */
    public void setCountry(final boolean country) {
        this.country = country;
    }

    /** {@inheritDoc} */
    public boolean isState() {
        return state;
    }

    /** {@inheritDoc} */
    public void setState(final boolean state) {
        this.state = state;
    }

    /** {@inheritDoc} */
    public boolean isLocal() {
        return local;
    }

    /** {@inheritDoc} */
    public void setLocal(final boolean local) {
        this.local = local;
    }
}
