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





package org.yes.cart.icecat.transform.xml

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import org.yes.cart.icecat.transform.Util
import org.yes.cart.icecat.transform.domain.*

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/9/12
 * Time: 9:23 PM
 */
class ProductHandler extends DefaultHandler {

    Product product = null;
    Map<String, Category> categoryMap;
    Map<String, Feature> featureMap;
    ProductPointer productPointer;

    boolean inProductRelated = false

    String lang;
    String langid;

    ProductHandler(Map<String, Category> categoryMap, Map<String, Feature> featureMap, ProductPointer productPointer,
            String lang, String langid) {
        this.categoryMap = categoryMap
        this.featureMap = featureMap
        this.productPointer = productPointer
        this.lang = lang
        this.langid = langid
    }

    void startElement(String uri, String localName, String qName, Attributes attributes) {

        if ("ProductRelated" == qName) {
            inProductRelated = true
            String catId = attributes.getValue("Category_ID");
            if (this.categoryMap.containsKey(catId)) {
                product.relatedCategories.add(catId)
            }

        }

        if ("Product" == qName && inProductRelated) {
            product.relatedProduct.add(attributes.getValue("ID"))

        }

        if ("Product" == qName && product == null && !inProductRelated) {

            if (productPointer.product == null) {
                println "Adding product instance to pp in $lang: $productPointer.Product_ID ($productPointer.Product_ID_valid)"
                product = new Product();
                productPointer.product = product;
            } else {
                println "Reusing product instance of pp for $lang: $productPointer.Product_ID ($productPointer.Product_ID_valid)"
                product = productPointer.product;
            }

            product.Code = Util.maxLength(attributes.getValue("Code"), 255);
            product.HighPic = attributes.getValue("HighPic");
            product.HighPicHeight = attributes.getValue("HighPicHeight");
            product.HighPicSize = attributes.getValue("HighPicSize");
            product.HighPicWidth = attributes.getValue("HighPicWidth");
            product.ID = attributes.getValue("ID");
            product.LowPic = attributes.getValue("LowPic");
            product.LowPicHeight = attributes.getValue("LowPicHeight");
            product.LowPicSize = attributes.getValue("LowPicSize");
            product.LowPicWidth = attributes.getValue("LowPicWidth");
            Util.setLocalisedValue(product, 'Name', langid, attributes.getValue("Name"), 255, [langid], [lang]);
            product.Pic500x500 = attributes.getValue("Pic500x500");
            product.Pic500x500Height = attributes.getValue("Pic500x500Height");
            product.Pic500x500Size = attributes.getValue("Pic500x500Size");
            product.Pic500x500Width = attributes.getValue("Pic500x500Width");
            product.Prod_id = Util.normalize(attributes.getValue("Prod_id")); //sku code
            product.Quality = attributes.getValue("Quality");
            product.ReleaseDate = attributes.getValue("ReleaseDate");
            product.ThumbPic = attributes.getValue("ThumbPic");
            product.ThumbPicSize = attributes.getValue("ThumbPicSize");
            Util.setLocalisedValue(product, 'Title', langid, attributes.getValue("Title"), 255, [langid], [lang]);

        }

        if ("Category" == qName) {
            product.CategoryID = attributes.getValue("ID");
        }

        if ("EANCode" == qName) {
            product.EANCode = attributes.getValue("EAN");
        }

        if ("ShortSummaryDescription" == qName) {
            readyToGetShortSummaryDescription = true;
        }

        if ("LongSummaryDescription" == qName) {
            readyToGetLongSummaryDescription = true;
        }

        if ("Supplier" == qName) {
            product.Supplier = attributes.getValue("Name");
        }

        if ("ProductPicture" == qName) {
            product.productPicture.add(attributes.getValue("Pic"));
        }

        if ("ProductFeature" == qName) {
            productFeature = new ProductFeature();
            productFeature.Value = attributes.getValue("Value")
            Util.setLocalisedValue(productFeature, 'PresentationValue',
                    langid, attributes.getValue("Presentation_Value"), 255, [langid], [lang]);
        }

        if ("Feature" == qName) {
            String featureId = attributes.getValue("ID");
            feature = featureMap.get(featureId);
            productFeature.feature = feature
        }


    }



    ProductFeature productFeature
    Feature feature

    boolean readyToGetShortSummaryDescription = false;
    boolean readyToGetLongSummaryDescription = false;


    void characters(char[] ch, int start, int length) {
        if (readyToGetShortSummaryDescription) {
            Util.setLocalisedValue(product, 'ShortSummaryDescription', langid,
                    new String(ch, start, length), 1900, [langid], [lang]);
            readyToGetShortSummaryDescription = false;
        }

        if (readyToGetLongSummaryDescription) {
            Util.setLocalisedValue(product, 'LongSummaryDescription', langid,
                    new String(ch, start, length), 1900, [langid], [lang]);
            readyToGetLongSummaryDescription = false;
        }

    }

    void endElement(String ns, String localName, String qName) {

         if ("ProductRelated" == qName) {
            inProductRelated = false

        }

        if ("ProductFeature" == qName) {
            if (productFeature.feature != null) {
                if (product.productFeatures.containsKey(productFeature.feature.ID)) {
                    def featureVal = product.productFeatures.get(productFeature.feature.ID);
                    // copy over new language value, do not duplicate feature
                    featureVal.PresentationValue.put(lang, productFeature.PresentationValue.get(lang));
                } else {
                    product.productFeatures.put(productFeature.feature.ID, productFeature);
                }
            }
            productFeature = null;
            feature = null;
        }

        if("Product" == qName  && !inProductRelated) {

            Category c = categoryMap.get(product.CategoryID);
            if (c != null ) {
                if (!product.getNameFor("en").isEmpty()) {
                    c.product.put(product.ID, product);
                    println("Added product " + product.Prod_id + "[" + product.ID + "] with " + product.productFeatures.size() +  " features to category " + c.getNameFor('def'))

                }
            }

        }

    }


}
