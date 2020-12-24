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

export interface ManagerInfoVO {

  managerId : number;

  login : string;
  email : string;

  firstName  : string;
  lastName  : string;

  enabled  : boolean;

  companyName1  : string;
  companyName2  : string;
  companyDepartment  : string;

  createdTimestamp?:Date;
  updatedTimestamp?:Date;
  createdBy?:string;
  updatedBy?:string;

}

export interface ManagerShopLinkVO {

  managerId : number;
  shopId : number;

  createdTimestamp?:Date;
  updatedTimestamp?:Date;
  createdBy?:string;
  updatedBy?:string;

}

export interface ManagerRoleLinkVO {

  managerId : number;
  code : string;

  createdTimestamp?:Date;
  updatedTimestamp?:Date;
  createdBy?:string;
  updatedBy?:string;

}

export interface ManagerSupplierCatalogVO {

  managerId : number;
  code : string;

  createdTimestamp?:Date;
  updatedTimestamp?:Date;
  createdBy?:string;
  updatedBy?:string;

}

export interface ManagerCategoryCatalogVO {

  managerId : number;
  categoryId : number;

  code : string;
  name : string;

  createdTimestamp?:Date;
  updatedTimestamp?:Date;
  createdBy?:string;
  updatedBy?:string;

}

export interface ManagerVO extends ManagerInfoVO {

  managerShops : Array<ManagerShopLinkVO>;
  managerRoles : Array<ManagerRoleLinkVO>;
  managerSupplierCatalogs : Array<ManagerSupplierCatalogVO>;
  managerCategoryCatalogs : Array<ManagerCategoryCatalogVO>;

}

export interface LicenseAgreementVO {

  agreed : boolean;

  text : string;

}

export interface UserVO {

  manager: ManagerVO;
  name: string;
  ui: any;

}

export interface JWT {

  token: string;

}

export interface JWTDecoded {

  aud: string;
  exp: number;
  iat: number;
  iss: string;
  rol: string[];
  sub: string;

}

export interface JWTAuth {

  jwt?: string;
  decoded?: JWTDecoded;
  status: number;
  message?: string;
  attemptResume?: boolean;

}

export interface LoginVO {

  username:string;
  password:string;
  organisation?:string;

  npassword?:string;
  cpassword?:string;

}
