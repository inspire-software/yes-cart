package org.yes.cart.payment.dto;

import java.io.Serializable;

/**
 * Payment gateway supported features.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 13:24:24
 */


public interface PaymentGatewayFeature extends Serializable {


    /**
     * Some payment gateway supports auth operation per
     * whole order, some per each shipment.
     *
     * @return true if supports per each shipment.
     */
    boolean isSupportAuthorizePerShipment();


    /**
     * Is payment gateway supports authorize. Payment processor may use
     * the AuthotizeCapture opration if not.
     *
     * @return true if supports
     */
    boolean isSupportAuthorize();

    /**
     * Is payment gateway supports captures on prev authorization.
     *
     * @return true if supports
     */
    boolean isSupportCapture();

    /**
     * Is payment gateway supports imediate sale operation, without delivery confirmation.
     *
     * @return true if supports
     */
    boolean isSupportAuthorizeCapture();

    /**
     * Is payment gateway supports void of a previous capture.
     *
     * @return true if supports
     */
    boolean isSupportVoid();


    /**
     * Is payment gateway supports reverse a previous authorization.
     *
     * @return true if supports
     */
    boolean isSupportReverseAuthorization();

    /**
     * Is payment gateway supports refunds a previous capture.
     *
     * @return true if supports
     */
    boolean isSupportRefund();

    /**
     * Is form will be processed on external site. If true, than need to provide backgound urls.
     *
     * @return true
     */
    boolean isExternalFormProcessing();


    /**
     * Get additional supported features in key1=value1[,key2=value2] string.
     *
     * @return key=value string.
     */
    String getAdditionalFeatures();

    /**
     * @param supportAuthorize true if supports
     */
    void setSupportAuthorize(final boolean supportAuthorize);

    /**
     * @param supportCapture true if supports
     */
    void setSupportCapture(final boolean supportCapture);

    /**
     * @param supportAuthorizeCapture true if supports
     */
    void setSupportAuthorizeCapture(final boolean supportAuthorizeCapture);

    /**
     * @param supportVoid true if supports
     */
    void setSupportVoid(final boolean supportVoid);

    /**
     * @param supportReverseAuthorization true if supports
     */
    void setSupportReverseAuthorization(final boolean supportReverseAuthorization);

    /**
     * @param supportRefund true if supports
     */
    void setSupportRefund(final boolean supportRefund);

    /**
     * @param externalFormProcessing true if supports
     */
    void setExternalFormProcessing(final boolean externalFormProcessing);


    /**
     * @param supportAuthorizePerShipment true if supports
     */
    void setSupportAuthorizePerShipment(final boolean supportAuthorizePerShipment);

    /**
     * @param additionalFeatures true if supports
     */
    void setAdditionalFeatures(final String additionalFeatures);

    /**
     * {@inheritDoc }
     */
    boolean isOnlineGateway();

    /**
     * {@inheritDoc }
     */
    void setOnlineGateway(boolean onlineGateway);

}
