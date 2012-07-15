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
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/7/12
 * Time: 10:16 PM
 */
 class Transform {

     static void main(String [] args) {

         Transform transform = new Transform();

         Context ctx = transform.fillContext();

         println ctx;

         CategoryWalker categoryWalker = new CategoryWalker(ctx);

         categoryWalker.collectData();
         

     }
     
     Context fillContext () {
         
         Context ctx = new Context();

         System.in.withReader {

             def line;

             println("Input data directory [$ctx.dataDirectory]  ")
             line = it.readLine();
             if (line != "") {
                 ctx.dataDirectory = line
             }


             println("Input http url [$ctx.url]  ");
             line = it.readLine();
             if (line != "") {
                 ctx.url = line;
             }


             println("Input login  ");
             ctx.login = it.readLine();
             
             println("Input password");
             ctx.pwd = it.readLine();

             println("Input language id [$ctx.langId]" );
             line = it.readLine();
             if (line != "") {
                 ctx.langId = line.toInteger()
             }

             println("Input product directory [$ctx.productDir]" );
             line = it.readLine();
             if (line != "") {
                 ctx.productDir = line;
             }


             println("Input comma separated category id list, for example [151,1296,942,803,788,195,194,197,943,196,191,192],1523,152,1662,575,244,1823,243,1347,1563,577,977,203,1754,1284,304,1285,932,1081,836,1514,1513,1592,32,1557,584,234,1554,262,211,1910,822,829,1372,826,966,56,827,214,869,219,2315,921,12,922,1689,1501,1840,175,367,62,1545,1307,221,1158,222,223,814,858,911,1020,1399,182,1211,1212,1637,909,258,567,902");
             line = it.readLine();
             if (line != "") {
                 ctx.categories = line.toLong();
             }


             println("Input product updated limit [$ctx.mindata] ");
             line = it.readLine();
             if (line != "") {
                 ctx.mindata = line.toLong();
             }

             println("Input products in category limit [$ctx.limit]");
             line = it.readLine();
             if (line != "") {
                 ctx.limit  = line.toInteger();
             }

         }

         return ctx;
         
     }


}
