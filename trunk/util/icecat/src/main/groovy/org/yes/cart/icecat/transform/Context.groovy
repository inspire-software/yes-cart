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
    
    String dataDirectory = "/development/projects/java/yc/env/sampledata/demo-data/icecat";

    String url = "http://data.icecat.biz/";

    String login = "denis.v.pavlov";

    String pwd = "d3n1\$PaV098";

    int langId = 8;

    String productDir = "RU";

    String categories = "151,1296,942,803,788,195,194,197,943,196,191,192";

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
