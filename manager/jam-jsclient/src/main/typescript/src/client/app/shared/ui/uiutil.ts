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

import { LRUCache } from '../model/internal/cache.model';
import { FormValidationEvent } from '../event/index';
import { LogUtil } from './../log/index';

export class UiUtil {

  private static _formStatusCacheTTL:number = 1000 * 60 * 60; // 1h
  private static _formStatusCache:LRUCache = new LRUCache(1000);

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
      // LogUtil.debug('CategoryComponent get availableto', object[prop], date);
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
        // LogUtil.debug('CategoryComponent set availableto', value);
        object[prop] = null;
      } else if (value.length == 10 || value.length == 19) {
        let date = UiUtil.toDate(value);
        // LogUtil.debug('CategoryComponent set availableto', value, date);
        object[prop] = date;
      }
    }
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
    return date.getFullYear() + '-' + UiUtil.toTwoChars(date.getMonth() + 1) + '-' + UiUtil.toTwoChars(date.getDate());
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
      // LogUtil.debug('UiUtil toDate datetime', dateParts, timeParts);
      return new Date(+dateParts[0], +dateParts[1] - 1, +dateParts[2], +timeParts[0], +timeParts[1], +timeParts[2], 0);
    }
    // date only
    let dateParts = date.split('-');
    // LogUtil.debug('UiUtil toDate date', dateParts);
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

  /**
   * Bind component with a form to perform delayed event emissions on form status change.
   *
   * @param component component object
   * @param form form property on component
   * @param subscription property on component to hold subscription object Form.statusChanges.subscribe()
   * @param delayedChange property on component for Future object
   * @param initialising property on component with initialisation flag
   * @param future is delayedChange a Future
   */
  public static formBind(component:any, form:string, subscription:string, delayedChange:string, initialising:string, future:boolean = true):void {
    let cacheKey = (form + Math.random()).replace('.', '');
    LogUtil.debug('UiUtils binding form', cacheKey);
    component[subscription] = component[form].statusChanges.subscribe((data:any) => {
      LogUtil.debug('UiUtils form status change form/data/dirty/valid ', form, data, component[form].dirty, component[form].valid);
      if (component[form].dirty && !component[initialising]) {
        let cache:any = UiUtil._formStatusCache.getValue(cacheKey);
        if (cache === null || cache !== data) {
          UiUtil._formStatusCache.putValue(cacheKey, data, UiUtil._formStatusCacheTTL);
          LogUtil.debug('UiUtils form is dirty firing delayedChange form/data/dirty/valid ', form, data, component[form].dirty, component[form].valid);
          if (future) {
            component[delayedChange].delay();
          } else {
            component[delayedChange]();
          }
        }
      } else {
        UiUtil._formStatusCache.putValue(cacheKey, 'INIT', UiUtil._formStatusCacheTTL);
      }
    });
    //component[form].valueChanges.subscribe((data:any) => {
    //  LogUtil.debug('UiUtils form value change form/data/dirty/valid ', form, data, component[form].dirty, component[form].valid);
    //});

  }

  /**
   * Un-bind component with a form.
   *
   * @param component component object
   * @param subscription property on component to hold subscription object Form.statusChanges.subscribe()
   */
  public static formUnbind(component:any, subscription:string):void {
    if (component[subscription]) {
      LogUtil.debug('UiUtils unbinding form', subscription);
      component[subscription].unsubscribe();
    }
  }

  /**
   * Mark single field as dirty.
   *
   * @param component component
   * @param form form
   * @param field filed to mark dirty
   */
  public static formMarkFieldDirty(component:any, form:string, field:string, onlySelf:boolean = true):void {
    LogUtil.debug('UiUtils mark dirty', form, field);
    component[form].controls[field].markAsDirty({ onlySelf: onlySelf });
  }

  public static formI18nDataChange(component:any, form:string, field:string, event:FormValidationEvent<any>):void {
    LogUtil.debug('UiUtils i18n change', form, field, event);
    let dirty = component[form].dirty;
    if (!dirty) {
      UiUtil.formMarkFieldDirty(component, form, field, false);
    }
    if (event.valid) {
      component[form].controls[field].setErrors(null, {emitEvent: true});
    } else {
      component[form].controls[field].setErrors({'invalid': true}, {emitEvent: true});
    }
  }

  /**
   * Initialise form with new data object.
   *
   * @param component component
   * @param initialising initialising flag
   * @param form form property
   * @param field component internal field name
   * @param value new value
   * @param lock boolean flag to lock fields
   * @param fieldsToLock field to lock if #lock is true and unlock if #lock is false
   */
  public static formInitialise(component:any, initialising:string, form:string, field:string, value:any, lock:boolean = false, fieldsToLock:string[] = []):void {

    component[initialising] = true;

    let _old:any = component[field];
    let _form:any = component[form];
    LogUtil.debug('UiUtils form before init form/field/old/new/dirty', form, field, _old, value, _form.dirty);

    component[field] = value;
    if (value != null) {
      _form.reset(value);
      fieldsToLock.forEach(lockedField => {
         if (lock) {
           _form.controls[lockedField].disable({ onlySelf: true });
         } else {
           _form.controls[lockedField].enable({ onlySelf: true });
         }
      });
    }

    component[initialising] = false;
    LogUtil.debug('UiUtils form after init form/field/old/new/dirty', form, field, _old, value, _form.dirty);

  }


  private static toTwoChars(num:number):string {
    if (num > 9) {
      return ''+num;
    }
    return '0'+num;
  }

}
