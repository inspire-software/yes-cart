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


export class CookieUtil {

  /*
   * Create cookie with given name/value for root path.
   */
  public static createCookie(name:string, value:string, days:number):void {
    let expires = '';
    if (days) {
      var date = new Date();
      date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
      expires = '; expires=' + date.toUTCString();
    }
    document.cookie = encodeURIComponent(name) + '=' + encodeURIComponent(value) + expires + '; path=/';
  }

  /*
   * Read cookie.
   */
  public static readCookie(name:string, def:string):string {
    var nameEQ = encodeURIComponent(name) + '=';
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
      var c = ca[i];
      while (c.charAt(0) == ' ') c = c.substring(1, c.length);
      if (c.indexOf(nameEQ) == 0) return decodeURIComponent(c.substring(nameEQ.length, c.length));
    }
    return def;
  }

  /*
   * Write cookie with negative expiry time to remove it
   */
  public static eraseCookie(name:string):void {
    CookieUtil.createCookie(name, '', -1);
  }

}
