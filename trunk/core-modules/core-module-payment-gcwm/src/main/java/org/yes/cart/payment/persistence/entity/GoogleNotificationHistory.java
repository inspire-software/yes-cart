package org.yes.cart.payment.persistence.entity;

/**
 * Responcible to track google checkout notification history.
 */
interface GoogleNotificationHistory extends Auditable {


    long getGoogleNotifcationId();

    void setGoogleNotifcationId(long googleNotifcationId);

    String getSerialNumber();

    void setSerialNumber(String serialNumber) ;

    String getNotification() ;

    void setNotification(String notification);
    


}
