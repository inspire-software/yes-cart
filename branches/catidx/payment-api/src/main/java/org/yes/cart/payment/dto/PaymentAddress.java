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

package org.yes.cart.payment.dto;

import java.io.Serializable;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 13:24:24
 */
public interface PaymentAddress extends Serializable {

    /**
     * Get city.
     *
     * @return city
     */
    String getCity();

    /**
     * Set city
     *
     * @param city value to set
     */
    void setCity(String city);

    /**
     * Get postcode.
     *
     * @return post code
     */
    String getPostcode();

    /**
     * Set post code
     *
     * @param postcode value to set
     */
    void setPostcode(String postcode);

    /**
     * Addr line 1.
     *
     * @return addr line 1
     */
    String getAddrline1();

    /**
     * Set first address line
     *
     * @param addrline1 value to set
     */
    void setAddrline1(String addrline1);

    /**
     * Get second address line
     *
     * @return value to set
     */
    String getAddrline2();

    /**
     * Set address line 2.
     *
     * @param addrline2 value to set
     */
    void setAddrline2(String addrline2);

    /**
     * Get address type
     *
     * @return addr type
     */
    String getAddressType();

    /**
     * Set addr type.
     *
     * @param addressType value to set
     */
    void setAddressType(String addressType);

    /**
     * Get country.
     *
     * @return coubtry.
     */
    String getCountryCode();

    /**
     * Set country.
     *
     * @param countryCode country to set
     */
    void setCountryCode(String countryCode);


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
     * State or province code.
     *
     * @return state or province code
     */
    String getStateCode();

    /**
     * Set state or province.
     *
     * @param stateCode state.
     */
    void setStateCode(final String stateCode);


    /**
     * Get comma separated list of phones of one phone.
     *
     * @return comma separated list of phones of one phone.
     */
    String getPhoneList();


    /**
     * set comma separated list of phones of one phone.
     *
     * @param phoneList comma separated list of phones of one phone.
     */
    void setPhoneList(String phoneList);


}
