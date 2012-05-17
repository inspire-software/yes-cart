package org.yes.cart.icecat.transform.domain

/**
 * 
 * User: igora Igor Azarny
 * Date: 5/9/12
 * Time: 2:56 PM
 * 
 */
class Category {

    String id;
    String lowPic;
    String score;
    String searchable;
    String thumbPic
    String UNCATID;
    String visible;

    String description;
    String keywords;
    String name;
    
    List<ProductPointer> productPointer = new ArrayList<ProductPointer>();
    List<Product> product = new ArrayList<Product>();

    List<CategoryFeatureGroup> categoryFeatureGroup = new ArrayList<CategoryFeatureGroup>();

    public String toProductType() { //TPRODTYPE
        return (name == null ? id : name) + ";" + description + "\n";
    }



    public String toProductTypeAttr() {       //TPRODUCTTYPEATTR & TATTRIBUTE
        StringBuilder builder = new StringBuilder();
        for (CategoryFeatureGroup cfg : categoryFeatureGroup) {
            for(Feature feature : cfg.featureList) {
                builder.append(name == null ? id : name)
                builder.append(";")
                builder.append(feature.Name);
                builder.append(";");
                builder.append(feature.Mandatory);
                builder.append(";");
                builder.append(feature.Searchable);
                builder.append(";");
                builder.append(  ((name == null ? id : name) + feature.Name).hashCode() );
                builder.append("\n")
            }

        }
        return builder.toString();
    }


    public String toArrtViewGroup() { //TATTRVIEWGROUP
        StringBuilder builder = new StringBuilder();
        for (CategoryFeatureGroup cfg : categoryFeatureGroup) {
            builder.append(cfg.Name)
            builder.append("\n")

        }
        return builder.toString();
        
        
    }

    public String toProductTypeAttrViewGroup() {       //TPRODTYPEATTRVIEWGROUP
        StringBuilder builder = new StringBuilder();
        for (CategoryFeatureGroup cfg : categoryFeatureGroup) {
            builder.append(cfg.Name)
            builder.append(";")
            for(Feature feature : cfg.featureList) {
                builder.append(feature.Name);
                builder.append(",");
            }
            builder.append("\n")
        }
        return builder.toString();
        
    }



    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", lowPic='" + lowPic + '\'' +
                ", score='" + score + '\'' +
                ", searchable='" + searchable + '\'' +
                ", thumbPic='" + thumbPic + '\'' +
                ", UNCATID='" + UNCATID + '\'' +
                ", visible='" + visible + '\'' +
                ", description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", name='" + name + '\'' +
                ", productPointer  ='" + productPointer.size() + '\'' +
                ", categoryFeatureGroup  ='" + categoryFeatureGroup.size() + '\'' +
                '}';
    }


}
