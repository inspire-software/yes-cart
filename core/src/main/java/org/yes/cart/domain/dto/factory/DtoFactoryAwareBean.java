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

package org.yes.cart.domain.dto.factory;

/**
 * Interface to enable DTO bean factory injection to the complex DTO's
 * in order to undertake complex nested DTO creation.
 * <p/>
 * User: dogma
 * Date: Jan 23, 2011
 * Time: 12:08:16 PM
 */
public interface DtoFactoryAwareBean {

    /**
     * @return DTO Factory that known the interface to class mappings.
     */
    DtoFactory getFactory();

}
