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
