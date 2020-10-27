/*
 * Copyright 2009 Inspire-Software.com
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

import java.time.Instant;

/**
 * Represent any registered person in system: customer, admin, call center , etc.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface RegisteredPerson {

    /**
     * Get person id.
     *
     * @return customer email.
     */
    String getEmail();

    /**
     * Set customer email
     *
     * @param email email
     */
    void setEmail(String email);

    /**
     * Get contact phone.
     *
     * @return customer phone.
     */
    String getPhone();

    /**
     * Set customer phone
     *
     * @param phone phone
     */
    void setPhone(String phone);

    /**
     * Get first name.
     *
     * @return first name
     */
    String getFirstname();

    /**
     * Set first name
     *
     * @param firstname value to set
     */
    void setFirstname(String firstname);

    /**
     * Get last name.
     *
     * @return last name
     */
    String getLastname();

    /**
     * Set last name
     *
     * @param lastname value to set
     */
    void setLastname(String lastname);

    /**
     * Get middle name
     *
     * @return middle name
     */
    String getMiddlename();

    /**
     * Set middle name
     *
     * @param middlename value to set
     */
    void setMiddlename(String middlename);

    /**
     * Get salutation
     *
     * @return salutation
     */
    String getSalutation();

    /**
     * Set salutation
     *
     * @param salutation value to set
     */
    void setSalutation(String salutation);

    /**
     * Get login.
     *
     * @return login
     */
    String getLogin();

    /**
     * Set login
     *
     * @param login login (e.g. email, phone, nickname)
     */
    void setLogin(String login);

    /**
     * Get password.
     *
     * @return password.
     */
    String getPassword();

    /**
     * Set password.
     *
     * @param password new password.
     */
    void setPassword(String password);

    /**
     * Get password.
     *
     * @return password expiry.
     */
    Instant getPasswordExpiry();

    /**
     * Set password.
     *
     * @param passwordExpiry password expiry.
     */
    void setPasswordExpiry(Instant passwordExpiry);

    /**
     * Authorisation token.
     *
     * @return auth token (for resetting passwords)
     */
    String getAuthToken();

    /**
     * Authorisation token.
     *
     * @param authToken auth token (for resetting passwords)
     */
    void setAuthToken(String authToken);

    /**
     * Authorisation token expiry.
     *
     * @return auth token expiry (for resetting passwords)
     */
    Instant getAuthTokenExpiry();

    /**
     * Authorisation token expiry.
     *
     * @param authTokenExpiry auth token expiry (for resetting passwords)
     */
    void setAuthTokenExpiry(Instant authTokenExpiry);

    /**
     * Company name for this person.
     *
     * @return company name
     */
    String getCompanyName1();

    /**
     * Company name for this person.
     *
     * @param companyName1 name
     */
    void setCompanyName1(String companyName1);

    /**
     * Company name for this person.
     *
     * @return company name
     */
    String getCompanyName2();

    /**
     * Company name for this person.
     *
     * @param companyName2 name
     */
    void setCompanyName2(String companyName2);


    /**
     * Company department for this person.
     *
     * @return company department
     */
    String getCompanyDepartment();

    /**
     * Company department for this person.
     *
     * @param companyDepartment department
     */
    void setCompanyDepartment(String companyDepartment);

}
