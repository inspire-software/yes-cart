package org.yes.cart.domain.dto;

import java.io.Serializable;

/**
 * Area, state or county.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface StateDTO extends Serializable {

    /**
     * Get contry code.
     *
     * @return contry code.
     */
    String getCountryCode();

    /**
     * Set contry code.
     *
     * @param countryCode code to set.
     */
    void setCountryCode(String countryCode);

    /**
     * Get state/area code.
     *
     * @return state/area code.
     */
    String getStateCode();

    /**
     * Set state/area code.
     *
     * @param stateCode code to set.
     */
    void setStateCode(String stateCode);

    /**
     * Get name.
     *
     * @return name.
     */
    String getName();

    /**
     * Set name.
     *
     * @param name name to set.
     */
    void setName(String name);

    /**
     * Get state pk value.
     *
     * @return state pk value.
     */
    long getStateId();

    /**
     * Set pk value.
     *
     * @param stateId pl value.
     */
    void setStateId(long stateId);


}
