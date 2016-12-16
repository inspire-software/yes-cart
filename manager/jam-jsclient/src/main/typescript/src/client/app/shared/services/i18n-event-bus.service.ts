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
import { BehaviorSubject }    from 'rxjs/BehaviorSubject';
import { Observable }    from 'rxjs/Observable';
import { LogUtil } from './../log/index';

@Injectable()
export class I18nEventBus {

  private static _i18nEventBus:I18nEventBus;

  i18nUpdated$ : Observable<any>;

  private _i18nSource : BehaviorSubject<any>;

  public static init(i18nEventBus:I18nEventBus) {
    I18nEventBus._i18nEventBus = i18nEventBus;
  }

  public static getI18nEventBus() : I18nEventBus {
    return I18nEventBus._i18nEventBus;
  }

  constructor() {
    LogUtil.debug('I18nEventBus constructed');
    this._i18nSource = new BehaviorSubject<any>('en');
    this.i18nUpdated$ = this._i18nSource.asObservable();
  }

  public emit(value: any): void {
    this._i18nSource.next(value);
    LogUtil.debug('emit i18n event', value);
  }

  public current():any {
    return this._i18nSource.getValue();
  }

}
