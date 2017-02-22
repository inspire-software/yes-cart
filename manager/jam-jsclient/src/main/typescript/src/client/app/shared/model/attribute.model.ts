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

export interface EtypeVO {

  etypeId : number;
  javatype : string;
  businesstype : string;

}

export interface AttributeGroupVO {

  attributegroupId : number;

  code : string;
  name : string;
  description : string;

}

export interface AttributeVO {

  attributeId : number;

  attributegroupId : number;

  code : string;
  name : string;
  description : string;

  displayNames :  Pair<string, string>[];

  etypeId : number;
  etypeName : string;

  val : string;

  mandatory : boolean;
  allowduplicate : boolean;
  allowfailover : boolean;

  regexp : string;
  validationFailedMessage : any;

  rank : number;

  choiceData : Pair<string, string>[];

  store : boolean;
  search : boolean;
  primary : boolean;
  navigation : boolean;

}

export interface AttrValueVO {

  attrvalueId : number;

  val : string;
  displayVals :  Pair<string, string>[];
  valBase64Data : string;

  attribute :  AttributeVO;


}

