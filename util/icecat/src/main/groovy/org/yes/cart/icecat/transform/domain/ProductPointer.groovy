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
 * Date: 5/8/12
 * Time: 4:22 PM
 * 
 */
class ProductPointer {

    Map<String, String> path = new HashMap<String, String>();
    String Product_ID;
    String Product_ID_valid;
    String Updated;
    String Quality;
    String Supplier_id;
    String Prod_ID;
    String On_Market;
    String Model_Name;
    String Product_View;
    String HighPic;
    String HighPicSize;
    String HighPicWidth;
    String HighPicHeight;
    String Date_Added;

    Product product;

    Map<String, BigDecimal> inventory = new HashMap<String, BigDecimal>();
    Map<String, Map<String, BigDecimal>> prices = new HashMap<String, Map<String, BigDecimal>>();


    Map<String, Category> categories = new HashMap<String, Category>();

    @Override
    public String toString() {
        return "    ProductPointer{" +
                "path='" + path + '\'' +
                ", Product_ID='" + Product_ID + '\'' +
                ", Product_ID_valid='" + Product_ID_valid + '\'' +
                ", Updated='" + Updated + '\'' +
                ", Quality='" + Quality + '\'' +
                ", Supplier_id='" + Supplier_id + '\'' +
                ", Prod_ID='" + Prod_ID + '\'' +
                ", On_Market='" + On_Market + '\'' +
                ", Model_Name='" + Model_Name + '\'' +
                ", Product_View='" + Product_View + '\'' +
                ", HighPic='" + HighPic + '\'' +
                ", HighPicSize='" + HighPicSize + '\'' +
                ", HighPicWidth='" + HighPicWidth + '\'' +
                ", HighPicHeight='" + HighPicHeight + '\'' +
                ", Date_Added='" + Date_Added + '\'' +
                '}';
    }


}
