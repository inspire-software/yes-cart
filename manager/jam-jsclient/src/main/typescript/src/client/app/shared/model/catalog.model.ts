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

import {AttrValueVO, AttributeVO} from './attribute.model';

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
  displayNames : any;

}

export interface ProductTypeAttrNavigationRangeVO {

  range : string;
  displayVals : any;

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
