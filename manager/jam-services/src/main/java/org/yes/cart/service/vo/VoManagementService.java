/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.VoLicenseAgreement;
import org.yes.cart.domain.vo.VoManager;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 08:58
 */
public interface VoManagementService {

    /**
     * Get information about current user
     *
     * @return manager vo
     *
     * @throws Exception
     */
    VoManager getMyself() throws Exception;

    /**
     * Get license for current user
     *
     * @return license vo
     *
     * @throws Exception
     */
    VoLicenseAgreement getMyAgreement() throws Exception;

    /**
     * Accept license for current user.
     *
     * @return license vo
     *
     * @throws Exception
     */
    VoLicenseAgreement acceptMyAgreement() throws Exception;

}
