package org.yes.cart.payment.persistence.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 * Auditable hold auditing fields, that also used to getByKey database changes slice
 * for particular user(s) in time frame(s). Change slice used for apply changes, that
 * already done on staging server, to productions server.
 * Guid field allow to make a decision what records shall be inserted/updated.
 * Here we have some limitionations for transfer change slices:
 * 1. The same changes on several different staging servers will be added to production server.
 */
public interface Auditable extends Serializable {
    /**
     * @return created timestamp.
     */
    Date getCreatedTimestamp();

    /**
     * @param createdTimestamp set created timestamp.
     */
    void setCreatedTimestamp(Date createdTimestamp);

    /**
     * @return updated timestamp.
     */
    Date getUpdatedTimestamp();

    /**
     * @param updatedTimestamp set updated timestamp.
     */
    void setUpdatedTimestamp(Date updatedTimestamp);

    /**
     * @return created by user identificator.
     */
    String getCreatedBy();

    /**
     * @param createdBy created by user identificator.
     */
    void setCreatedBy(String createdBy);

    /**
     * @return updated by user identificator.
     */
    String getUpdatedBy();

    /**
     * @param updatedBy updated by user identificator.
     */
    void setUpdatedBy(String updatedBy);

    /**
     * @return record guid.
     */
    String getGuid();

    /**
     * @param guid record guid.
     */
    void setGuid(String guid);


}
