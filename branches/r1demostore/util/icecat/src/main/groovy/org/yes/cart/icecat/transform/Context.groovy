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
    
    String dataDirectory = "d:\\icecat";

    String url = "http://data.icecat.biz/";

    String login = "iazarny";

    String pwd = "sharpevil77";

    String langId = "9,8";

    String langNames = "en,ru";

    String productDir = "EN,RU";

    String categories = """474,1050,482,1199,1027,483,1034,1450,1103,1811,1035,1032,485,388,491,1030,1477,2272,1449,484,481,474,1926,477,1107,475,1033,493,480,486,1190,1031,1050,
453,1105,464,1468,471,2329,2336,1088,1473,460,1025,1472,457,455,459,472,1116,461,1397,2400,456,1048,1162,454,463,1049,
781,394,1790,2035,392,597,1029,1028,424,393,438,
428,426,2334,57,425,
2035,1192,2036,2035,401,2438,400,
438,473,450,448,449,2512,489,492,1198,1014,1496,1160,1111,445,2508,1821,1015,443,1159,980,1394,440,490,449,1822,438,447,495,451""";

    //long mindata =  20120101000000L;

    long mindata =  20110101000000L;

    int productsPerCategoryLimit = 1000;



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
