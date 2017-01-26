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

export interface ManagerInfoVO {

  managerId : number;

  email : string;

  firstName  : string;

  lastName  : string;

  enabled  : boolean;

}

export interface ManagerShopLinkVO {

  managerId : number;

  shopId : number;

}

export interface ManagerRoleLinkVO {

  managerId : number;

  roleId : number;

  code : string;

}

export interface ManagerVO extends ManagerInfoVO {

  managerShops : Array<ManagerShopLinkVO>;

  managerRoles : Array<ManagerRoleLinkVO>;

}

export interface LicenseAgreementVO {

  agreed : boolean;

  text : string;

}

export interface UserVO {

  manager: ManagerVO;
  name: string;
  ui: string;

}
