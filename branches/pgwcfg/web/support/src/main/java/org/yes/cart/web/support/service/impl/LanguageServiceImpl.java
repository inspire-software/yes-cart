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

package org.yes.cart.web.support.service.impl;

import org.yes.cart.web.support.service.LanguageService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/25/11
 * Time: 10:19 PM
 */
public class LanguageServiceImpl implements LanguageService {

    private final Map<String, String> languageName;

    private final List<String> supportedLanguages;


    /**
     * Construct language service.
     * @param languageName  map lang code - lang name
     */
    public LanguageServiceImpl(final Map<String, String> languageName) {
        this.languageName = new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        this.languageName.putAll(languageName);
        this.supportedLanguages = new ArrayList<String>(this.languageName.keySet());
    }

    /**
     * Get most appropriate  full language name.
     *
     * @param language to char language code.
     * @return language name
     */
    public String resolveLanguageName(final String language) {
        return languageName.get(language);
    }

    /**
     * Get supported languages.
     * @return  supported languages.
     */
    public Map<String, String> getLanguageName() {
        return languageName;
    }

    /**
     * Get supported languages list.
     * @return  supported languages list.
     */
    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }
}
