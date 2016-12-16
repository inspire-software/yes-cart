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
import { Component,  OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { AttributeVO } from './../model/index';
import { AttributeService } from './../services/index';
import { Futures, Future } from './../event/index';
import { Config } from './../config/env.config';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-product-attribute-select',
  moduleId: module.id,
  templateUrl: 'product-attribute-select.component.html',
})

export class ProductAttributeSelectComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<AttributeVO> = new EventEmitter<AttributeVO>();

  private filteredAttributes : AttributeVO[] = [];
  private attributeFilter : string;

  private selectedAttribute : AttributeVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;

  private attributeFilterRequired:boolean = true;
  private attributeFilterCapped:boolean = false;

  private loading:boolean = false;

  constructor (private _attributeService : AttributeService) {
    LogUtil.debug('ProductAttributeSelectComponent constructed');
  }

  ngOnDestroy() {
    LogUtil.debug('ProductAttributeSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('ProductAttributeSelectComponent ngOnInit');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllAttributes();
    }, this.delayedFilteringMs);

  }

  onSelectClick(attribute: AttributeVO) {
    LogUtil.debug('ProductAttributeSelectComponent onSelectClick', attribute);
    this.selectedAttribute = attribute;
    this.dataSelected.emit(this.selectedAttribute);
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }


  protected getSearchFlags(row:AttributeVO) {
    let flags = '';
    if (row.store) {
      flags += '<i class="fa fa-save"></i>&nbsp;';
    }
    if (row.search) {
      if (row.primary) {
        flags += '<i class="fa fa-search-plus"></i>&nbsp;';
      } else {
        flags += '<i class="fa fa-search"></i>&nbsp;';
      }
    }
    if (row.navigation) {
      flags += '<i class="fa fa-list-alt"></i>&nbsp;';
    }
    return flags;
  }


  private getAllAttributes() {

    this.attributeFilterRequired = (this.attributeFilter == null || this.attributeFilter.length < 2);

    if (!this.attributeFilterRequired) {
      this.loading = true;
      var _sub:any = this._attributeService.getFilteredAttributes('PRODUCT', this.attributeFilter, this.filterCap).subscribe(allattributes => {
        LogUtil.debug('ProductAttributeSelectComponent getAllAttributes', allattributes);
        this.filteredAttributes = allattributes;
        this.attributeFilterCapped = this.filteredAttributes.length >= this.filterCap;
        this.loading = false;
        _sub.unsubscribe();
      });
    }
  }


}
