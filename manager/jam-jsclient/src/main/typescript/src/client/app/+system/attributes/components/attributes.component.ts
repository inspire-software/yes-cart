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
import { AttributeGroupVO, AttributeVO, Pair, SearchResultVO } from './../../../shared/model/index';
import { I18nEventBus } from './../../../shared/services/index';
import { Config } from './../../../shared/config/env.config';
import { LogUtil } from './../../../shared/log/index';

@Component({
  selector: 'yc-attributes',
  moduleId: module.id,
  templateUrl: 'attributes.component.html',
})

export class AttributesComponent implements OnInit, OnDestroy {

  @Input() selectedAttribute:AttributeVO;

  @Output() dataSelected: EventEmitter<AttributeVO> = new EventEmitter<AttributeVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _attributes:SearchResultVO<AttributeVO> = null;

  private filteredAttributes:Array<AttributeVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('AttributesComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('AttributesComponent ngOnInit');
  }

  @Input()
  set attributes(attributes:SearchResultVO<AttributeVO>) {
    this._attributes = attributes;
    this.filterAttributes();
  }

  ngOnDestroy() {
    LogUtil.debug('AttributesComponent ngOnDestroy');
    this.selectedAttribute = null;
    this.dataSelected.emit(null);
  }

  onPageChanged(event:any) {
    if (this.currentPage != event.page) {
      this.pageSelected.emit(event.page - 1);
    }
  }

  onSortClick(event:any) {
    if (event == this.sortColumn) {
      if (this.sortDesc) {  // same column already desc, remove sort
        this.sortSelected.emit(null);
      } else {  // same column asc, change to desc
        this.sortSelected.emit({ first: event, second: true });
      }
    } else { // different column, start asc sort
      this.sortSelected.emit({ first: event, second: false });
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
    if (row.attributegroup === 'PRODUCT') {
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


  protected getAttributeName(attr:AttributeVO):string {

    let lang = I18nEventBus.getI18nEventBus().current();
    let i18n = attr.displayNames;
    let def = attr.name != null ? attr.name : attr.code;

    if (i18n == null) {
      return def;
    }

    let namePair = i18n.find(_name => {
      return _name.first == lang;
    });

    if (namePair != null) {
      return namePair.second;
    }

    return def;
  }


  private filterAttributes() {

    LogUtil.debug('AttributesComponent filterAttributes', this.filteredAttributes);

    if (this._attributes != null) {

      this.filteredAttributes = this._attributes.items != null ? this._attributes.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._attributes.searchContext.size;
      this.totalItems = this._attributes.total;
      this.currentPage = this._attributes.searchContext.start + 1;
      this.sortColumn = this._attributes.searchContext.sortBy;
      this.sortDesc = this._attributes.searchContext.sortDesc;
    } else {
      this.filteredAttributes = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
