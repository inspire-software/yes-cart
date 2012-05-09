package org.yes.cart.icecat.transform.xml

import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.Attributes
import org.yes.cart.icecat.transform.domain.Category
import org.yes.cart.icecat.transform.domain.CategoryFeatureGroup
import org.yes.cart.icecat.transform.domain.Feature;

/**
 *
 * User: igora Igor Azarny
 * Date: 5/9/12
 * Time: 2:49 PM
 *
 */
class CategoryHandler extends DefaultHandler {

    boolean allowAddToCategoryList = false;

    List<Category> categoryList = new ArrayList<Category>();

    List<String> categoryIdFiler;
    String langFilter;

    Category category;
    CategoryFeatureGroup categoryFeatureGroup;
    Feature feature = null;


    String description;
    String keywords;
    String name;

    CategoryHandler(String categoryList, String langFilter) {

        this.langFilter = langFilter
        categoryIdFiler = Arrays.asList(categoryList.split(","));

    }



    void startElement(String uri, String localName, String qName, Attributes attributes) {

        if ("Category" == qName) {
            if (allowAddToCategoryList) {
                category = new Category();
                category.id = attributes.getValue("ID");
                category.lowPic = attributes.getValue("LowPic");
                category.score = attributes.getValue("Score");
                category.searchable = attributes.getValue("Searchable");
                category.thumbPic = attributes.getValue("ThumbPic");
                category.UNCATID = attributes.getValue("UNCATID");
                category.visible = attributes.getValue("Visible");
            } else {
                //locate already collected category and fill features
                if (categoryIdFiler.contains(attributes.getValue("ID"))) {
                    category = locateCategory(attributes.getValue("ID"));
                }

            }
        } else if ("Feature" == qName) {

            feature = new Feature();
            feature.CategoryFeatureGroup_ID = attributes.getValue("CategoryFeatureGroup_ID");
            feature.CategoryFeature_ID = attributes.getValue("CategoryFeature_ID");
            feature.lClass = attributes.getValue("Class");
            feature.ID = attributes.getValue("ID");
            feature.LimitDirection = attributes.getValue("LimitDirection");
            feature.Mandatory = attributes.getValue("Mandatory");
            feature.No = attributes.getValue("No");
            feature.Searchable = attributes.getValue("Searchable");
            feature.Use_Dropdown_Input = attributes.getValue("Use_Dropdown_Input");

        } else if ("FeatureGroup" == qName) {
            if (langFilter == attributes.getValue("langid")) {
                categoryFeatureGroup.Name = attributes.getValue("Value");
            }
        } else if ("CategoryFeatureGroup" == qName) {
            categoryFeatureGroup = new CategoryFeatureGroup();
            categoryFeatureGroup.ID = attributes.getValue("ID");
            categoryFeatureGroup.No = attributes.getValue("No");

        } else if ("Description" == qName) {
            if (langFilter == attributes.getValue("langid")) {
                category.description = attributes.getValue("Value");
            }
        } else if ("Name" == qName) {
            if (feature == null) {
                if (langFilter == attributes.getValue("langid")) {
                    category.name = attributes.getValue("Value");
                }
            } else {
                if (langFilter == attributes.getValue("langid")) {
                    feature.Name = attributes.getValue("Value");
                }
            }


        } else if ("Keywords" == qName) {
            if (langFilter == attributes.getValue("langid")) {
                category.keywords = attributes.getValue("Value");
            }
        } else if ("CategoriesList" == qName) {
            allowAddToCategoryList = true;
        }
    }

    Category locateCategory(String categId) {

        Category c = null;
        categoryList.each {
            if (categId == it.id) {
                c = it;
            }
        }
        return c;

    }


    void endElement(String ns, String localName, String qName) {

        if ("Category" == qName) {
            if (allowAddToCategoryList && categoryIdFiler.contains(category.id)) {
                categoryList.add(category);
            }
        } else if ("Feature" == qName) {
            //todo add into groupd
            feature = null;

        } else if ("CategoryFeatureGroup" == qName) {

            category.categoryFeatureGroup.add(categoryFeatureGroup);

        } else if ("CategoriesList" == qName) {
            allowAddToCategoryList = false;

        }


    }


}
