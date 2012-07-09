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
