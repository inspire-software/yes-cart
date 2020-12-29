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
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { LogUtil } from './../log/index';

@Injectable()
export class CommandEventBus {

  private static _commandEventBus:CommandEventBus;

  commandUpdated$ : Observable<any>;

  private _commandSource : BehaviorSubject<any>;

  public static init(commandEventBus:CommandEventBus) {
    CommandEventBus._commandEventBus = commandEventBus;
  }

  public static getCommandEventBus() : CommandEventBus {
    return CommandEventBus._commandEventBus;
  }

  constructor() {
    LogUtil.debug('CommandEventBus constructed');
    this._commandSource = new BehaviorSubject<any>(null);
    this.commandUpdated$ = this._commandSource.asObservable();
  }

  public emit(value: any): void {
    LogUtil.debug('emit command event', value);
    this._commandSource.next(value);
  }

  public current():any {
    return this._commandSource.getValue();
  }

}
