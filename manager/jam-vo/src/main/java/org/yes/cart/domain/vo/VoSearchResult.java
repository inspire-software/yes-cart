/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.domain.vo;

import org.yes.cart.domain.misc.SearchContext;

import java.util.List;

/**
 * User: denispavlov
 * Date: 02/11/2019
 * Time: 20:02
 */
public class VoSearchResult<T> {

    private VoSearchContext searchContext;
    private List<T> items;
    private int total;

    public VoSearchContext getSearchContext() {
        return searchContext;
    }

    public void setSearchContext(final VoSearchContext searchContext) {
        this.searchContext = searchContext;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(final List<T> items) {
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(final int total) {
        this.total = total;
    }

}
