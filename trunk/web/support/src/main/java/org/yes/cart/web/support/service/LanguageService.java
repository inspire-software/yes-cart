package org.yes.cart.web.support.service;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/25/11
 * Time: 10:19 PM
 */
public interface  LanguageService {

     /**
     * Get most appropriate  full language name.
     *
     * @param language to char language code.
     * @return language name
     */
    String resolveLanguageName(String language);

    /**
     * Get supported languages.
     * @return  supported languages.
     */
    Map<String, String> getLanguageName();

     /**
     * Get supported languages list.
     * @return  supported languages list.
     */
    List<String> getSupportedLanguages();

}
