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
import { Component, OnInit, OnDestroy, Output, EventEmitter, ViewChild } from '@angular/core';
import { CarrierSlaVO } from './../model/index';
import { ShippingService } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Futures, Future, FormValidationEvent } from './../event/index';
import { Config } from './../config/env.config';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-carrier-sla-select',
  moduleId: module.id,
  templateUrl: 'carrier-sla-select.component.html',
})

export class CarrierSlaSelectComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<FormValidationEvent<CarrierSlaVO>> = new EventEmitter<FormValidationEvent<CarrierSlaVO>>();

  private changed:boolean = false;

  @ViewChild('carrierSlaModalDialog')
  private carrierSlaModalDialog:ModalComponent;

  private validForSelect:boolean = false;

  private filteredCarrierSlas : CarrierSlaVO[] = [];
  private carrierSlaFilter : string;

  private selectedCarrierSla : CarrierSlaVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;

  private carrierSlaFilterRequired:boolean = true;
  private carrierSlaFilterCapped:boolean = false;

  private loading:boolean = false;

  constructor (private _shippingService : ShippingService) {
    LogUtil.debug('CarrierSlaSelectComponent constructed');
  }

  ngOnDestroy() {
    LogUtil.debug('CarrierSlaSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('CarrierSlaSelectComponent ngOnInit');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllCarrierSlas();
    }, this.delayedFilteringMs);

  }

  onSelectClick(producttype: CarrierSlaVO) {
    LogUtil.debug('CarrierSlaSelectComponent onSelectClick', producttype);
    this.selectedCarrierSla = producttype;
    this.validForSelect = true;
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  public showDialog() {
    LogUtil.debug('CarrierSlaSelectComponent showDialog');
    this.carrierSlaModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CarrierSlaSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.dataSelected.emit({ source: this.selectedCarrierSla, valid: true });
      this.selectedCarrierSla = null;
    }
  }

  private getAllCarrierSlas() {

    this.carrierSlaFilterRequired = (this.carrierSlaFilter == null || this.carrierSlaFilter.length < 2);

    if (!this.carrierSlaFilterRequired) {
      this.loading = true;
      var _sub:any = this._shippingService.getFilteredCarrierSlas(this.carrierSlaFilter, this.filterCap).subscribe(allproducts => {
        LogUtil.debug('CarrierSlaSelectComponent getAllCarrierSlas', allproducts);
        this.selectedCarrierSla = null;
        this.changed = false;
        this.validForSelect = false;
        this.filteredCarrierSlas = allproducts;
        this.carrierSlaFilterCapped = this.filteredCarrierSlas.length >= this.filterCap;
        this.loading = false;
        _sub.unsubscribe();
      });
    }
  }

}
