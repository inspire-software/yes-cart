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

/**
 * Relation between manages and his roles
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ManagerRole extends Auditable {

    /**
     * @return email of manager who this role has been granted to
     */
    String getEmail();

    /**
     * @param email email of manager who this role has been granted to
     */
    void setEmail(String email);

    /**
     * @return Role code
     */
    String getCode();

    /**
     * @param code role code
     */
    void setCode(String code);

    /**
     * @return unique PK
     */
    long getManagerRoleId();

    /**
     * @param managerRoleId unique PK
     */
    void setManagerRoleId(long managerRoleId);
}
