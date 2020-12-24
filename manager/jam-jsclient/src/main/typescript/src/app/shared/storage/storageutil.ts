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


export class StorageUtil {

  /*
   * Create value with given name/value for root path.
   */
  public static saveValue(name:string, value:string):void {

    localStorage.setItem(name, value);

  }

  /*
   * Read value.
   */
  public static readValue(name:string, def:string):string {

    let _existing = localStorage.getItem(name);
    if (_existing) {
      return _existing;
    }

    return def;
  }

  /*
   * Remove value
   */
  public static removeValue(name:string):void {

    localStorage.removeItem(name);

  }

}
