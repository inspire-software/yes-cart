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

package org.yes.cart.remote.service.impl;

import org.yes.cart.remote.service.RemoteLanguageService;
import org.yes.cart.service.misc.LanguageService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 08/10/2014
 * Time: 20:25
 */
public class RemoteLanguageServiceImpl implements RemoteLanguageService {

    private final LanguageService languageService;

    public RemoteLanguageServiceImpl(final LanguageService languageService) {
        this.languageService = languageService;
    }

    /** {@inheritDoc} */
    public Map<String, String> getSupportedLanguages() {
        final Map<String, String> langs = new HashMap<String, String>();
        langs.putAll(languageService.getLanguageName());
        return langs;
    }
}
