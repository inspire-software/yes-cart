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
public class CarrierSlaMapAdapter extends XmlAdapter<CarrierSlaMapAdapter.AdaptedMap, Map<String, Long>> {

    @XmlType(name = "carrier-sla")
    public static class AdaptedMap {

        public List<Entry> selection = new ArrayList<Entry>();

    }

    @XmlType(name = "carrier-sla-id")
    public static class Entry {

        @XmlAttribute(name = "supplier-code")
        public String key;

        @XmlValue
        public Long value;

    }

    @Override
    public Map<String, Long> unmarshal(AdaptedMap adaptedMap) throws Exception {
        if (adaptedMap != null && adaptedMap.selection != null && !adaptedMap.selection.isEmpty()) {
            Map<String, Long> map = new HashMap<String, Long>();
            for(Entry entry : adaptedMap.selection) {
                map.put(entry.key, entry.value);
            }
            return map;
        }
        return null;
    }

    @Override
    public AdaptedMap marshal(Map<String, Long> map) throws Exception {
        if (map != null) {
            AdaptedMap adaptedMap = new AdaptedMap();
            for(Map.Entry<String, Long> mapEntry : map.entrySet()) {
                Entry entry = new Entry();
                entry.key = mapEntry.getKey();
                entry.value = mapEntry.getValue();
                adaptedMap.selection.add(entry);
            }
            return adaptedMap;
        }
        return null;
    }

}
