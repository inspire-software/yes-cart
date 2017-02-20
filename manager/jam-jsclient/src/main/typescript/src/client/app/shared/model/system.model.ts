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

/**
 * Contains all models related to shop.
 */


import { AttrValueVO } from './attribute.model';


export interface AttrValueSystemVO extends AttrValueVO {

  systemId : number;

}

export interface CacheInfoVO {

  cacheName : string;
  cacheSize : number;

  inMemorySize : number;
  diskStoreSize : number;
  calculateInMemorySize : number;
  calculateOnDiskSize : number;

  hits : number;
  misses : number;

  stats : number;

  nodeId : string;
  nodeUri : string;

}

export interface ClusterNodeVO {

  current : boolean;
  admin : boolean;

  id : string;
  nodeId : string;
  nodeType : string;
  nodeConfig : string;
  clusterId : string;
  version : string;
  buildNo : string;
  fullVersion : string;
  ftIndexDisabled : boolean;

  channel : string;

}

export interface JobStatusVO {

  token : string;
  state : string;
  completion : string;
  report : string;

}

export interface DataGroupInfoVO {

  label : string;
  name : string;

}
