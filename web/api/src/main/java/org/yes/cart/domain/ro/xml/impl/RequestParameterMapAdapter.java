/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.domain.ro.xml.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 20/08/2014
 * Time: 10:56
 */
public class RequestParameterMapAdapter extends XmlAdapter<RequestParameterMapAdapter.AdaptedMap, Map<String, List>> {

    @XmlType(name = "parameters")
    public static class AdaptedMap {

        public List<Entry> parameter = new ArrayList<Entry>();

    }

    @XmlType(name = "parameter")
    public static class Entry {

        @XmlAttribute(name = "key")
        public String key;

        @XmlValue
        public String value;

    }

    @Override
    public Map<String, List> unmarshal(AdaptedMap adaptedMap) throws Exception {
        if (adaptedMap != null && adaptedMap.parameter != null && !adaptedMap.parameter.isEmpty()) {
            Map<String, List> map = new HashMap<String, List>();
            for (Entry entry : adaptedMap.parameter) {
                if (!map.containsKey(entry.key)) {
                    map.put(entry.key, new ArrayList());
                }
                map.get(entry.key).add(entry.value);
            }
            return map;
        }
        return null;
    }

    @Override
    public AdaptedMap marshal(Map<String, List> map) throws Exception {
        if (map != null) {
            AdaptedMap adaptedMap = new AdaptedMap();
            for (Map.Entry<String, List> mapEntry : map.entrySet()) {
                for (final Object mapEntryItem : mapEntry.getValue()) {
                    if (mapEntryItem != null) {
                        Entry entry = new Entry();
                        entry.key = mapEntry.getKey();
                        entry.value = String.valueOf(mapEntryItem);
                        adaptedMap.parameter.add(entry);
                    }
                }
            }
            return adaptedMap;
        }
        return null;
    }

}
