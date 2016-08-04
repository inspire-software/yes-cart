package org.yes.cart.domain.vo.converter;

import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 17:44
 */
public class Converters {

    public static final Map<String, Object> BASIC = Collections.unmodifiableMap(
            new HashMap<String, Object>() {{
                put("DisplayValues", new DisplayValueMapToListMutablePairConverter());
                put("CSVToList", new CSVToListConverter());
            }}
    );

}
