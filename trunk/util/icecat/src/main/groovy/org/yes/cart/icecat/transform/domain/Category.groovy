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

/**
 * 
 * User: igora Igor Azarny
 * Date: 5/9/12
 * Time: 2:56 PM
 * 
 */
class Category {

    String id;
    String lowPic;
    String score;
    String searchable;
    String thumbPic
    String UNCATID;
    String visible;

    String description;
    String keywords;
    String name;
    
    List<ProductPointer> productPointer = new ArrayList<ProductPointer>();
    List<Product> product = new ArrayList<Product>();

    List<CategoryFeatureGroup> categoryFeatureGroup = new ArrayList<CategoryFeatureGroup>();

    public String toProductType() { //TPRODTYPE  and TCATEGORY
        //100 default root category
        return "100;" + (name == null ? id : name) + ";" + description + "\n";
    }



    public String toProductTypeAttr() {       //TPRODUCTTYPEATTR & TATTRIBUTE
        StringBuilder builder = new StringBuilder();
        for (CategoryFeatureGroup cfg : categoryFeatureGroup) {

            for(Feature feature : cfg.featureList) {

                final String attributeName = feature.Name.replace(";", " ").replace('"', "\\\"").replace(',', " ");

                builder.append(name == null ? id : name)
                builder.append(";")
                builder.append(attributeName);
                builder.append(";");
                builder.append(feature.Mandatory);
                builder.append(";");
                builder.append(feature.Searchable);
                builder.append(";");
                builder.append(  ((name == null ? id : name) + feature.Name).hashCode() );
                builder.append(";");

                if (isFilteredNavigation(attributeName)) {
                    builder.append("true");
                }   else {
                    builder.append("false");
                }
                builder.append(";");


                if (isRangeNavigation(attributeName)) {
                    builder.append("R");
                } else {
                    builder.append("S");
                }
                builder.append(";");

                if (isRangeNavigation(attributeName)) {
                    builder.append(getRangeNavigationXml(attributeName));
                }   else {
                    builder.append("\"\"");

                }
                builder.append(";");



                builder.append("\n")
            }

        }
        return builder.toString();
    }

    def navigableMap = [
            "Дисплей" : 120,
            "Диагональ экрана" : 100,
            "Разрешение экрана" : 110,
            "Вес" : 200,
            "Емкость батареи" : 250,
            "Емкость жесткого диска" : 60,
            "Оперативная память" : 50,
            "Семейство процессоров" : 10,
            "Тактовая частота процессора" : 20,
            "Тип оптического привода" : 300,
            "Цвет продукта" : 400
    ];

    private boolean isFilteredNavigation(String name) {
        return navigableMap.get(name) != null;
    }



    private boolean isRangeNavigation(String name) {
        return  "Вес".equals(name) ||   "Weight".equals(name)

    }

    private String getRangeNavigationXml(String name) {
        if ("Вес".equals(name) ) {
            return '<rangeList serialization="custom"><unserializable-parents/><list><default><size>8</size></default><int>8</int><range><range><first class="string">1200 г</first><second class="string">1500 г</second></range></range><range><range><first class="string">1500 г</first><second class="string">1800 г</second></range></range><range><range><first class="string">1800 г</first><second class="string">2000 г</second></range></range><range><range><first class="string">2000 г</first><second class="string">2500 г</second></range></range><range><range><first class="string">2500 г</first><second class="string">3000 г</second></range></range><range><range><first class="string">3000 г</first><second class="string">3500 г</second></range></range><range><range><first class="string">3500 г</first><second class="string">4000 г</second></range></range><range><range><first class="string">4000 г</first><second class="string">5000 г</second></range></range></list></rangeList>'
        }   else if ("Weight".equals(name)) {
           return  ''
        }
        return ''

    }


    public String toArrtViewGroup() { //TATTRVIEWGROUP
        StringBuilder builder = new StringBuilder();
        for (CategoryFeatureGroup cfg : categoryFeatureGroup) {
            builder.append(cfg.Name)
            builder.append(';')
            builder.append(cfg.ID)
            builder.append(';')
            builder.append(cfg.Name)
            builder.append(';')
            builder.append((((name == null ? id : name)) + cfg.Name).hashCode())
            builder.append("\n")

        }
        return builder.toString();
        
        
    }

    public String toProductTypeAttrViewGroup() {       //TPRODTYPEATTRVIEWGROUP
        StringBuilder builder = new StringBuilder();
        for (CategoryFeatureGroup cfg : categoryFeatureGroup) {
            builder.append((name == null ? id : name))
            builder.append(";")
            builder.append(cfg.Name)
            builder.append(";")
            for(Feature feature : cfg.featureList) {
                builder.append(feature.Name);
                builder.append(",");
            }
            builder.append(";")
            builder.append((((name == null ? id : name)) + cfg.Name).hashCode())
            builder.append(";")

            builder.append("\n")
        }
        return builder.toString();
        
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
