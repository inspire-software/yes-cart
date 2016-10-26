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
import {Component,  OnInit, OnDestroy, Output, EventEmitter} from '@angular/core';
import {NgFor} from '@angular/common';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {AttributeVO} from './../model/index';
import {AttributeService} from './../services/index';
import {Futures, Future} from './../event/index';
import {Config} from './../config/env.config';

@Component({
  selector: 'yc-product-attribute-select',
  moduleId: module.id,
  templateUrl: 'product-attribute-select.component.html',
  directives: [ROUTER_DIRECTIVES, NgFor],
})

export class ProductAttributeSelectComponent implements OnInit, OnDestroy {

  private filteredAttributes : AttributeVO[] = [];
  private attributeFilter : string;

  private selectedAttribute : AttributeVO = null;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;

  attributeFilterRequired:boolean = true;
  attributeFilterCapped:boolean = false;

  @Output() dataSelected: EventEmitter<AttributeVO> = new EventEmitter<AttributeVO>();

  constructor (private _attributeService : AttributeService) {
    console.debug('ProductAttributeSelectComponent constructed');
  }

  getAllAttributes() {

    this.attributeFilterRequired = (this.attributeFilter == null || this.attributeFilter.length < 2);

    if (!this.attributeFilterRequired) {
      var _sub:any = this._attributeService.getFilteredAttributes('PRODUCT', this.attributeFilter, this.filterCap).subscribe(allattributes => {
        console.debug('ProductAttributeSelectComponent getAllAttributes', allattributes);
        this.filteredAttributes = allattributes;
        this.attributeFilterCapped = this.filteredAttributes.length >= this.filterCap;
        _sub.unsubscribe();
      });
    }
  }

  ngOnDestroy() {
    console.debug('ProductAttributeSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    console.debug('ProductAttributeSelectComponent ngOnInit');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getAllAttributes();
    }, this.delayedFilteringMs);

  }

  onSelectClick(attribute: AttributeVO) {
    console.debug('ProductAttributeSelectComponent onSelectClick', attribute);
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


}
