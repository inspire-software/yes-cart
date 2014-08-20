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

package org.yes.cart.icecat.transform.csv

import org.yes.cart.icecat.transform.domain.Category
import org.yes.cart.icecat.transform.Util

/**
 * User: denispavlov
 * Date: 12-08-09
 * Time: 8:03 AM
 */
class CategoryCsvAdapter {

    Map<String, Category> categoryMap;

    public CategoryCsvAdapter(final Map<String, Category> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public toCsvFile(String filename) {

        StringBuilder builder = new StringBuilder();
        builder.append("parent category id;guid;category name;EN;RU;UK;description;EN;RU;UK;keywords;EN;RU;UK\n");
        categoryMap.values().each {
            builder.append('100;"')
            //builder.append(it.parentCategoryid).append(';"')
            builder.append(it.id).append('";"')
            builder.append(Util.escapeCSV(it.getNameFor('en'))).append('";"')
            builder.append(Util.escapeCSV(it.getNameFor('en'))).append('";"')
            builder.append(Util.escapeCSV(it.getNameFor('ru'))).append('";"')
            builder.append(Util.escapeCSV(it.getNameFor('uk'))).append('";"')
            builder.append(Util.escapeCSV(it.getDescriptionFor('en'))).append('";"')
            builder.append(Util.escapeCSV(it.getDescriptionFor('en'))).append('";"')
            builder.append(Util.escapeCSV(it.getDescriptionFor('ru'))).append('";"')
            builder.append(Util.escapeCSV(it.getDescriptionFor('uk'))).append('";"')
            builder.append(Util.escapeCSV(it.getKeywordsFor('en'))).append('";"')
            builder.append(Util.escapeCSV(it.getKeywordsFor('en'))).append('";"')
            builder.append(Util.escapeCSV(it.getKeywordsFor('ru'))).append('";"')
            builder.append(Util.escapeCSV(it.getKeywordsFor('uk'))).append('"\n')
        }
        new File(filename).write(builder.toString(), 'UTF-8');

    }

    /**
     *
     * @param categoryId
     * @param hierarhyRoot
     * @return       true in case if category belong to given root
     */
    private boolean allowOutput(String categoryId, String hierarhyRoot) {

        final Category cat = categoryMap.get(categoryId);
        if (cat != null) {
            if (cat.parentCategoryid == cat.id) { // root
                return false;
            }
            if (cat.parentCategoryid == hierarhyRoot) {
                return true;
            }
            return allowOutput(cat.parentCategoryid, hierarhyRoot);

        }
        return false;

    }


}
