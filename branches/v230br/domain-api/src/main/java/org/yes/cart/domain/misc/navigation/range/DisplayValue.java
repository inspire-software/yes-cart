package org.yes.cart.domain.misc.navigation.range;

import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 23/04/2014
 * Time: 15:40
 */
public interface DisplayValue extends Serializable {

    /**
     * @return language
     */
    String getLang();

    /**
     * @param lang language
     */
    void setLang(String lang);

    /**
     * @return value
     */
    String getValue();

    /**
     * @param value value
     */
    void setValue(String value);

}
