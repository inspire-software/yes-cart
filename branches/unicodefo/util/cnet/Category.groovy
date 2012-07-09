package com.npa.cnetxml
/**
 * User: Gordon.Freeman (A) gordon-minus-freeman@ukr.net
 * Date: 9 серп 2010
 * Time: 16:51:30
 */

public class Category {

  private static int rootId = 100;

  long id;

  long parentId;

  String name;

  String description;

  List<Category> subCategories;

  List<Product> products;

  def Category(final id) {
    this.id = id;
    this.subCategories = new ArrayList<Category>();
    this.products = new ArrayList<Product>();
  }

  def Category() {
    this.id = rootId;
    this.subCategories = new ArrayList<Category>();
    this.products = new ArrayList<Product>();
  }


  String getProdXml() {
    if (rootId == id) {
      return null;
    }
    return "prod-" + id + ".xml";
  }


  String toString() {
    return "Category {" +
            "id=" + id + " name=" + name +
            "\n subCategories=" + subCategories +
            '}';
  }






}