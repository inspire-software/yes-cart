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
import {Component,  OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {NgFor} from '@angular/common';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {DataGroupInfoVO} from './../model/index';
import {ImpexService, I18nEventBus} from './../services/index';
import {Futures, Future} from './../event/index';
import {Config} from './../config/env.config';

@Component({
  selector: 'yc-data-group-select',
  moduleId: module.id,
  templateUrl: 'data-group-select.component.html',
  directives: [ROUTER_DIRECTIVES, NgFor],
})

export class DataGroupSelectComponent implements OnInit, OnDestroy {

  private groups : DataGroupInfoVO[] = null;
  private filteredGroups : DataGroupInfoVO[] = [];
  private groupFilter : string;

  private selectedGroup : DataGroupInfoVO = null;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  @Input() mode: string = null;

  @Output() dataSelected: EventEmitter<DataGroupInfoVO> = new EventEmitter<DataGroupInfoVO>();

  constructor (private _groupService : ImpexService) {
    console.debug('DataGroupSelectComponent constructed selectedGroup ', this.selectedGroup);
  }

  getAllGroups() {

    var _lang = I18nEventBus.getI18nEventBus().current();
    var _sub:any = this._groupService.getGroups(_lang, this.mode).subscribe(groups => {

      console.debug('ImportManagerComponent groups', groups);
      this.groups = groups;
      this.selectedGroup = null;
      _sub.unsubscribe();
      this.reloadGroupList();

    });

  }

  ngOnDestroy() {
    console.debug('DataGroupSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    console.debug('DataGroupSelectComponent ngOnInit');
    if (this.groups == null) {
      this.getAllGroups();
    }
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.reloadGroupList();
    }, this.delayedFilteringMs);

  }

  onSelectClick(group: DataGroupInfoVO) {
    console.debug('DataGroupSelectComponent onSelectClick', group);
    this.selectedGroup = group;
    this.dataSelected.emit(this.selectedGroup);
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  /**
   * Reload list of groups
   */
  reloadGroupList() {

    if (this.groups != null) {

      if (this.groupFilter) {
        let _filter = this.groupFilter.toLowerCase();
        this.filteredGroups = this.groups.filter(group =>
          group.name.toLowerCase().indexOf(_filter) != -1 ||
          group.label.toLowerCase().indexOf(_filter) != -1
        );
        console.debug('DataGroupSelectComponent reloadGroupList filter: ' + _filter, this.filteredGroups);
      } else {
        this.filteredGroups = this.groups;
        console.debug('DataGroupSelectComponent reloadGroupList no filter', this.filteredGroups);
      }
    }

  }

}
