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

package org.yes.cart.bulkimport.csv.impl;

import org.yes.cart.bulkimport.model.ImportContext;

/**
 * User: denispavlov
 * Date: 11/06/2015
 * Time: 07:59
 */
public class CsvImportContextImpl implements ImportContext {

    private String shopCode;
    private String shopCodeColumn;

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public String getShopCodeColumn() {
        return shopCodeColumn;
    }

    /**
     * Set column name in this descriptor that contains shopCode string value.
     *
     * @param shopCodeColumn index
     */
    public void setShopCodeColumn(final String shopCodeColumn) {
        this.shopCodeColumn = shopCodeColumn;
    }

}
