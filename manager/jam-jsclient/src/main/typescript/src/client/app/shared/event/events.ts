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

/**
 * Interface to allow passing of modified object together with validation result.
 */
export interface FormValidationEvent<T> {

  source : T;

  valid  : boolean;

}

/**
 * Timed function allows to postpone function execution to prevent multiple consequitive calls.
 * Typical use case when even has to trigger on some user input change as they type. This functions
 * can be used to set a timer to detect pause in user typing and execute once.
 */
export interface Future {

  delay(ms?:number):void;
  cancel():void;
  isDone():boolean;

}


class TimedFunction implements Future {

  private ref:any;
  private delayMs:number;
  private done:boolean = false;
  private future:() => void;

  constructor(future:() => void, delayMs:number) {
    let that = this;
    this.future = function() {
      that.done = true;
      try {
        future();
      } catch(e) {
        LogUtil.error('TimedFunction error executing timed task', e);
      }
    };
    this.delayMs = delayMs;
    this.ref = setTimeout(this.future, delayMs);
  }

  public delay(ms?:number) {
    if (!this.isDone()) {
      clearTimeout(this.ref);
      let delay = ( (typeof ms === 'undefined') ? this.delayMs : ms);
      this.ref = setTimeout(this.future, delay);
    }
  }

  public cancel() {
    if (!this.isDone()) {
      clearTimeout(this.ref);
    }
  }

  public isDone():boolean {
    return this.done;
  }

}

class ResettableTimedFunction implements Future {


  private ref:any;
  private delayMs:number;
  private done:boolean = false;
  private future:() => void;

  constructor(future:() => void, delayMs:number, startNow:boolean) {
    let that = this;
    this.future = function() {
      that.done = true;
      try {
        future();
      } catch(e) {
        LogUtil.error('ResettableTimedFunction error executing timed task', e);
      }
    };
    this.delayMs = delayMs;
    if (startNow) {
      this.ref = setTimeout(this.future, delayMs);
    } else {
      this.done = true;
    }
  }

  public delay(ms?:number) {
    if (!this.isDone()) {
      // LogUtil.debug('ResettableTimedFunction clear', this.ref);
      clearTimeout(this.ref);
    }
    this.done = false;
    let delay = ( (typeof ms === 'undefined') ? this.delayMs : ms);
    this.ref = setTimeout(this.future, delay);
    // LogUtil.debug('ResettableTimedFunction delay ' + delay, this.ref);
  }

  public cancel() {
    if (!this.isDone()) {
      clearTimeout(this.ref);
      // LogUtil.debug('ResettableTimedFunction cancel', this.ref);
    }
  }

  public isDone():boolean {
    return this.done;
  }

}

export class Futures {

  /**
   * Simple future that delays until it is run once. Thereafter no longer usable.
   *
   * @param future function to execute
   * @param delayMs delay in ms
   * @returns {TimedFunction}
   */
  public static once(future:() => void, delayMs:number):Future {
    return new TimedFunction(future, delayMs);
  }

  /**
   * Perpetual future that behaves line #once(). When done call to #delay() will
   * reset the function to another execution.
   *
   * @param future function to execute
   * @param delayMs delay in ms
   * @returns {ResettableTimedFunction}
   */
  public static perpetual(future:() => void, delayMs:number):Future {
    return new ResettableTimedFunction(future, delayMs, false);
  }

}
