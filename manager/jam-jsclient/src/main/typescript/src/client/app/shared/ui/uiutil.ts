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


export class UiUtil {

  /**
   * Getter for date property that transforms Date into string.
   *
   * @param object source object
   * @param prop date property
   * @return value for input
   */
  public static dateInputGetterProxy(object:any, prop:string):string {
    if (object != null && object[prop] != null) {
      let date = UiUtil.toDateString(object[prop], true);
      // console.debug('CategoryComponent get availableto', object[prop], date);
      return date;
    }
    return null;
  }

  /**
   * Setter for date property that transforms string into Date if format is correct.
   *
   * @param object source object
   * @param prop date property
   * @param value value from input
   */
  public static dateInputSetterProxy(object:any, prop:string, value:string) {
    if (object != null) {
      if (value == null || value.length == 0) {
        // console.debug('CategoryComponent set availableto', value);
        object[prop] = null;
      } else if (value.length == 10 || value.length == 19) {
        let date = UiUtil.toDate(value);
        // console.debug('CategoryComponent set availableto', value, date);
        object[prop] = date;
      }
    }
  }



  private static toTwoChars(num:number):string {
    if (num > 9) {
      return ''+num;
    }
    return '0'+num;
  }

  /**
   * Get string representation of date.
   *
   * @param value date
   * @param showtime if true then adds time as well
   */
  public static toDateString(value:any, showtime:boolean):string {
    if (value == null) {
      return '';
    }

    var date:Date;
    if (value instanceof Date) {
      date = value;
    } else {
      date = new Date(value);
    }

    if (showtime) {
      return date.getFullYear() + '-' + UiUtil.toTwoChars(date.getMonth() + 1) + '-' + UiUtil.toTwoChars(date.getDate()) +
        ' ' +
        UiUtil.toTwoChars(date.getHours()) + ':' + UiUtil.toTwoChars(date.getMinutes()) + ':' + UiUtil.toTwoChars(date.getSeconds());

    }
    return date.getFullYear() + '-' + UiUtil.toTwoChars(date.getMonth()) + '-' + UiUtil.toTwoChars(date.getDate());
  }

  /**
   * Convert string to date.
   *
   * @param date date in YYYY-MM-DD format or datetime in YYYY-MM-DD HH:mm:SS format
   * @returns {any}
   */
  public static toDate(date:string):Date {
    if (date == null || date == '' || (date.length != 10 && date.length != 19)) {
      return null;
    }

    if (date.length > 10) {
      // date time
      let dateAndTime = date.split(' ');
      let dateParts = dateAndTime[0].split('-');
      let timeParts = dateAndTime[1].split(':');
      // console.debug('UiUtil toDate datetime', dateParts, timeParts);
      return new Date(+dateParts[0], +dateParts[1] - 1, +dateParts[2], +timeParts[0], +timeParts[1], +timeParts[2], 0);
    }
    // date only
    let dateParts = date.split('-');
    // console.debug('UiUtil toDate date', dateParts);
    return new Date(+dateParts[0], +dateParts[1] - 1, +dateParts[2], 0, 0, 0, 0);

  }

  /**
   * Create example date search string from one month before till now.
   *
   * @returns {string}
   */
  public static exampleDateSearch():string {
    let now = new Date();
    let yearStart = now.getFullYear();
    let yearEnd = yearStart;
    let mthStart = now.getMonth() + 1;
    let mthEnd = mthStart + 1;
    if (mthStart == 1) {
      yearStart--;
      mthStart = 12;
    }
    return yearStart + '-' + (mthStart < 10 ? '0'+mthStart : mthStart)  + '-01<' + yearEnd + '-' + (mthEnd < 10 ? '0'+mthEnd : mthEnd) + '-01';
  }

}
