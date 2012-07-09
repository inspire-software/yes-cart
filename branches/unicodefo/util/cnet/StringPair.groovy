package com.npa.cnetxml
/**
 * User: Gordon.Freeman (A) gordon-minus-freeman@ukr.net
 * Date: 10 серп 2010
 * Time: 13:04:30
 */
public class StringPair {

  String attributeName;

  String attributeValue;

  def StringPair(final attributeName, final attributeValue) {
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
  }

  public String toString() {
    return attributeName + '=' +  attributeValue;
  }


}
