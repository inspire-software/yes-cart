package com.npa.cnetxml
/**
 * User: Gordon.Freeman (A) gordon-minus-freeman@ukr.net
 * Date: 9 серп 2010
 * Time: 16:58:35
 */

public class Product {


  String id;

  String name;

  String brand;

  String sku;

  String specs; //description     ???

  String price; //LowOfferPrice or HighOfferPrice

  List<StringPair> attrValue = new ArrayList<StringPair>();  


  String getImageFileName() {
    return "i-" + id + ".gif";
  }


  String getAttributeFileName() {
    return "p-" + id + ".html";
  }


  public String toString() {
    return "Product{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", brand='" + brand + '\'' +
            ", sku='" + sku + '\'' +
            ", specs='" + specs + '\'' +
            ", price='" + price + '\'' +
            '}';
  }

  String toCsvString(Category cat) {
    String attrs = "";
    attrValue.each { attrs += it.attributeName + "=" + it.attributeValue + "|"}
    return name + ';' +
           cat.getName() + ';' +
           cat.getName() + ';' +
           brand + ';' +
           "Standard" + ';' +
           sku + ';' +
           attrs + ';' +
           10 + ';' + //qty
           price + ';' +
           specs;
  }

}