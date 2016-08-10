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
import { Pipe, PipeTransform } from '@angular/core';

/**
 * NG2 DatePipes use Intl package which is NOT available in some browsers, so
 * need to improvise.
 * see https://github.com/angular/angular/issues/3333
 */

function toTwoChars(num:number):string {
  if (num > 9) {
    return ''+num;
  }
  return '0'+num;
}

@Pipe({name: 'ycdate'})
export class YcDatePipe implements PipeTransform {
  transform(value: any, args: string[]): string {
    if (value == null) {
      return '';
    }


    var date:Date;
    if (value instanceof Date) {
      date = value;
    } else {
      date = new Date(value);
    }

    console.debug('ycdatetime formatting', date);
    return date.getFullYear() + '-' + toTwoChars(date.getMonth()) + '-' + toTwoChars(date.getDate());

  }
}


@Pipe({name: 'ycdatetime'})
export class YcDateTimePipe implements PipeTransform {
  transform(value: any, args: string[]): string {
    if (value == null) {
      return '';
    }

    var date:Date;
    if (value instanceof Date) {
      date = value;
    } else {
      date = new Date(value);
    }

    console.debug('ycdatetime formatting', date);
    return date.getFullYear() + '-' + toTwoChars(date.getMonth()) + '-' + toTwoChars(date.getDate()) +
           ' ' +
           toTwoChars(date.getHours()) + ':' + toTwoChars(date.getMinutes()) + ':' + toTwoChars(date.getSeconds());
  }
}

