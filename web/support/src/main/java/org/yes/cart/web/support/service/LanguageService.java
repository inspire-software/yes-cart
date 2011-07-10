package org.yes.cart.web.support.service;

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

}
