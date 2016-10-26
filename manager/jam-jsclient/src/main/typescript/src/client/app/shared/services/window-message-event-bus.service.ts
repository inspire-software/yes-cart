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
import {Injectable} from '@angular/core';
import {BehaviorSubject}    from 'rxjs/BehaviorSubject';
import {Observable}    from 'rxjs/Observable';

@Injectable()
export class WindowMessageEventBus {

  private static _windowMessageEventBus:WindowMessageEventBus;

  public static init(windowMessageEventBus:WindowMessageEventBus) {
    WindowMessageEventBus._windowMessageEventBus = windowMessageEventBus;
  }

  public static getWindowMessageEventBus() : WindowMessageEventBus {
    return WindowMessageEventBus._windowMessageEventBus;
  }

  messageUpdated$ : Observable<any>;

  private _messageSource : BehaviorSubject<any>;

  constructor() {
    console.debug('WindowMessageEventBus constructed');
    this._messageSource = new BehaviorSubject<any>('init');
    this.messageUpdated$ = this._messageSource.asObservable();
    let that = this;
    window.addEventListener('message', function(event) {
      console.debug('emit window message event', event);
      that._messageSource.next(event);
    });
  }

  public current():any {
    return this._messageSource.getValue();
  }

}
