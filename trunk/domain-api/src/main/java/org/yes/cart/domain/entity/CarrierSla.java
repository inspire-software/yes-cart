
package org.yes.cart.domain.entity;

import java.math.BigDecimal;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 * <p/>
 * Represent carrier SLA, that can be quite complex. At this moment
 * several strategies(types) are supported:
 * <p/>
 * 1. E - External
 * 2. R - Free
 * <p/>
 * 3. F - Fixed
 * 4. O - Per Order
 * 5. P - Per package
 * <p/>
 * Each FOP stategy has destination flag. It mean that price and percent will be selected as
 * single values in case of F - fixed strategy
 * or
 * null delivery cost in case of free shipping
 * or
 * maximum from destination-weight / destination-volume matrix for Per Order or Per Package.
 * <p/>
 * <p/>
 * This this not represent the actual shipping rates for delivery but price for customer.
 *
 * Total devilery cost cab be limited as well as price and percent 
 *
 * When price ant percent are obtained cost calculation
 * will be following:
 *
 * (F)ixed
 *     delivery cost = 1 * price + max(order_sub_total * percent, percent_not_less)
 * (O) Per Order
 *     delivery cost = max( 1 * price                                 + max(order_sub_total * percent, percent_not_less), cost_not_less)
 * (P) Per package
 *     delivery cost = max( max(packages qty * price, price_not_less) + max(     item_price * percent, percent_not_less), cost_not_less)
 *
 */
public interface CarrierSla extends Auditable {

    /**
     * External procedure delivery cost calcluation
     */
    public static String EXTERNAL = "E";

    /**
     * Free delivery cost calcluation
     */
    public static String FREE = "R";

    /**
     * Fixed price, percent
     */
    public static String FIXED = "F";

    /**
     * Per order price, percent
     */
    public static String PER_ORDER = "O";

    /**
     * Per package price, percent
     */
    public static String PER_PACKAGE = "P";

    /**
     * Get pk value.
     *
     * @return pk value
     */
    long getCarrierslaId();

    /**
     * Set pk value.
     *
     * @param carrierslaId pk value
     */
    void setCarrierslaId(long carrierslaId);


    /**
     * Get SLA type.
     *
     * @return SLA type.
     */
    String getSlaType();

    /**
     * Set SLA type.
     *
     * @param slaType SLA type.
     */
    void setSlaType(String slaType);


    /**
     * Get SLA name.
     *
     * @return SLA name
     */
    String getName();

    /**
     * Set SLA name.
     *
     * @param name SLA name
     */
    void setName(String name);

    /**
     * Get SLA adescition.
     *
     * @return SLA desrpition.
     */
    String getDescription();

    /**
     * Set desvcription.
     *
     * @param description description
     */
    void setDescription(String description);

    /**
     *
     * Get price. This is for internal usage only. Not a part of api.
     * Price of delivery must be obtained from service.
     *
     * @return price.
     */
    BigDecimal getPrice();

    /**
     * Set price. This is for internal usage only.
     *
     * @param price price.
     */
    void setPrice(BigDecimal price);

    /**
     * Get percent.
     *
     * @return percent.
     */
    BigDecimal getPercent();

    /**
     * Set percent.
     *
     * @param percent percent.
     */
    void setPercent(BigDecimal percent);


    /**
     * Get minimal cost for delivery.
     * @return minimal cost for delivery.
     */
    BigDecimal getPriceNotLess();

    /**
     * Set minimal cost for delivery.
     * @param priceNotLess minimal cost for delivery.
     */
    void setPriceNotLess(BigDecimal priceNotLess);

    /**
     * Get minimal amount  for precent.
     * @return minimal amount  for precent.
     */
    BigDecimal getPercentNotLess();

    /**
     * Set minimal amount  for precent.
     * @param percentNotLess minimal amount  for precent.
     */
    void setPercentNotLess(BigDecimal percentNotLess);

    /**
     * Get minimal delivery cost.
     * @return minimal delivery cost.
     */
    BigDecimal getCostNotLess();

    /**
     * Set minimal delivery cost.
     * @param costNotLess minimal delivery cost.
     */
    void setCostNotLess(BigDecimal costNotLess);

    /**
     * Get external script to calculate delivery cost.
     *
     * @return external script.
     */
    String getScript();

    /**
     * Set external script to calculate delivery cost.
     *
     * @param script script.
     */

    void setScript(String script);


    /**
     * Get currency.
     *
     * @return currency.
     */
    String getCurrency();

    /**
     * Set currency.
     *
     * @param currency currency.
     */
    void setCurrency(String currency);


    /**
     * Get max deys delivery.     
     * @return max deys delivery.
     */
    Integer getMaxDays();

    /**
     * Set max deys delivery.
     *
     * @param maxDays max deys delivery.
     */
    void setMaxDays(Integer maxDays);


    /**
     * Get carrier.
     *
     * @return {@link Carrier}
     */
    Carrier getCarrier();

    /**
     * Set carrier.
     *
     * @param carrier carrire.
     */
    void setCarrier(Carrier carrier);

}


