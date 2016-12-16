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

import { Config } from '../config/env.config';

export class LogUtil {

  private static DEBUG:boolean = Config.DEBUG_ON;


  public static info(message?: any, ...optionalParams: any[]): void {
    if (console) {
      console.info(message, optionalParams);
    }
  }

  public static warn(message?: any, ...optionalParams: any[]): void {
    if (console) {
      console.warn(message, optionalParams);
    }
  }

  public static error(message?: any, ...optionalParams: any[]): void {
    if (console) {
      console.error(message, optionalParams);
    }
  }

  public static log(message?: any, ...optionalParams: any[]): void {
    if (LogUtil.DEBUG && console) {
      console.log(message, optionalParams);
    }
  }

  public static debug(message?: any, ...optionalParams: any[]): void {
    if (LogUtil.DEBUG && console) {
      console.log(message, optionalParams);
    }
  }

}
