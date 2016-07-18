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
import {Injectable} from 'angular2/core';
import {ShopVO} from '../model/shop';
import {BehaviorSubject}    from 'rxjs/subject/BehaviorSubject';
import {Observable}    from 'rxjs/Observable';

@Injectable()
export class ShopEventBus {

  shopUpdated$ : Observable<ShopVO>;

  private _shopSource : BehaviorSubject<ShopVO>;

  constructor() {
    console.debug('ShopEventBus constructed');
    this._shopSource = new BehaviorSubject<ShopVO>(null);
    this.shopUpdated$ = this._shopSource.asObservable();
  }

  public emit(value: ShopVO): void {
    this._shopSource.next(value);
    console.debug('emit shop event', value);
  }

  public current():ShopVO {
    return this._shopSource.getValue();
  }

}
