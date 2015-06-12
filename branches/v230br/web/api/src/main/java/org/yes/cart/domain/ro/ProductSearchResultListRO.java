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

package org.yes.cart.domain.ro;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * User: denispavlov
 * Date: 24/03/2015
 * Time: 15:30
 */
@XmlRootElement(name = "products")
public class ProductSearchResultListRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    private List<ProductSearchResultRO> products;

    public ProductSearchResultListRO() {
    }

    public ProductSearchResultListRO(final List<ProductSearchResultRO> products) {
        this.products = products;
    }

    @XmlElement(name = "product")
    public List<ProductSearchResultRO> getProducts() {
        return products;
    }

    public void setProducts(final List<ProductSearchResultRO> products) {
        this.products = products;
    }
}
