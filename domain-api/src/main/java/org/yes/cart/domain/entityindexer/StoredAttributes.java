package org.yes.cart.domain.entityindexer;

import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 04/11/2015
 * Time: 17:43
 */
public interface StoredAttributes {

    /**
     * @param code attribute code
     * @return string value and display value for that code
     */
    Pair<String, I18NModel> getValue(String code);

    /**
     * @param code attribute code
     * @param value string value for that code
     * @param displayValue display value object {@link I18NModel}
     */
    void putValue(String code, String value, Object displayValue);

    /**
     * @return all values mapped to codes
     */
    Map<String, Pair<String, I18NModel>> getAllValues();

}
