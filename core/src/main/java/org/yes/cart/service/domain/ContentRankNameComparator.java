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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Content;

import java.util.Comparator;

/**
 * User: denispavlov
 * Date: 20/04/2019
 * Time: 18:41
 */
public class ContentRankNameComparator implements Comparator<Content> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Content cn1, final Content cn2) {
        int rank = Integer.compare(cn1.getRank(), cn2.getRank());
        if (rank == 0) {
            final String name1 = cn1.getName();
            final String name2 = cn2.getName();
            int name = name1 == null ? -1 : (name2 == null ? 1 : name1.toLowerCase().compareTo(name2.toLowerCase()));
            if (name == 0) {
                return Long.compare(cn1.getContentId(), cn2.getContentId());
            }
            return name;
        }
        return rank;
    }
}
