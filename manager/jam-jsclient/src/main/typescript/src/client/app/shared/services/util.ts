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
import { LogUtil } from './../log/index';

export interface ErrorMessage {

  code : number;
  message : string;
  key? : string;
  params? : any[];

}

export class Util {

  /**
   * Clone Object using JSON serialization.
   *
   * @param object object
   * @returns {any}
   */
  public static clone<T> (object:T):T {
    return JSON.parse(JSON.stringify(object));
  }

  /**
   * Copy values from one object to another.
   *
   * @param fromObject object
   * @param toObject object
   */
  public static copyValues(fromObject:any, toObject:any):void {
    for (var prop in fromObject) {
      if (toObject.hasOwnProperty(prop)) {
        toObject[prop] = fromObject[prop];
      }
    }
  }

  /**
   * Remove from <code>arr</code> all items from itemsToRemove list.
   * @param arr
   * @param itemsToRemove
   * @returns {Array<any>}
   */
  public static remove(arr:Array<any>, itemsToRemove:Array<any>) : Array<any> {
    for (var i =0; i < itemsToRemove.length ; i++) {
      var index = arr.indexOf(itemsToRemove[i], 0);
      if (index > -1) {
        arr.splice(index, 1);
      }
    }
    return arr;
  }

  /**
   * Utility to deduce the correct error message from various error object that
   * may arise during services invocation.
   *
   * @param error error object
   * @returns {any}
   */
  public static determineErrorMessage(error:any):ErrorMessage {
    if (error) {
      let code = (error.status && !isNaN(error.status)) ? error.status : 500;
      let message = error.message;
      if (message) {
        let key = error.key;
        if (key) {
          return { code: code, message: message, key: key, params: error.param };
        }
        return { code: code, message: message };
      }
      if (typeof(error.json) === 'function') {
        try {
          let json = error.json();
          if (json && json.error) {
            return { code: code, message: json.error };
          }
        } catch (err) {
          LogUtil.debug('Unable to parse error.json()');
        }
      }
      if (typeof(error.text) === 'function') {
        try {
          return { code: code, message: error.text() };
        } catch (err) {
          LogUtil.debug('Unable to get error.text()');
        }
      }
      try {
        return { code: code, message: JSON.stringify(error) };
      } catch (err) {
        LogUtil.debug('Unable to JSON.stringify(error)');
      }
    }
    return { code: 500, message: 'Server Error' };
  }

  public static toCsv(object:any, header:boolean):string {

    var _arr:Array<any> = [];
    if (Object.prototype.toString.call(object) === '[object Array]') {
      _arr = <Array<any>> object;
    } else if (object != null) {
      _arr.push(object);
    }

    var _csv:string = '';
    if (header && _arr.length > 0) {
      for (let prop in _arr[0]) {
        _csv += prop + ',';
      }
      _csv += '\n';
    }

    _arr.forEach(val => {
      for (let prop in val) {
        _csv += val[prop] + ',';
      }
      _csv += '\n';
    });

    return _csv;
  }

}
