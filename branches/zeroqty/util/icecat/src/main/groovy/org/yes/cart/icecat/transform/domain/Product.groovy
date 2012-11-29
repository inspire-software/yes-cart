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
 * Time: 4:08 PM
 *
 */
class Product {

    String Code;
    String HighPic;
    String HighPicHeight;
    String HighPicSize;
    String HighPicWidth;
    String ID;
    String LowPic;
    String LowPicHeight;
    String LowPicSize;
    String LowPicWidth;
    Map<String, String> Name = new HashMap<String, String>();
    String Pic500x500;
    String Pic500x500Height;
    String Pic500x500Size;
    String Pic500x500Width;
    String Prod_id;
    String Quality;
    String ReleaseDate;
    String ThumbPic;
    String ThumbPicSize;
    Map<String, String> Title = new HashMap<String, String>();

    String CategoryID;
    String CategoryName;

    String EANCode;

    Map<String, ProductFeature> productFeatures = new HashMap<String, ProductFeature>();
    List<String> relatedProduct = new ArrayList<String>();
    Set<String> relatedCategories = new HashSet<String>();


    Map<String, String> ShortSummaryDescription = new HashMap<String, String>();
    Map<String, String> LongSummaryDescription = new HashMap<String, String>();
    List<String> productPicture = new ArrayList<String>();

    String Supplier;

    public String getNameFor(String lang) {
        return Util.getLocalisedValue(this, "Name", lang);
    }

    public String getTitleFor(String lang) {
        return Util.getLocalisedValue(this, "Title", lang);
    }

    public String getShortSummaryDescriptionFor(String lang) {
        return Util.getLocalisedValue(this, "ShortSummaryDescription", lang);
    }

    public String getLongSummaryDescriptionFor(String lang) {
        return Util.getLocalisedValue(this, "LongSummaryDescription", lang);
    }

    public String toString() {
        StringBuilder prodFeature = new StringBuilder();
     /*   for (ProductFeature pf: productFeatures) {
            prodFeature.append(pf.feature.name.replace(";", " ").replace('"', "\\\"").replace(',', " "));
            prodFeature.append("->");
            prodFeature.append(pf.Presentation_Value.replace(";", " ").replace('"', "\\\"").replace(',', " ") ) ;
            prodFeature.append(',');
        }



        return (Title + ";"
                + CategoryName + ";"   // category
                + CategoryName + ";" // type
                + Supplier + ";" // brand
                + "std" + ";"
                + Prod_id + ";" //sku code
                + "100" + ";" //qty on warehouse
                + new BigDecimal(500f + new Random().nextFloat() * 3000).setScale(2, BigDecimal.ROUND_HALF_UP) + ";" //price
                + EANCode + ";"
                + prodFeature + ";"
                + LongSummaryDescription.values().toString()
                + "\n"
        );   */
        return ID;

    }


}
