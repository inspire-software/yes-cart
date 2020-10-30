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
import { Component, OnInit, OnDestroy, Output, EventEmitter, ViewChild } from '@angular/core';
import { StateVO, SearchContextVO } from './../model/index';
import { LocationService } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Futures, Future, FormValidationEvent } from './../event/index';
import { Config } from './../config/env.config';
import { LogUtil } from './../log/index';

@Component({
  selector: 'cw-country-state-select',
  moduleId: module.id,
  templateUrl: 'country-state-select.component.html',
})

export class CountryStateSelectComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<FormValidationEvent<StateVO>> = new EventEmitter<FormValidationEvent<StateVO>>();

  private changed:boolean = false;

  @ViewChild('stateModalDialog')
  private stateModalDialog:ModalComponent;

  private validForSelect:boolean = false;

  private filteredStates : StateVO[] = [];
  private stateFilter : string;

  private selectedState : StateVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;

  private stateFilterRequired:boolean = true;
  private stateFilterCapped:boolean = false;

  private loading:boolean = false;

  constructor (private _locationService : LocationService) {
    LogUtil.debug('CountryStateSelectComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllStates();
    }, this.delayedFilteringMs);
  }

  ngOnDestroy() {
    LogUtil.debug('CountryStateSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('CountryStateSelectComponent ngOnInit');
  }

  onSelectClick(state: StateVO) {
    LogUtil.debug('CountryStateSelectComponent onSelectClick', state);
    this.selectedState = state;
    this.validForSelect = true;
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  protected onClearFilter() {
    this.stateFilter = '';
    this.delayedFiltering.delay();
  }

  public showDialog() {
    LogUtil.debug('CountryStateSelectComponent showDialog');
    this.stateModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CountryStateSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.dataSelected.emit({ source: this.selectedState, valid: true });
      this.selectedState = null;
    }
  }

  private getAllStates() {

    this.stateFilterRequired = (this.stateFilter == null || this.stateFilter.length < 2);

    if (!this.stateFilterRequired) {
      this.loading = true;
      let _ctx:SearchContextVO = {
        parameters : {
          filter: [ this.stateFilter ]
        },
        start : 0,
        size : this.filterCap,
        sortBy : 'name',
        sortDesc : false
      };
      let _sub:any = this._locationService.getFilteredStates(_ctx).subscribe(allstates => {
        LogUtil.debug('CountryStateSelectComponent getFilteredStates', allstates);
        this.selectedState = null;
        this.changed = false;
        this.validForSelect = false;
        this.filteredStates = allstates != null ? allstates.items : [];
        this.stateFilterCapped = allstates != null && allstates.total > this.filterCap;
        this.loading = false;
        _sub.unsubscribe();
      });
    }
  }

}
