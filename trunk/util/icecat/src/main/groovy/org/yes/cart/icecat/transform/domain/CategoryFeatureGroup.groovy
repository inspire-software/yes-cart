package org.yes.cart.icecat.transform.domain

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/9/12
 * Time: 12:02 PM
 */
 class CategoryFeatureGroup {

     String ID;
     String No;
     String FeatureGroup_ID;
     String Name;

     List<Feature> featureList = new ArrayList<Feature>();




     public String toString ( ) {
     return "CategoryFeatureGroup{" +
     "ID='" + ID + '\'' +
     ", No='" + No + '\'' +
     ", FeatureGroup_ID='" + FeatureGroup_ID + '\'' +
     ", Name='" + Name + '\'' +
     ", featureList=" + featureList +
     '}' ;
     }}
