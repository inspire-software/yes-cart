package org.yes.cart.domain.entity;

/**
 *
 * Warehouse entity. Each shop can have several warehouses.
 * Address part of warehouse used to:
 *
 * 1. Show only suitable delivery options to customer.
 * 2. Devivery planing in case of splited delivery or splited orders.
 *
 */
public interface Warehouse extends Auditable {


    /**
     * Get primary key.
     *
     * @return primary key
     */
    public long getWarehouseId();

    /**
     * Set primary key
     *
     * @param warehouseId primary key.
     */
    public void setWarehouseId(long warehouseId);

    /**
     * Get warehouse code.
     *
     * @return warehouse code.
     */
    String getCode();

    /**
     * Set warehouse code.
     *
     * @param code warehouse code.
     */
    void setCode(String code);


    /**
     * Warehouse name.
     *
     * @return warehouse name.
     */
    public String getName();

    /**
     * Set Warehouse name.
     *
     * @param name name of warehouse
     */
    public void setName(String name);

    /**
     * @return description.
     */
    public String getDescription();

    /**
     * Set description.
     *
     * @param description warehouse description.
     */
    public void setDescription(String description);






    // address part of warehouse begin
    /**
     * Get country.
     * @return coubtry.
     */
    String getCountryCode();

    /**
     * Set country.
     * @param countryCode country to set
     */
    void setCountryCode(String countryCode);

    /**
     * State or province code.
     * @return state or province code
     */
    String getStateCode();

    /**
     * Set state or province.
     * @param stateCode state.
     */
    void setStateCode(final String stateCode);

    /**
     * Get city.
     * @return city
     */
    String getCity();

    /**
     * Set city
     * @param city value to set
     */
    void setCity(String city);

    /**
     * Get postcode.
     * @return post code
     */
    String getPostcode();

    /**
     * Set post code
     * @param postcode value to set
     */
    void setPostcode(String postcode);

    // address part of warehouse end


}


