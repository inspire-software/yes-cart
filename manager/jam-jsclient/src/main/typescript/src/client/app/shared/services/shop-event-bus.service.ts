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
import { Injectable } from '@angular/core';
import { ShopVO } from '../model/shop.model';
import { BehaviorSubject }    from 'rxjs/BehaviorSubject';
import { Observable }    from 'rxjs/Observable';
import { LogUtil } from './../log/index';

@Injectable()
export class ShopEventBus {

  private static _shopEventBus:ShopEventBus;

  shopUpdated$ : Observable<ShopVO>;
  shopsUpdated$ : Observable<ShopVO[]>;

  private _shopSource : BehaviorSubject<ShopVO>;
  private _shopsSource : BehaviorSubject<ShopVO[]>;

  public static init(shopEventBus:ShopEventBus) {
    ShopEventBus._shopEventBus = shopEventBus;
  }

  public static getShopEventBus() : ShopEventBus {
    return ShopEventBus._shopEventBus;
  }

  constructor() {
    LogUtil.debug('ShopEventBus constructed');
    this._shopSource = new BehaviorSubject<ShopVO>(null);
    this.shopUpdated$ = this._shopSource.asObservable();
    this._shopsSource = new BehaviorSubject<ShopVO[]>(null);
    this.shopsUpdated$ = this._shopsSource.asObservable();
  }

  public emit(value: ShopVO): void {
    this._shopSource.next(value);
    LogUtil.debug('emit shop event', value);
  }

  public emitAll(value: ShopVO[]): void {
    this._shopsSource.next(value);
    LogUtil.debug('emit shops event', value);
  }

  public current():ShopVO {
    return this._shopSource.getValue();
  }

  public currentAll():ShopVO[] {
    return this._shopsSource.getValue();
  }

}
