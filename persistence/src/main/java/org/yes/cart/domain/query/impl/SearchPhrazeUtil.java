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

package org.yes.cart.domain.query.impl;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 * 
 */
public class SearchPhrazeUtil {

    /**
     * Tokenize search phraze and clean from empty strings.
     *
     * @param phraze optional phraze
     * @param charThreshold character threshold. e.g. if set to 3 will skip words less than 3 characters
     *
     * @return list of tokens, that found in phraze.
     */
    public static List<String> splitForSearch(final String phraze, final int charThreshold) {
        if (phraze != null) {
            String [] token = StringUtils.splitPreserveAllTokens(phraze, "| ;,.");
            List<String> words = new ArrayList<String>(token.length);
            for (final String aToken : token) {
                if (StringUtils.isNotBlank(aToken)) {
                    final String clean = aToken.trim();
                    if (clean.length() >= charThreshold) {
                        words.add(clean);
                    }
                }
            }
            return words;
        }
        return Collections.EMPTY_LIST;
    }


}
