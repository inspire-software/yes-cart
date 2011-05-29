package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AvailabilityDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class AvailabilityDTOImpl implements AvailabilityDTO {

    private static final long serialVersionUID = 20100717L;

    @DtoField(value = "availabilityId")
    private long availabilityId;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    /** {@inheritDoc}*/
    public long getAvailabilityId() {
        return availabilityId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return availabilityId;
    }

    /** {@inheritDoc}*/
    public void setAvailabilityId(final long availabilityId) {
        this.availabilityId = availabilityId;
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
}
