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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { AttributeGroupVO, AttributeVO } from './../../../shared/model/index';
import { Config } from './../../../shared/config/env.config';
import { LogUtil } from './../../../shared/log/index';

@Component({
  selector: 'yc-attributes',
  moduleId: module.id,
  templateUrl: 'attributes.component.html',
})

export class AttributesComponent implements OnInit, OnDestroy {

  @Input() group:AttributeGroupVO;

  @Input() selectedAttribute:AttributeVO;

  @Output() dataSelected: EventEmitter<AttributeVO> = new EventEmitter<AttributeVO>();

  private _attributes:Array<AttributeVO> = [];

  private filteredAttributes:Array<AttributeVO>;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  private pageStart:number = 0;
  private pageEnd:number = this.itemsPerPage;

  constructor() {
    LogUtil.debug('AttributesComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('AttributesComponent ngOnInit');
  }

  @Input()
  set attributes(attributes:Array<AttributeVO>) {
    this._attributes = attributes;
    this.filterAttributes();
  }

  ngOnDestroy() {
    LogUtil.debug('AttributesComponent ngOnDestroy');
    this.selectedAttribute = null;
    this.dataSelected.emit(null);
  }

  resetLastPageEnd() {
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  onPageChanged(event:any) {
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onSelectRow(row:AttributeVO) {
    LogUtil.debug('AttributesComponent onSelectRow handler', row);
    if (row == this.selectedAttribute) {
      this.selectedAttribute = null;
    } else {
      this.selectedAttribute = row;
    }
    this.dataSelected.emit(this.selectedAttribute);
  }

  protected getSearchFlags(row:AttributeVO) {
    if (this.group && this.group.code === 'PRODUCT') {
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
    return '&nbsp;';
  }

  private filterAttributes() {

    this.filteredAttributes = this._attributes;
    LogUtil.debug('AttributesComponent filterAttributes', this.filteredAttributes);

    if (this.filteredAttributes === null) {
      this.filteredAttributes = [];
    }

    let _total = this.filteredAttributes.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
