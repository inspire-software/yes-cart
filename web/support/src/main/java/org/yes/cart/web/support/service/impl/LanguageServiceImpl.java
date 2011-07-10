package org.yes.cart.web.support.service.impl;

import org.yes.cart.web.support.service.LanguageService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/25/11
 * Time: 10:19 PM
 */
public class LanguageServiceImpl implements LanguageService {

    private final Map<String, String> languageName;


    /**
     * Construct language service.
     * @param languageName  map lang code - lang name
     */
    public LanguageServiceImpl(final Map<String, String> languageName) {
        this.languageName = languageName;
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
}
