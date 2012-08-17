/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.domain.entity;

import java.util.Date;

/**
 * Auditable holds auditing fields, that is also used to "getByKey" database changes slice
 * for particular user(s) in time frame(s). Change slice used for applying changes
 * to production servers that have already been done on staging server.
 *
 * GUID field allows to make a decision what records shall be inserted/updated.
 * Here we have some limitations for transferring change slices:
 * 1. The same changes on several different staging servers will be added to production server.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 * <p/>
 */
public interface Auditable extends Identifiable, Guidable {

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

}
