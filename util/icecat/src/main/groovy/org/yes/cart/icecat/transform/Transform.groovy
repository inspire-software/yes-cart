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


             println("Input comma separated category id list, for example 151,897,1561,1285,182,814");
             ctx.categories = it.readLine();

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
