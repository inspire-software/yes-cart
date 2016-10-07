/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.domain.vo;

import org.yes.cart.domain.misc.MutablePair;

import java.util.*;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 15:02
 */
public class VoUtils {

    private VoUtils() {
    }


    public static List<MutablePair<String, String>> adaptMapToPairs(Map<String, String> map) {
        final Set<Map.Entry<String, String>> es = map != null ? map.entrySet() : Collections.EMPTY_SET;
        List<MutablePair<String, String>> rez = new ArrayList<MutablePair<String, String>>(es.size());
        for (Map.Entry ent : es) {
            rez.add(MutablePair.of(ent.getKey(), ent.getValue()));
        }
        return rez;
    }

    public static Map<String, String> adaptPairsToMap(List<MutablePair<String, String>> pairs) {
        Map<String, String> map = new HashMap<>(pairs != null ? pairs.size() : 0);
        if (pairs != null) {
            for (MutablePair<String, String> pair : pairs) {
                map.put(pair.getFirst(), pair.getSecond());
            }
        }
        return map;
    }


}
