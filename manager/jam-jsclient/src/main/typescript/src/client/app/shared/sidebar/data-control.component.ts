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
import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-data-control',
  moduleId: module.id,
  templateUrl: 'data-control.component.html',
})



export class DataControlComponent implements OnInit {

  @Input() changed : boolean;
  @Input() valid : boolean;

  @Output() saveEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() discardEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() refreshEvent: EventEmitter<any> = new EventEmitter<any>();


  constructor() {
    LogUtil.debug('DataControlComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('DataControlComponent ngOnInit changed=' + this.changed);
  }

  onRefresh() {
    LogUtil.debug('DataControlComponent refresh');
    this.refreshEvent.emit(null);
  }


  onSave() {
    LogUtil.debug('DataControlComponent save ' + this.valid);
    if (this.valid) {
      this.saveEvent.emit(null);
    }
  }


  onDiscard() {
    LogUtil.debug('DataControlComponent discard');
    this.discardEvent.emit(null);
  }

}
