package org.yes.cart.icecat.transform

import org.yes.cart.icecat.transform.xml.CategoryHandler
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.InputSource
import org.yes.cart.icecat.transform.xml.ProductPointerHandler

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/7/12
 * Time: 10:11 PM
 */
class CategoryWalker {

    Context context;

    CategoryWalker(Context context) {

        this.context = context;

    }


    void collectData() {

        def FileInputStream refs = new FileInputStream("$context.dataDirectory/export/freexml.int/refs.xml");
        def handler = new CategoryHandler(context.categories, context.langId.toString())
        def reader = SAXParserFactory.newInstance().newSAXParser().XMLReader
        reader.setContentHandler(handler)
        reader.parse(new InputSource(refs))


        def indexis = new FileInputStream("$context.dataDirectory/export/freexml.int/$context.productDir/index.html");
        def productPointerHandler = new ProductPointerHandler(handler.categoryList);
        def productReadeReader = SAXParserFactory.newInstance().newSAXParser().XMLReader
        productReadeReader.setContentHandler(productPointerHandler)
        productReadeReader.parse(new InputSource(indexis))




        
        println(handler.categoryList.size());
        handler.categoryList.each {
            println(it);
        }

    }



}
