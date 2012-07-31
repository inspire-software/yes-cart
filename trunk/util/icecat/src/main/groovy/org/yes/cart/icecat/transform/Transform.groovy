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


             println("Input login for $ctx.url ");
             //ctx.login = it.readLine();
             
             println("Input password for $ctx.url");
             //ctx.pwd = it.readLine();

             println("Input language id [$ctx.langId] (8 - RU, 9 - EN, see refs.xml for other ids)" );
             line = it.readLine();
             if (line != "") {
                 ctx.langId = line.toInteger()
             }

             println("Input product lang directory [$ctx.productDir]. This will be used to get index.html" );
             line = it.readLine();
             if (line != "") {
                 ctx.productDir = line;
             }


             println("Input comma separated category id list [$ctx.categories]");
             line = it.readLine();
             if (line != "") {
                 ctx.categories = line;
             }


             println("Input product updated min time [$ctx.mindata] ");
             line = it.readLine();
             if (line != "") {
                 ctx.mindata = line.toLong();
             }

             println("Input products per category limit [$ctx.productsPerCategoryLimit]");
             line = it.readLine();
             if (line != "") {
                 ctx.productsPerCategoryLimit  = line.toInteger();
             }

         }

         return ctx;
         
     }


}
