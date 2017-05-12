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
export class UserEventBus {

  private static _userEventBus:UserEventBus;

  userUpdated$ : Observable<any>;

  private _userSource : BehaviorSubject<any>;

  public static init(userEventBus:UserEventBus) {
    UserEventBus._userEventBus = userEventBus;
  }

  public static getUserEventBus() : UserEventBus {
    return UserEventBus._userEventBus;
  }

  constructor() {
    LogUtil.debug('UserEventBus constructed');
    this._userSource = new BehaviorSubject<any>(null);
    this.userUpdated$ = this._userSource.asObservable();
  }

  public emit(value: any): void {
    this._userSource.next(value);
    LogUtil.debug('emit user event', value);
  }

  public current():any {
    return this._userSource.getValue();
  }

}
