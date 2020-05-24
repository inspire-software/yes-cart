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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;

import java.util.Comparator;

/**
 * User: denispavlov
 * Date: 28/08/2015
 * Time: 08:42
 */
public class CategoryRankDisplayNameComparator implements Comparator<Category> {

    private final String lang;

    public CategoryRankDisplayNameComparator(final String lang) {
        this.lang = lang;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Category cat1, final Category cat2) {
        int rank = Integer.compare(cat1.getRank(), cat2.getRank());
        if (rank == 0) {
            final String name1 = new FailoverStringI18NModel(cat1.getDisplayName(), cat1.getName()).getValue(lang);
            final String name2 = new FailoverStringI18NModel(cat2.getDisplayName(), cat2.getName()).getValue(lang);
            int name = name1 == null ? -1 : (name2 == null ? 1 : name1.toLowerCase().compareTo(name2.toLowerCase()));
            if (name == 0) {
                return Long.compare(cat1.getCategoryId(), cat2.getCategoryId());
            }
            return name;
        }
        return rank;
    }
}
