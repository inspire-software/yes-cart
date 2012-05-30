package org.yes.cart.icecat.transform.xml

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import org.yes.cart.icecat.transform.domain.Product
import org.yes.cart.icecat.transform.domain.ProductFeature
import org.yes.cart.icecat.transform.domain.Feature
import org.yes.cart.icecat.transform.domain.Category
import org.yes.cart.icecat.transform.domain.CategoryFeatureGroup

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/9/12
 * Time: 9:23 PM
 */
class ProductHandler extends DefaultHandler {

    Product product = null;
    List<Category> categoryList;

    boolean inProductRelated = false



    ProductHandler(List<Category> categoryList) {
        this.categoryList = categoryList
    }

    void startElement(String uri, String localName, String qName, Attributes attributes) {

        if ("ProductRelated" == qName) {
            inProductRelated = true
            product.relatedCategories.add(attributes.getValue("Category_ID"))

        }

        if ("Product" == qName && inProductRelated) {
            product.relatedProduct.add(attributes.getValue("ID"))

        }

        if ("Product" == qName && product == null && inProductRelated == false) {
            product = new Product();
            product.Code = attributes.getValue("Code");
            product.HighPic = attributes.getValue("HighPic");
            product.HighPicHeight = attributes.getValue("HighPicHeight");
            product.HighPicSize = attributes.getValue("HighPicSize");
            product.HighPicWidth = attributes.getValue("HighPicWidth");
            product.ID = attributes.getValue("ID");
            product.LowPic = attributes.getValue("LowPic");
            product.LowPicHeight = attributes.getValue("LowPicHeight");
            product.LowPicSize = attributes.getValue("LowPicSize");
            product.LowPicWidth = attributes.getValue("LowPicWidth");
            product.Name = attributes.getValue("Name");
            product.Pic500x500 = attributes.getValue("Pic500x500");
            product.Pic500x500Height = attributes.getValue("Pic500x500Height");
            product.Pic500x500Size = attributes.getValue("Pic500x500Size");
            product.Pic500x500Width = attributes.getValue("Pic500x500Width");
            product.Prod_id = attributes.getValue("Prod_id").replace("_", "-").replace(" ", "-").replace(".", "-").replace("?", "-"); //sku code
            product.Quality = attributes.getValue("Quality");
            product.ReleaseDate = attributes.getValue("ReleaseDate");
            product.ThumbPic = attributes.getValue("ThumbPic");
            product.ThumbPicSize = attributes.getValue("ThumbPicSize");
            product.Title = attributes.getValue("Title");

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
            productFeature.Presentation_Value = attributes.getValue("Presentation_Value")
        }

        if ("Feature" == qName) {
            String featureId = attributes.getValue("ID");
            feature = locateFeature(locateCategory(product.CategoryID),  featureId);
            productFeature.feature = feature
        }


    }



    ProductFeature productFeature
    Feature feature

    boolean readyToGetShortSummaryDescription = false;
    boolean readyToGetLongSummaryDescription = false;


    void characters(char[] ch, int start, int length) {
        if (readyToGetShortSummaryDescription) {
            product.ShortSummaryDescription = new String(ch, start, length);
            readyToGetShortSummaryDescription = false;
        }

        if (readyToGetLongSummaryDescription) {
            product.LongSummaryDescription = new String(ch, start, length);
            readyToGetLongSummaryDescription = false;
        }

    }

    Feature locateFeature(Category category, String featureId) {

        for(CategoryFeatureGroup cfg : category.categoryFeatureGroup) {
            for(Feature f:cfg.featureList) {
                if(f.ID == featureId) {
                    return f;
                }
            }
        }
        return null;

    }

    Category locateCategory(String categId) {
        for (Category cat: categoryList) {
            if (categId == cat.id) {
                return cat;
            }
        }
        return null;

    }



    void endElement(String ns, String localName, String qName) {

         if ("ProductRelated" == qName) {
            inProductRelated = false

        }

        if ("ProductFeature" == qName) {
            product.productFeatures.add(productFeature);
            productFeature = null;
            feature = null;
        }

        if("Product" == qName  && !inProductRelated) {

            Category c = locateCategory(product.CategoryID);
            c.product.add(product);
            product.CategoryName = (c.name == null ? c.id : c.name); //category name and product type

        }

    }


}
