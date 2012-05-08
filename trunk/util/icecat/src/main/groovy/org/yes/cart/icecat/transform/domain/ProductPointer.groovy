package org.yes.cart.icecat.transform.domain

/**
 * 
 * User: igora Igor Azarny
 * Date: 5/8/12
 * Time: 4:22 PM
 * 
 */
class ProductPointer {

    String path;
    String Product_ID;
    String Updated;
    String Quality;
    String Supplier_id;
    String Prod_ID;
    String Catid;
    String On_Market;
    String Model_Name;
    String Product_View;
    String HighPic;
    String HighPicSize;
    String HighPicWidth;
    String HighPicHeight;
    String Date_Added;

    @Override
    public String toString() {
        return "    ProductPointer{" +
                "path='" + path + '\'' +
                ", Product_ID='" + Product_ID + '\'' +
                ", Updated='" + Updated + '\'' +
                ", Quality='" + Quality + '\'' +
                ", Supplier_id='" + Supplier_id + '\'' +
                ", Prod_ID='" + Prod_ID + '\'' +
                ", Catid='" + Catid + '\'' +
                ", On_Market='" + On_Market + '\'' +
                ", Model_Name='" + Model_Name + '\'' +
                ", Product_View='" + Product_View + '\'' +
                ", HighPic='" + HighPic + '\'' +
                ", HighPicSize='" + HighPicSize + '\'' +
                ", HighPicWidth='" + HighPicWidth + '\'' +
                ", HighPicHeight='" + HighPicHeight + '\'' +
                ", Date_Added='" + Date_Added + '\'' +
                '}';
    }


}
