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
import { FulfilmentCentreInfoVO, SearchContextVO } from './../model/index';
import { FulfilmentService } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Futures, Future, FormValidationEvent } from './../event/index';
import { Config } from './../../../environments/environment';

import { LogUtil } from './../log/index';

@Component({
  selector: 'cw-fulfilment-centre-select',
  templateUrl: 'fulfilment-centre-select.component.html',
})

export class FulfilmentCentreSelectComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<FormValidationEvent<FulfilmentCentreInfoVO>> = new EventEmitter<FormValidationEvent<FulfilmentCentreInfoVO>>();

  @ViewChild('centreModalDialog')
  private centreModalDialog:ModalComponent;

  public validForSelect:boolean = false;

  public filteredCentres : FulfilmentCentreInfoVO[] = [];
  public centreFilter : string;

  private selectedCentre : FulfilmentCentreInfoVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;

  public centreFilterCapped:boolean = false;

  public loading:boolean = false;

  constructor (private _centreService : FulfilmentService) {
    LogUtil.debug('FulfilmentCentreSelectComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllCentres();
    }, this.delayedFilteringMs);
  }

  ngOnDestroy() {
    LogUtil.debug('FulfilmentCentreSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('FulfilmentCentreSelectComponent ngOnInit');
  }

  onSelectClick(centre: FulfilmentCentreInfoVO) {
    LogUtil.debug('FulfilmentCentreSelectComponent onSelectClick', centre);
    this.selectedCentre = centre;
    this.validForSelect = true;
  }

  onFilterChange() {

    this.delayedFiltering.delay();

  }

  onClearFilter() {
    this.centreFilter = '';
    this.delayedFiltering.delay();
  }

  public showDialog() {
    LogUtil.debug('CarrierSlaSelectComponent showDialog');
    this.centreModalDialog.show();
    this.delayedFiltering.delay();
  }


  onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CarrierSlaSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.dataSelected.emit({ source: this.selectedCentre, valid: true });
      this.selectedCentre = null;
    }
  }

  private getAllCentres() {

    this.loading = true;
    let _ctx:SearchContextVO = {
      parameters : {
        filter: [ this.centreFilter ]
      },
      start : 0,
      size : this.filterCap,
      sortBy : null,
      sortDesc : false
    };
    this._centreService.getFilteredFulfilmentCentres(_ctx).subscribe( allcentres => {
      LogUtil.debug('FulfilmentCentreSelectComponent getAllCentres', allcentres);
      this.selectedCentre = null;
      this.validForSelect = false;
      this.filteredCentres = allcentres != null ? allcentres.items : [];
      this.centreFilterCapped = allcentres != null && allcentres.total > this.filterCap;
      this.loading = false;
    });
  }

}
