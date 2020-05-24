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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { InventoryVO, Pair, SearchResultVO } from './../../shared/model/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-inventory',
  moduleId: module.id,
  templateUrl: 'inventory.component.html',
})

export class InventoryComponent implements OnInit, OnDestroy {

  @Input() selectedInventory:InventoryVO;

  @Output() dataSelected: EventEmitter<InventoryVO> = new EventEmitter<InventoryVO>();

  @Output() pageSelected: EventEmitter<number> = new EventEmitter<number>();

  @Output() sortSelected: EventEmitter<Pair<string, boolean>> = new EventEmitter<Pair<string, boolean>>();

  private _inventory:SearchResultVO<InventoryVO> = null;

  private filteredInventory:Array<InventoryVO>;

  //sorting
  private sortColumn:string = null;
  private sortDesc:boolean = false;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1;

  constructor() {
    LogUtil.debug('InventoryComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('InventoryComponent ngOnInit');
  }

  @Input()
  set inventory(inventory:SearchResultVO<InventoryVO>) {
    this._inventory = inventory;
    this.filterInventory();
  }

  ngOnDestroy() {
    LogUtil.debug('InventoryComponent ngOnDestroy');
    this.selectedInventory = null;
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

  protected onSelectRow(row:InventoryVO) {
    LogUtil.debug('InventoryComponent onSelectRow handler', row);
    if (row == this.selectedInventory) {
      this.selectedInventory = null;
    } else {
      this.selectedInventory = row;
    }
    this.dataSelected.emit(this.selectedInventory);
  }

  protected isAvailableFromNow(row:InventoryVO) {
    return row.availablefrom === null || (row.availablefrom < new Date());
  }

  protected isAvailableToNow(row:InventoryVO) {
    return row.availableto === null || (row.availableto > new Date());
  }

  protected isReleasedNow(row:InventoryVO) {
    return row.releaseDate === null || (row.releaseDate < new Date());
  }

  private filterInventory() {

    LogUtil.debug('InventoryComponent filterInventory', this.filteredInventory);

    if (this._inventory != null) {

      this.filteredInventory = this._inventory.items != null ? this._inventory.items : [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = this._inventory.searchContext.size;
      this.totalItems = this._inventory.total;
      this.currentPage = this._inventory.searchContext.start + 1;
      this.sortColumn = this._inventory.searchContext.sortBy;
      this.sortDesc = this._inventory.searchContext.sortDesc;
    } else {
      this.filteredInventory = [];
      this.maxSize = Config.UI_TABLE_PAGE_NUMS;
      this.itemsPerPage = Config.UI_TABLE_PAGE_SIZE;
      this.totalItems = 0;
      this.currentPage = 1;
      this.sortColumn = null;
      this.sortDesc = false;
    }

  }

}
