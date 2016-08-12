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

export interface ErrorMessage {

  code : number;
  message : string;

}

export class Util {

  public static clone<T> (object:T):T {
    return JSON.parse(JSON.stringify(object));
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

  public static determineErrorMessage(error:any):ErrorMessage {
    if (error) {
      let code = (error.status && !isNaN(error.status)) ? error.status : 500;
      let message = error.message;
      if (message) {
        return { code: code, message: message };
      }
      if (typeof(error.json) === 'function') {
        try {
          let json = error.json();
          if (json && json.error) {
            return { code: code, message: json.error };
          }
        } catch (err) {
          console.debug('Unable to parse error.json()');
        }
      }
      if (typeof(error.text) === 'function') {
        try {
          return { code: code, message: error.text() };
        } catch (err) {
          console.debug('Unable to get error.text()');
        }
      }
      try {
        return { code: code, message: JSON.stringify(error) };
      } catch (err) {
        console.debug('Unable to JSON.stringify(error)');
      }
    }
    return { code: 500, message: 'Server Error' };
  }

}
