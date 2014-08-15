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





package org.yes.cart.icecat.transform.domain

import org.yes.cart.icecat.transform.Util

/**
 * 
 * User: igora Igor Azarny
 * Date: 5/9/12
 * Time: 2:56 PM
 * 
 */
class Category {

    static Map<String, String> URI_MAP = new HashMap<String, String>() {{
        put("151", "notebooks");
        put("195", "mice");
        put("194", "keyboards");
        put("153", "pc");
        put("932", "data-storage");
        put("156", "servers");
        put("897", "tablet-pc");
    }};

    String parentCategoryid;
    String id;
    String lowPic;
    String score;
    String searchable;
    String thumbPic
    String UNCATID;
    String visible;

    Map<String, String> description = new TreeMap<String, String>();
    Map<String, String> keywords = new TreeMap<String, String>();
    Map<String, String> name = new TreeMap<String, String>();

    List<ProductPointer> productPointer = new ArrayList<ProductPointer>();
    Map<String, Product> product = new TreeMap<String, Product>();

    List<CategoryFeatureGroup> categoryFeatureGroup = new ArrayList<CategoryFeatureGroup>();

    public String getURI() {
        String uri = URI_MAP.get(id);
        if (uri == null) {
            return id;
        }
        return uri;
    }

    public String getNameFor(String lang) {
        def name = Util.getLocalisedValue(this, "name", lang);
        if (name == '') {
            return id;
        }
        return name;
    }

    public String getDescriptionFor(String lang) {
        return Util.getLocalisedValue(this, "description", lang);
    }

    public String getKeywordsFor(String lang) {
        return Util.getLocalisedValue(this, "keywords", lang);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", lowPic='" + lowPic + '\'' +
                ", score='" + score + '\'' +
                ", searchable='" + searchable + '\'' +
                ", thumbPic='" + thumbPic + '\'' +
                ", UNCATID='" + UNCATID + '\'' +
                ", visible='" + visible + '\'' +
                ", description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", name='" + name + '\'' +
                ", productPointer  ='" + productPointer.size() + '\'' +
                ", categoryFeatureGroup  ='" + categoryFeatureGroup.size() + '\'' +
                '}';
    }


}
