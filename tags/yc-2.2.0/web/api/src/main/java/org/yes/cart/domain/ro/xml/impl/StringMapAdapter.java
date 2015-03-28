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
public class StringMapAdapter extends XmlAdapter<StringMapAdapter.AdaptedMap, Map<String, String>> {

    @XmlType(name = "string-map")
    public static class AdaptedMap {

        public List<Entry> entry = new ArrayList<Entry>();

    }

    @XmlType(name = "entry")
    public static class Entry {

        @XmlAttribute(name = "key")
        public String key;

        @XmlValue
        public String value;

    }

    @Override
    public Map<String, String> unmarshal(AdaptedMap adaptedMap) throws Exception {
        if (adaptedMap != null && adaptedMap.entry != null && !adaptedMap.entry.isEmpty()) {
            Map<String, String> map = new HashMap<String, String>();
            for(Entry entry : adaptedMap.entry) {
                map.put(entry.key, entry.value);
            }
            return map;
        }
        return null;
    }

    @Override
    public AdaptedMap marshal(Map<String, String> map) throws Exception {
        if (map != null) {
            AdaptedMap adaptedMap = new AdaptedMap();
            for(Map.Entry<String, String> mapEntry : map.entrySet()) {
                Entry entry = new Entry();
                entry.key = mapEntry.getKey();
                entry.value = mapEntry.getValue();
                adaptedMap.entry.add(entry);
            }
            return adaptedMap;
        }
        return null;
    }

}
