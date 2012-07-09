package org.yes.cart.icecat.transform.domain

/**
 *
 * User: igora Igor Azarny
 * Date: 5/9/12
 * Time: 4:08 PM
 *
 */
class Product {

    String Code;
    String HighPic;
    String HighPicHeight;
    String HighPicSize;
    String HighPicWidth;
    String ID;
    String LowPic;
    String LowPicHeight;
    String LowPicSize;
    String LowPicWidth;
    String Name;
    String Pic500x500;
    String Pic500x500Height;
    String Pic500x500Size;
    String Pic500x500Width;
    String Prod_id;
    String Quality;
    String ReleaseDate;
    String ThumbPic;
    String ThumbPicSize;
    String Title;

    String CategoryID;
    String CategoryName;

    String EANCode;

    List<ProductFeature> productFeatures = new ArrayList<ProductFeature>();
    List<String> relatedProduct = new ArrayList<String>();
    Set<String> relatedCategories = new HashSet<String>();


    String ShortSummaryDescription;
    String LongSummaryDescription;
    List<String> productPicture = new ArrayList<String>();

    String Supplier;

    public String toString() {
        StringBuilder prodFeature = new StringBuilder();
        for (ProductFeature pf: productFeatures) {
            prodFeature.append(pf.feature.Name.replace(";", " ").replace('"', "\\\"").replace(',', " "));
            prodFeature.append("->");
            prodFeature.append(pf.Presentation_Value.replace(";", " ").replace('"', "\\\"").replace(',', " ") ) ;
            prodFeature.append(',');
        }



        return (Title + ";"
                + CategoryName + ";"   // category
                + CategoryName + ";" // type
                + Supplier + ";" // brand
                + "std" + ";"
                + Prod_id + ";" //sku code
                + "100" + ";" //qty on warehouse
                + new BigDecimal(500f + new Random().nextFloat() * 3000).setScale(2, BigDecimal.ROUND_HALF_UP) + ";" //price
                + EANCode + ";"
                + prodFeature + ";"
                + LongSummaryDescription
                + "\n"
        );


    }


}
