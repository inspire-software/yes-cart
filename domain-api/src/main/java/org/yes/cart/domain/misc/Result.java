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

package org.yes.cart.domain.misc;

/**
 *
 * Simple class to hold operation result.
 *
 * User: iazarny@yahoo.com
 * Date: 9/1/12
 * Time: 9:49 AM
 */
public final class Result {

    public static final String OK = "0";

    private  String errorCode;

    private  String localizationKey;

    private  Object [] localizedMessageParameters;

    private  String errorMessage;


    /**
     * Construct result object.
     * @param errorCode error code.
     * @param errorMessage message
     * @param localizationKey localization key
     * @param localizedMessageParameters message parameters to format message.
     */
    public Result(final String errorCode,
                  final String errorMessage,
                  final String localizationKey,
                  final Object ... localizedMessageParameters) {
        this.errorCode = errorCode;
        this.localizationKey = localizationKey;
        this.localizedMessageParameters = localizedMessageParameters;
        this.errorMessage = errorMessage;
    }

    /**
     * Construct result object.
     * @param errorCode error code.
     * @param errorMessage message
     * @param localizationKey localization key
     */
    public Result(final String errorCode,
                  final String errorMessage,
                  final String localizationKey
    ) {
        this.errorCode = errorCode;
        this.localizationKey = localizationKey;
        this.errorMessage = errorMessage;
        this.localizedMessageParameters = null;
    }


    /**
     * Construct result object.
     * @param errorCode error code.
     * @param errorMessage message
    */
    public Result(final String errorCode, final String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        localizationKey = null;
        localizedMessageParameters = null;
    }

    /**
     * Getr erro rocde.
     * @return error code.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Get error localizetion key.
     * @return error localization key
     */
    public String getLocalizationKey() {
        return localizationKey;
    }

    /**
     * Get localiztion mesage paraeters.
     * @return localization message parameters
     */
    public Object[] getLocalizedMessageParameters() {
        return localizedMessageParameters;
    }

    /**
     * Get non localized erro message.
     * @return non localized error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }


    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    public void setLocalizationKey(final String localizationKey) {
        this.localizationKey = localizationKey;
    }

    public void setLocalizedMessageParameters(final Object[] localizedMessageParameters) {
        this.localizedMessageParameters = localizedMessageParameters;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
