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
import { UserVO, JWTAuth } from '../model/manager.model';
import { BehaviorSubject }    from 'rxjs/BehaviorSubject';
import { Observable }    from 'rxjs/Observable';
import { LogUtil } from './../log/index';

@Injectable()
export class UserEventBus {

  private static _userEventBus:UserEventBus;

  userUpdated$ : Observable<UserVO>;
  jwtUpdated$ : Observable<JWTAuth>;
  activeUpdated$ : Observable<boolean>;

  private _userSource : BehaviorSubject<UserVO>;
  private _jwtSource : BehaviorSubject<JWTAuth>;
  private _activeSource : BehaviorSubject<boolean>;

  public static init(userEventBus:UserEventBus) {
    UserEventBus._userEventBus = userEventBus;
  }

  public static getUserEventBus() : UserEventBus {
    return UserEventBus._userEventBus;
  }

  constructor() {
    LogUtil.debug('UserEventBus constructed');
    this._userSource = new BehaviorSubject<UserVO>(null);
    this.userUpdated$ = this._userSource.asObservable();
    this._jwtSource = new BehaviorSubject<JWTAuth>(null);
    this.jwtUpdated$ = this._jwtSource.asObservable();
    this._activeSource = new BehaviorSubject<boolean>(false);
    this.activeUpdated$ = this._activeSource.asObservable();
  }

  public emit(value: UserVO): void {
    LogUtil.debug('emit user event', value);
    this._userSource.next(value);
  }

  public current():UserVO {
    return this._userSource.getValue();
  }

  public emitJWT(value: JWTAuth): void {
    LogUtil.debug('emit JWT event', value);
    this._jwtSource.next(value);
  }

  public currentJWT():JWTAuth {
    return this._jwtSource.getValue();
  }

  public emitActive(value: boolean): void {
    //LogUtil.debug('emit active event new value', this._activeSource.getValue(), value);
    if (this._activeSource.getValue() != value) {
      LogUtil.debug('emit active event', value);
      this._activeSource.next(value);
    }
  }

  public currentActive():boolean {
    return this._activeSource.getValue();
  }

}
