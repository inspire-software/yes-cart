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
    
    String dataDirectory = "c:\\icecat";

    String url = "http://data.icecat.biz/";

    String login = "iazarny";

    String pwd = "sharpevil77";

    String langId = "9,8";

    String langNames = "en,ru";

    String productDir = "EN,RU";

    String categories = "151,1296,942,803,788,195,194,197,943,196,191,192,378,153,381,932,156,375,1373,1573,897";
    //

    int limitPerBrand = 50;

//канцелярия
/*
    String categories = """1013,1130,1134,1135,1144,1149,1150,1154,1350,1351,1352,1815,1816,996,1226,1227,1054,1206,1207,1209,1224,1225,1229,1231,1276,1381,1396,1461,1605,1843,1849,1826,1827,1828,1829,
467,469,1940,1279,1368,1844,1850,2048,2378,1192,2036,2438,400,401,1017,1036,1075,1094,1186,1187,1228,1232,1751,1752,1754,1825,1835,1924,1925,2483,2502,306,307,
316,325,353,355,360,363,367,390,403,415,431,453,474,854,907,979,2338,1294,1834,1023,1063,1242,356,1022,322,323,1028,1029,1790,2035,392,393,394,424,438,1106,1109,
1131,1178,1193,1194,1221,1312,1367,1393,1458,1476,1480,1483,1484,1485,404,405,406,407,413,1069,1101,1195,1196,1197,1456,1840,1922,2335,416,419,422,423,2334,425,
426,428,1053,1147,1185,2424,432,433,434,436,446,452,982,1014,1015,1111,1159,1160,1198,1394,1496,1821,1822,2508,2512,440,443,445,447,448,449,450,451,473,489,490,
492,494,495,980,1025,1048,1049,1088,1105,1116,1162,1397,1468,1472,1473,2329,2336,2400,454,455,456,457,459,460,461,463,464,471,472,1027,1030,1031,1032,1033,1034,
1035,1050,1103,1107,1190,1199,1449,1450,1477,1811,1926,2272,388,475,477,480,481,482,483,484,485,486,491,493,1052,1070,1113,1114,1218,1447,1474,1478,1482,704,705,706,708,709,710,2339""";
*/

    //long mindata =  20120101000000L;

    long mindata =  20120101000000L;

    int productsPerCategoryLimit = 10;



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
