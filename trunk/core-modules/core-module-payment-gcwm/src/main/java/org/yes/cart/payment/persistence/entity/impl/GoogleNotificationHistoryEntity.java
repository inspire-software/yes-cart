package org.yes.cart.payment.persistence.entity.impl;

import org.yes.cart.payment.persistence.entity.GoogleNotificationHistory;

import javax.persistence.*;
import java.util.Date;

/**
 * {@inheritDoc}
 */
@Entity
@Table(name = "TGOOGLENOTIFICATION")
public class GoogleNotificationHistoryEntity implements GoogleNotificationHistory {

    private static final long serialVersionUID = 2012015L;


    private long googleNotifcationId;
    private String serialNumber;
    private String notification;

    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    /**
     * {@inheritDoc}
     */
    @Id
    @GeneratedValue
    @Column(name = "GOOGLENOTIFICATION_ID", nullable = false)
    public long getGoogleNotifcationId() {
        return googleNotifcationId;
    }

    /**
     * {@inheritDoc}
     */
    public void setGoogleNotifcationId(final long googleNotifcationId) {
        this.googleNotifcationId = googleNotifcationId;
    }

    /**
     * {@inheritDoc}
     */
    @Column(name = "SERIAL_NUMBER", length = 64, nullable = false)
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * {@inheritDoc}
     */
    public void setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Lob
    @Column(name = "NOTIFICATION")
    public String getNotification() {
        return notification;
    }

    /**
     * {@inheritDoc}
     */
    public void setNotification(final String notification) {
        this.notification = notification;
    }

    /**
     * {@inheritDoc}
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP")
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    public void setCreatedTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIMESTAMP")
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    public void setUpdatedTimestamp(final Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Column(name = "CREATED_BY", length = 64)
    public String getCreatedBy() {
        return this.createdBy;
    }

    /**
     * {@inheritDoc}
     */
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * {@inheritDoc}
     */
    @Column(name = "UPDATED_BY", length = 64)
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    /**
     * {@inheritDoc}
     */
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * {@inheritDoc}
     */
    @Column(name = "GUID", length = 36, nullable = false)
    public String getGuid() {
        return this.guid;
    }

    /**
     * {@inheritDoc}
     */
    public void setGuid(final String guid) {
        this.guid = guid;
    }


}
