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

import org.yes.cart.domain.entity.Content;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;

import java.util.Comparator;

/**
 * User: denispavlov
 * Date: 20/04/2019
 * Time: 18:41
 */
public class ContentRankDisplayNameComparator implements Comparator<Content> {

    private final String lang;

    public ContentRankDisplayNameComparator(final String lang) {
        this.lang = lang;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Content cn1, final Content cn2) {
        int rank = Integer.compare(cn1.getRank(), cn2.getRank());
        if (rank == 0) {
            final String name1 = new FailoverStringI18NModel(cn1.getDisplayName(), cn1.getName()).getValue(lang);
            final String name2 = new FailoverStringI18NModel(cn2.getDisplayName(), cn2.getName()).getValue(lang);
            int name = name1 == null ? -1 : (name2 == null ? 1 : name1.toLowerCase().compareTo(name2.toLowerCase()));
            if (name == 0) {
                return Long.compare(cn1.getContentId(), cn2.getContentId());
            }
            return name;
        }
        return rank;
    }
}
