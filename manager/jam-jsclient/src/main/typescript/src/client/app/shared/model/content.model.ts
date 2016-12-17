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


export interface ContentVO {

  contentId : number;

  parentId : number;
  parentName : string;

  rank : number;

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

  children : Array<ContentVO>;

}


export interface AttrValueContentVO extends AttrValueVO {

  contentId : number;

}

export interface ContentBodyVO {

  contentId : number;
  lang : string;
  text : string;

}

export interface ContentWithBodyVO extends ContentVO {

  contentBodies : ContentBodyVO[];

}
