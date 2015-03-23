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

package org.yes.cart.domain.ro;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * User: denispavlov
 * Date: 20/08/2014
 * Time: 00:26
 */
@XmlRootElement(name = "categories")
public class CategoryListRO {

    private List<CategoryRO> categories;

    public CategoryListRO() {

    }

    public CategoryListRO(final List<CategoryRO> categories) {
        this.categories = categories;
    }

    @XmlElement(name = "category")
    public List<CategoryRO> getCategories() {
        return categories;
    }

    public void setCategories(final List<CategoryRO> categories) {
        this.categories = categories;
    }
}
