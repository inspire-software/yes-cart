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

package org.yes.cart.exception;

/**
 * Exception thrown by  DtoFactory when an unmapped object is requested.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 11:36:29 PM
 */
public class UnmappedInterfaceException extends Exception {

    private static final long serialVersionUID = 20100122L;

    /**
     * Default constructor.
     */
    public UnmappedInterfaceException(final String message) {
        super(message);
    }
}
