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

package org.yes.cart.service.order;

/**
 * User: denispavlov
 * Date: 02/09/2016
 * Time: 08:56
 */
public class PGDisabledException extends OrderException {

    private final String pgLabel;

    public PGDisabledException(final String message, final Throwable cause, final String pgLabel) {
        super(message, cause);
        this.pgLabel = pgLabel;
    }

    public PGDisabledException(final String message, final String pgLabel) {
        super(message);
        this.pgLabel = pgLabel;
    }

    public String getPgLabel() {
        return pgLabel;
    }
}
