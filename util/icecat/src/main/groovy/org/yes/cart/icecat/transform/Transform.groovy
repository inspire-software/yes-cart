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

         try {

             CategoryWalker categoryWalker = new CategoryWalker(ctx);

             categoryWalker.collectData();

         } catch (e) {
             println e.getMessage()
             e.printStackTrace();
         }
         

     }
     
     Context fillContext () {
         
         Context ctx = new Context();

         System.in.withReader {

             def line;

             println("Input data directory [default: $ctx.dataDirectory]  ")
             line = it.readLine();
             if (line != "") {
                 ctx.dataDirectory = line
             }
             println("data directory: $ctx.dataDirectory");

             println("Input http url [default: $ctx.url]  ");
             line = it.readLine();
             if (line != "") {
                 ctx.url = line;
             }
             println("http url: $ctx.url");

             //println("Input login for $ctx.url ");
             //ctx.login = it.readLine();
             
             //println("Input password for $ctx.url");
             //ctx.pwd = it.readLine();

             println("Input language id [default: $ctx.langId] (8 - RU, 9 - EN, 25 - UK, see refs.xml for other ids)" );
             line = it.readLine();
             if (line != "") {
                 ctx.langId = line
             }
             ctx.langId += ",1"  // default language for blank values fallback
             println("language id: $ctx.langId");

             println("Input language names [default: $ctx.langNames] (ru,en,uk exactly as they are mapped in storefront locale)" );
             line = it.readLine();
             if (line != "") {
                 ctx.langNames = line
             }
             ctx.langNames += ",def" // default language for blank values fallback
             println("language names: $ctx.langNames");

             println("Input product lang directory [default: $ctx.productDir]. This will be used to get index.html last one is for default" );
             line = it.readLine();
             if (line != "") {
                 ctx.productDir = line;
             }
             println("product lang directory: $ctx.productDir" );

             println("Input comma separated category id list [default: $ctx.categories]");
             line = it.readLine();
             if (line != "") {
                 ctx.categories = line;
             }
             println("comma separated category id list: $ctx.categories");

             println("Input product updated min time [default: $ctx.mindata] ");
             line = it.readLine();
             if (line != "") {
                 ctx.mindata = line.toLong();
             }
             println("product updated min time: $ctx.mindata");

             println("Input products per category limit [default: $ctx.productsPerCategoryLimit]");
             line = it.readLine();
             if (line != "") {
                 ctx.productsPerCategoryLimit  = line.toInteger();
             }
             println("products per category limit: $ctx.productsPerCategoryLimit");

         }

         return ctx;
         
     }


}
