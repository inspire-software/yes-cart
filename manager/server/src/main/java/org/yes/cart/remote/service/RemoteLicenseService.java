/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.remote.service;

/**
 * User: denispavlov
 * Date: 29/04/2016
 * Time: 09:18
 */
public interface RemoteLicenseService {

    /**
     * Get license text to be displayed to users of YUM application.
     *
     * This license applies to all YC web applications and if the user does not
     * agree with terms they should not use this software.
     *
     * @return license text
     */
    String getLicenseText();

    /**
     * Check if current user has agreed to the license.
     *
     * @return true if agreed, false other wise
     */
    boolean isAgreedToLicense();

    /**
     * Method called when used clicks "Agree" button in YUM to confirm they
     * agreed to license.
     */
    void agreeToLicense();

}
