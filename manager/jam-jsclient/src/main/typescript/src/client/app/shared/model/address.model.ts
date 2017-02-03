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

export interface AddressVO {

  addressId: number;
  customerId: number;
  addressType: string;
  defaultAddress: boolean;

  city: string;
  countryCode: string;
  stateCode: string;

  salutation: string;
  firstname: string;
  middlename: string;
  lastname: string;

  postcode: string;
  addrline1: string;
  addrline2: string;

  phone1: string;
  phone2: string;
  mobile1: string;
  mobile2: string;
  email1: string;
  email2: string;

  custom0: string;
  custom1: string;
  custom2: string;
  custom3: string;
  custom4: string;
  custom5: string;
  custom6: string;
  custom7: string;
  custom8: string;
  custom9: string;

}

export interface AddressBookVO {

  addresses: AddressVO[];

  formattingShopId: number;

  formattedAddresses: Pair<number, string>[];

  addressForm: Pair<string, AttrValueVO[]>[];

  countries: Pair<string, Pair<string, string>[]>[];
  states: Pair<string, Pair<string, string>[]>[];

}
