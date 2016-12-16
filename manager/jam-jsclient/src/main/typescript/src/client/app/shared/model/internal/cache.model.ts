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
import { LogUtil } from './../../log/index';

export class LRUCache {

  private _map:any = {};
  private _count:number = 0;
  private _max:number = 100;

  public constructor(max:number = 100) {
    this._max = max;
  }

  /**
   * Get value from cache (extends TTL)
   *
   * @param key key object
   * @returns {any} value or null
   */
  public getValue(key:any):any {
    let k = JSON.stringify(key);
    if (this._map.hasOwnProperty(k)) {
      let v = this._map[k];
      let _now = Date.now();
      if (_now < v.expiry) {
        v.expiry = _now + v.ttl;
        LogUtil.debug('LRUCache getValue', k, v.value);
        return v.value;
      }
      LogUtil.debug('LRUCache getValue ... expired', k, v.value);
    } else {
      LogUtil.debug('LRUCache getValue ... no value', k);
    }
    return null;
  }

  /**
   * Put value to cache.
   *
   * @param key key object
   * @param value value
   * @param ttl time to live in ms
   */
  public putValue(key:any, value:any, ttl:number = 1000):void {
    let k = JSON.stringify(key);
    let _now = Date.now();

    LogUtil.debug('LRUCache putValue', k, value);

    if (this._count >= this._max) {
      this.removeOutdated(_now);
    }

    this._map[k] = {
      value: value,
      expiry: (_now + ttl),
      ttl: ttl
    };
  }

  /**
   * Remove all outdated entries
   */
  public evictOutdated():void {
    this.removeOutdated(Date.now());
  }

  private removeOutdated(now:number):void {
    for (var _k in this._map) {
      if (now > this._map[_k].expiry) {
        delete this._map[_k];
      }
    }
  }

}
