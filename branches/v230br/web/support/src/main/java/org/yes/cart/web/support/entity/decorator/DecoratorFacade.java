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

package org.yes.cart.web.support.entity.decorator;

/**
 * Simple facae for creating entity decorators for Web.
 * User: denispavlov
 * Date: 12-08-20
 * Time: 5:23 PM
 */
public interface DecoratorFacade {

    /**
     * Get decorator object for storefront.
     *
     * @param object decoratable object
     * @param servletContextPath path
     * @param withAttributes true if decorator should load attributes
     *
     * @return decorated object
     */
    <T extends ObjectDecorator> T decorate(Object object,
                                           String servletContextPath,
                                           final boolean withAttributes);

}
