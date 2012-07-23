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





package org.yes.cart.cnetxml

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