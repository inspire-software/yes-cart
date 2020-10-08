/*
 * Copyright 2009 Inspire-Software.com
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

export interface DataGroupInfoVO {

  label : string;
  name : string;

}

export interface DataGroupVO {

  datagroupId : number;
  name : string;
  qualifier : string;
  type : string;
  descriptors : string;

  displayNames : Pair<string, string>[];

  createdTimestamp?:Date;
  updatedTimestamp?:Date;
  createdBy?:string;
  updatedBy?:string;

}

export interface DataDescriptorVO {

  datadescriptorId : number;
  name : string;
  type : string;
  value : string;

  createdTimestamp?:Date;
  updatedTimestamp?:Date;
  createdBy?:string;
  updatedBy?:string;

}

