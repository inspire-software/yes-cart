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
import { BrandVO } from './../model/index';
import { CatalogService } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Futures, Future, FormValidationEvent } from './../event/index';
import { Config } from './../config/env.config';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-brand-select',
  moduleId: module.id,
  templateUrl: 'brand-select.component.html',
})

export class BrandSelectComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<FormValidationEvent<BrandVO>> = new EventEmitter<FormValidationEvent<BrandVO>>();

  private changed:boolean = false;

  @ViewChild('brandModalDialog')
  private brandModalDialog:ModalComponent;

  private validForSelect:boolean = false;

  private filteredBrands : BrandVO[] = [];
  private brandFilter : string;

  private selectedBrand : BrandVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;

  private brandFilterRequired:boolean = true;
  private brandFilterCapped:boolean = false;

  private loading:boolean = false;

  constructor (private _brandService : CatalogService) {
    LogUtil.debug('BrandSelectComponent constructed');
  }

  ngOnDestroy() {
    LogUtil.debug('BrandSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('BrandSelectComponent ngOnInit');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllBrands();
    }, this.delayedFilteringMs);

  }

  onSelectClick(brand: BrandVO) {
    LogUtil.debug('BrandSelectComponent onSelectClick', brand);
    this.selectedBrand = brand;
    this.validForSelect = true;
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  public showDialog() {
    LogUtil.debug('BrandSelectComponent showDialog');
    this.brandModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ProductTypeSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.dataSelected.emit({ source: this.selectedBrand, valid: true });
      this.selectedBrand = null;
    }
  }

  private getAllBrands() {

    this.brandFilterRequired = (this.brandFilter == null || this.brandFilter.length < 2);

    if (!this.brandFilterRequired) {
      this.loading = true;
      var _sub:any = this._brandService.getFilteredBrands(this.brandFilter, this.filterCap).subscribe(allbrands => {
        LogUtil.debug('BrandSelectComponent getAllBrands', allbrands);
        this.selectedBrand = null;
        this.changed = false;
        this.validForSelect = false;
        this.filteredBrands = allbrands;
        this.brandFilterCapped = this.filteredBrands.length >= this.filterCap;
        this.loading = false;
        _sub.unsubscribe();
      });
    }
  }

}
