package org.yes.cart.payment.persistence.entity;

/**
 * Responsible to track google checkout notification history.
 */
public interface GoogleNotificationHistory extends Auditable {


    /**
     * Get pk.
     * @return pk value.
     */
    long getGoogleNotifcationId();

    /**
     * Set pk value.
     * @param googleNotifcationId  pk value
     */
    void setGoogleNotifcationId(long googleNotifcationId);

    /**
     * Get serial number.
     * @return  serial number.
     */
    String getSerialNumber();

    /**
     * Set serial number.
     * @param serialNumber  serial number.
     */
    void setSerialNumber(String serialNumber) ;

    /**
     * Get notification body.
     * @return  notification body.
     */
    String getNotification() ;

    /**
     * Set notification body.
     * @param notification  xml body of notification
     */
    void setNotification(String notification);
    


}
