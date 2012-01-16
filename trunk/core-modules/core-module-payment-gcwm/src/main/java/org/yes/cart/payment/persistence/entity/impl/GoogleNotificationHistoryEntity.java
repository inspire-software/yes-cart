package org.yes.cart.payment.persistence.entity.impl;

/**
 * {@inheritDoc}
 */
public class GoogleNotificationHistoryEntity {


    private long googleNotifcationId;
    private String serialNumber;
    private String notification;


    public long getGoogleNotifcationId() {
        return googleNotifcationId;
    }

    public void setGoogleNotifcationId(long googleNotifcationId) {
        this.googleNotifcationId = googleNotifcationId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
