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

package org.yes.cart.domain.query.impl;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 * 
 */
public class SearchPhrazeUtil {

    private final static Pattern splitPattern = Pattern.compile("\\s+|,+|;+|-+|\\++|\\.+|\\|+");

    /**
     * Tokenaze search phraze and clean from empty strings.
     * @param phraze optional phraze
     * @return list of tokens, that found in phraze.
     */
    public static List<String> splitForSearch(final String phraze) {
        if (phraze != null) {
            String [] token = splitPattern.split(phraze);
            List<String> words = new ArrayList<String>(token.length);
            for (int i = 0; i < token.length ; i++) {
                if (StringUtils.isNotBlank(token[i])) {
                    words.add(token[i].trim());
                }
            }
            return words;
        }
        return Collections.EMPTY_LIST;
    }


}
