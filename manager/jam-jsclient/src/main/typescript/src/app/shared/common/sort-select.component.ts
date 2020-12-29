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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Pair } from '../model/common.model';
import { LogUtil } from './../log/index';

@Component({
  selector: 'cw-sort-select',
  templateUrl: 'sort-select.component.html',
})

export class SortSelectComponent implements OnInit, OnDestroy {

  @Input() availableOptions : ValueOption[] = [];

  @Input() sortColumn:string;
  @Input() sortDesc:boolean;

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  @ViewChild('selectModalDialog')
  private selectModalDialog:ModalComponent;

  public validForSelect:boolean = false;

  private selectedOption : string = null;

  constructor () {
    LogUtil.debug('SortSelectComponent constructed');
  }

  ngOnDestroy() {
    LogUtil.debug('SortSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('SortSelectComponent ngOnInit');
  }

  onSelectClick(option: ValueOption) {
    LogUtil.debug('SortSelectComponent onSelectClick', option);
    this.selectedOption = option.key;
    this.validForSelect = true;
  }

  showDialog() {
    LogUtil.debug('SortSelectComponent showDialog');
    this.selectModalDialog.show();
  }


  onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('SortSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let event = this.selectedOption;
      this.selectedOption = null;
      this.validForSelect = false;
      if (event == this.sortColumn) {
        if (this.sortDesc) {  // same column already desc, remove sort
          LogUtil.debug('SortSelectComponent sortSelected default');
          this.sortSelected.emit(null);
        } else {  // same column asc, change to desc
          LogUtil.debug('SortSelectComponent sortSelected desc', event);
          this.sortSelected.emit({ first: event, second: true });
        }
      } else { // different column, start asc sort
        LogUtil.debug('SortSelectComponent sortSelected asc', event);
        this.sortSelected.emit({ first: event, second: false });
      }
    }
  }

}

interface ValueOption {

  key: string;
  messageKey: string;
  message: string;

}
