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
import { AttrValueVO } from './attribute.model';
import { BrandVO, ProductTypeInfoVO } from './catalog.model';

export interface AssociationVO {

  associationId : number;

  code : string;
  name : string;
  description : string;

}



export interface AttrValueProductVO extends AttrValueVO {

  productId : number;

}



export interface AttrValueProductSkuVO extends AttrValueVO {

  skuId : number;

}


export interface ProductSkuVO {

  skuId : number;
  productId : number;

  guid : string;
  code : string;
  manufacturerCode : string;
  manufacturerPartCode : string;
  supplierCode : string;
  supplierCatalogCode : string;
  barCode : string;

  rank : number;

  name : string;
  description : string;
  displayNames : Pair<string, string>[];

  uri : string;

  title : string;
  displayTitles : Pair<string, string>[];
  metakeywords : string;
  displayMetakeywords : Pair<string, string>[];
  metadescription : string;
  displayMetadescriptions : Pair<string, string>[];

}

export interface ProductCategoryVO {

  productCategoryId : number;

  productId : number;
  categoryId : number;

  categoryName : number;
  rank : number;

}

export interface ProductAssociationVO {

  productassociationId : number;

  associationId : number;
  productId : number;

  rank : number;

  associatedProductId : number;
  associatedCode : string;
  associatedName : string;
  associatedDescription : string;

}

export interface ProductVO {

  productId : number;

  guid : string;
  code : string;
  manufacturerCode : string;
  manufacturerPartCode : string;
  supplierCode : string;
  supplierCatalogCode : string;
  pimCode : string;

  tag : string;
  availablefrom : Date;
  availableto : Date;

  availability : number;

  brand : BrandVO;
  productType : ProductTypeInfoVO;

  productCategories : ProductCategoryVO[];

  name : string;
  description : string;
  displayNames : Pair<string, string>[];

  featured : boolean;

  minOrderQuantity : number;
  maxOrderQuantity : number;
  stepOrderQuantity : number;

  uri : string;

  title : string;
  displayTitles : Pair<string, string>[];
  metakeywords : string;
  displayMetakeywords : Pair<string, string>[];
  metadescription : string;
  displayMetadescriptions : Pair<string, string>[];

  sku : ProductSkuVO[];

}

export interface ProductWithLinksVO extends ProductVO {

  associations : ProductAssociationVO[];

}
