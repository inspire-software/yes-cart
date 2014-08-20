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





package org.yes.cart.icecat.transform

/**
 * Context Groovy class.
 */
class Context  {
    
    String dataDirectory = "/development/projects/java/yc-v200br/env/sampledata/demo-data/icecat";

    String url = "http://data.icecat.biz/";

    String login = "iazarny";

    String pwd = "sharpevil77";

    //  9 - EN, 8 - RU, 25 - UK, see refs.xml for other ids - seems like icecat's UK products have no equivalents in RU or EN
    // we get better localization results
    String langId = "25,8,9";

    String langNames = "uk,ru,en";

    String productDir = "UK,RU,EN";

    //String categories = "151,1296,942,803,788,195,194,197,943,196,191,192,378,153,381,932,156,375,1373,897";
    String categories = "151,195,194,153,932,156,897";
    /*
    * 151 - laptop
    * 195 - mouse
    * 194 - keyboard
    * 197 - scanners
    * 153 - PC
    * 932 - data storage
    * 156 - servers
    * 897 - tablet pc
    *
    *
    *
    *
    *
    *
    * */

    int limitPerBrand = 50;

    long mindata =  20130101000000L;

    // We need to have sync between all langs in IceCat, so some entries will only be partial and cleaned up
    // So this 600 is not going to be 600 per category in the end after clean up but it prevents scanning full
    // IceCat catalog with data size in TB
    int productsPerCategoryLimit = 600;



    @Override
    public String toString() {
        return "Context{" +
                "dataDirectory='" + dataDirectory + '\'' +
                ", url='" + url + '\'' +
                ", login='" + login + '\'' +
                ", pwd='" + pwd + '\'' +
                ", langId=" + langId +
                ", categories='" + categories + '\'' +
                ", mindata=" + mindata +
                ", productsPerCategoryLimit=" + productsPerCategoryLimit +
                '}';
    }


}
