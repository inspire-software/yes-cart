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
import { BrandVO, SearchContextVO } from './../model/index';
import { CatalogService } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Futures, Future, FormValidationEvent } from './../event/index';
import { Config } from './../../../environments/environment';

import { LogUtil } from './../log/index';

@Component({
  selector: 'cw-brand-select',
  templateUrl: 'brand-select.component.html',
})

export class BrandSelectComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<FormValidationEvent<BrandVO>> = new EventEmitter<FormValidationEvent<BrandVO>>();

  @ViewChild('brandModalDialog')
  private brandModalDialog:ModalComponent;

  public validForSelect:boolean = false;

  public filteredBrands : BrandVO[] = [];
  public brandFilter : string;

  private selectedBrand : BrandVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;

  public brandFilterRequired:boolean = true;
  public brandFilterCapped:boolean = false;

  public loading:boolean = false;

  constructor (private _brandService : CatalogService) {
    LogUtil.debug('BrandSelectComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllBrands();
    }, this.delayedFilteringMs);
  }

  ngOnDestroy() {
    LogUtil.debug('BrandSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('BrandSelectComponent ngOnInit');
  }

  onSelectClick(brand: BrandVO) {
    LogUtil.debug('BrandSelectComponent onSelectClick', brand);
    this.selectedBrand = brand;
    this.validForSelect = true;
  }

  onFilterChange() {

    this.delayedFiltering.delay();

  }

  onClearFilter() {
    this.brandFilter = '';
    this.delayedFiltering.delay();
  }

  showDialog() {
    LogUtil.debug('BrandSelectComponent showDialog');
    this.brandModalDialog.show();
  }


  onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('BrandSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.dataSelected.emit({ source: this.selectedBrand, valid: true });
      this.selectedBrand = null;
    }
  }

  private getAllBrands() {

    this.brandFilterRequired = (this.brandFilter == null || this.brandFilter.length < 2);

    if (!this.brandFilterRequired) {
      this.loading = true;
      let _ctx:SearchContextVO = {
        parameters : {
          filter: [ this.brandFilter ]
        },
        start : 0,
        size : this.filterCap,
        sortBy : 'name',
        sortDesc : false
      };
      this._brandService.getFilteredBrands(_ctx).subscribe(allbrands => {
        LogUtil.debug('BrandSelectComponent getAllBrands', allbrands);
        this.selectedBrand = null;
        this.validForSelect = false;
        this.filteredBrands = allbrands != null ? allbrands.items : [];
        this.brandFilterCapped = allbrands != null && allbrands.total > this.filterCap;
        this.loading = false;
      });
    }
  }

}
