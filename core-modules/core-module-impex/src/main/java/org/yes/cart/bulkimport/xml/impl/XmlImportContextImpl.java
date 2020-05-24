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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkimport.xml.XmlImportContext;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 11:24
 */
public class XmlImportContextImpl implements XmlImportContext {

    private String shopCode;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getImpExService() {
        return "XML";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShopCode() {
        return shopCode;
    }

    /**
     * Set shop specific context for this import.
     *
     * @param shopCode shop code
     */
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

}
