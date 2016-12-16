/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import { Pair } from './common.model';
import { AttrValueVO, AttributeVO } from './attribute.model';

export interface BrandVO {

  brandId : number;
  name : string;
  description : string;

}

export interface AttrValueBrandVO extends AttrValueVO {

  brandId : number;

}

export interface ProductTypeInfoVO {

  producttypeId : number;

  guid  : string;

  name : string;
  description : string;

  uitemplate : string;
  uisearchtemplate : string;

  ensemble : boolean;
  shippable : boolean;
  downloadable : boolean;
  digital : boolean;

}

export interface ProductTypeViewGroupVO {

  prodTypeAttributeViewGroupId : number;
  producttypeId : number;

  attrCodeList : string[];

  rank : number;

  name : string;
  displayNames : Pair<string, string>[];

}

export interface ProductTypeAttrNavigationRangeVO {

  range : string;
  displayVals : Pair<string, string>[];

}

export interface ProductTypeAttrNavigationRangesVO {

  ranges : ProductTypeAttrNavigationRangeVO[];

}

export interface ProductTypeAttrVO {

  productTypeAttrId : number;
  producttypeId : number;

  attribute : AttributeVO;

  rank : number;

  visible : boolean;
  similarity : boolean;
  store : boolean;
  search : boolean;
  primary : boolean;
  navigation : boolean;
  navigationType : string;

  rangeNavigation : ProductTypeAttrNavigationRangesVO;

}

export interface ProductTypeVO extends ProductTypeInfoVO {

  viewGroups : ProductTypeViewGroupVO[];

}

export interface BasicCategoryVO {

  name : string;

  guid  : string;

}

export interface CategoryNavigationPriceTierVO {

  from : number;
  to : number;

}

export interface CategoryNavigationPriceTiersVO {

  tiers : Array<Pair<string, Array<CategoryNavigationPriceTierVO>>>;

}

export interface CategoryVO {

  categoryId : number;

  parentId : number;
  parentName : string;

  linkToId : number;
  linkToName : string;

  rank : number;

  productTypeId : number;
  productTypeName : string;

  name : string;

  guid  : string;

  displayNames :  Pair<string, string>[];

  description : string;

  uitemplate  : string;

  availablefrom : Date;

  availableto : Date;

  uri  : string;

  title  : string;

  metakeywords  : string;

  metadescription  : string;

  displayTitles : Pair<string, string>[];

  displayMetakeywords : Pair<string, string>[];

  displayMetadescriptions : Pair<string, string>[];

  navigationByAttributes : boolean;
  navigationByBrand : boolean;
  navigationByPrice : boolean;
  navigationByPriceTiers  : CategoryNavigationPriceTiersVO;

  children : Array<CategoryVO>;

}


export interface AttrValueCategoryVO extends AttrValueVO {

  categoryId : number;

}
