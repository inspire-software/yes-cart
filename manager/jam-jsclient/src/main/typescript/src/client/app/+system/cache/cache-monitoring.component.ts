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
import {Component, OnInit} from '@angular/core';
import {CORE_DIRECTIVES } from '@angular/common';
import {REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {PaginationComponent} from './../../shared/pagination/index';
import {CacheInfoVO} from './../../shared/model/index';
import {SystemService, Util} from './../../shared/services/index';
import {Futures, Future} from './../../shared/event/index';
import {Config} from './../../shared/config/env.config';

@Component({
  selector: 'yc-cache-monitoring',
  moduleId: module.id,
  templateUrl: 'cache-monitoring.component.html',
  directives: [PaginationComponent, REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES]
})

export class CacheMonitoringComponent implements OnInit {

  private searchHelpShow:boolean = false;

  caches:Array<CacheInfoVO> = [];
  filteredCaches:Array<CacheInfoVO> = [];
  cacheFilter:string;

  selectedRow:CacheInfoVO;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  //paging
  maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  totalItems:number = 0;
  currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  pageStart:number = 0;
  pageEnd:number = this.itemsPerPage;

  /**
   * Construct shop attribute panel
   *
   * @param _systemService system service
   */
  constructor(private _systemService:SystemService) {
    console.debug('CacheMonitoringComponent constructed');

  }

  /** {@inheritDoc} */
  public ngOnInit() {
    console.debug('CacheMonitoringComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterCaches();
    }, this.delayedFilteringMs);

  }


  protected onRowDeleteSelected() {
    if (this.selectedRow != null) {

      console.debug('CacheMonitoringComponent delete cache', this.selectedRow);

      var _sub:any = this._systemService.evictSingleCache(this.selectedRow.cacheName).subscribe(caches => {

        console.debug('CacheMonitoringComponent evictSingleCache', caches);
        this.caches = caches;
        this.selectedRow = null;
        this.filterCaches();
        _sub.unsubscribe();

      });

    } else {

      console.debug('CacheMonitoringComponent delete cache all');

      var _sub:any = this._systemService.evictAllCache().subscribe(caches => {

        console.debug('CacheMonitoringComponent evictAllCache', caches);
        this.caches = caches;
        this.selectedRow = null;
        this.filterCaches();
        _sub.unsubscribe();

      });

    }
  }

  protected onRowEditSelected() {
    if (this.selectedRow != null) {

      let cache = this.selectedRow.cacheName;
      let stats = !this.selectedRow.stats;

      console.debug('CacheMonitoringComponent stats set: ' + stats, this.selectedRow);

      var _sub:any = this._systemService.saveCacheStatsFlag(cache, stats).subscribe(caches => {

        console.debug('CacheMonitoringComponent saveCacheStatsFlag', caches);
        this.caches = caches;
        this.selectedRow = null;
        this.filterCaches();
        _sub.unsubscribe();

      });

    }
  }

  protected onSelectRow(row:CacheInfoVO) {
    console.debug('CacheMonitoringComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onSaveHandler() {
    console.debug('CacheMonitoringComponent Save handler');

    let myWindow = window.open('', 'ExportCacheInfo', 'width=800,height=600');

    var _csv:string = Util.toCsv(this.caches, true);
    myWindow.document.write('<textarea style="width:100%; height:100%">' + _csv + '</textarea>');

  }

  protected onRefreshHandler() {
    console.debug('CacheMonitoringComponent refresh handler');
    this.getCacheInfo();
  }

  /**
   * Read attributes.
   */
  private getCacheInfo() {
    console.debug('CacheMonitoringComponent get caches');

    var _sub:any = this._systemService.getCacheInfo().subscribe(caches => {

      console.debug('CacheMonitoringComponent attributes', caches);
      this.caches = caches;
      this.selectedRow = null;
      this.filterCaches();
      _sub.unsubscribe();

    });

  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  private filterCaches() {
    if (this.cacheFilter) {

      let _filter = this.cacheFilter.toLowerCase();

      if (_filter.indexOf('#') == 0) {

        let _size = parseInt(_filter.substr(1));
        if (isNaN(_size)) {
          _size = 100;
        }

        this.filteredCaches = this.caches.filter(cache =>
          cache.cacheSize >= _size
        );
        console.debug('CacheMonitoringComponent filterCaches size', this.cacheFilter);
      } else if (_filter.indexOf('^') == 0) {

        let _top = parseInt(_filter.substr(1));
        if (isNaN(_top)) {
          _top = 5;
        }

        this.filteredCaches = this.caches.sort((n1,n2) => n2.cacheSize - n1.cacheSize).slice(0, _top);

      } else {
        this.filteredCaches = this.caches.filter(cache =>
          cache.cacheName.toLowerCase().indexOf(_filter) !== -1 ||
          cache.nodeId.toLowerCase().indexOf(_filter) !== -1
        );
        console.debug('CacheMonitoringComponent filterCaches text', this.cacheFilter);
      }
    } else {
      this.filteredCaches = this.caches;
      console.debug('CacheMonitoringComponent filterCaches no filter');
    }

    if (this.filteredCaches === null) {
      this.filteredCaches = [];
    }

    let _total = this.filteredCaches.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
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

  protected getHitsAndMissed(row:CacheInfoVO):string {
    if (row.stats) {
      if (row.hits <= 0) {
        return '0/' + row.misses;
      }
      return row.hits + '/' + row.misses + ' (' + (row.misses > 0 ? Math.round(row.misses * 100 / row.hits) + '%' : '0%') + ')';
    }
    return '-';
  }

  protected getMemSize(row:CacheInfoVO):string {
    return row.inMemorySize + (row.stats ? this.getHumanReadableSize(row.calculateInMemorySize) : '');
  }

  protected getDiskSize(row:CacheInfoVO):string {
    return row.diskStoreSize + (row.stats ? this.getHumanReadableSize(row.calculateOnDiskSize) : '');
  }

  private getHumanReadableSize(bytes:number):string {
    if (bytes >= 0) {

      var _kb:number = 1024;
      var _mb:number = 1024 * _kb;
      var _gb:number = 1024 * _mb;

      var _bytes:number = bytes;
      var _out:string = '' + _bytes;
      if (_bytes >= _gb) {
        _out = '' + Math.round(_bytes / _gb) + '.' + Math.round((_bytes % _gb) / _mb / 100) + 'GB';
      } else if (_bytes >= _mb) {
        _out = '' + Math.round(_bytes / _mb) + '.' + Math.round((_bytes % _mb) / _kb / 100) + 'MB';
      } else if (_bytes >= _kb) {
        _out = '' + Math.round(_bytes / _kb) + '.' + Math.round((_bytes % _kb) / 100) + 'Kb';
      }

      return ' (' + _out + ')';

    }
    return '';
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onTopSelected() {
    this.cacheFilter = '^10';
    this.searchHelpShow = false;
    this.filterCaches();
  }

  protected onSizeSelected() {
    this.cacheFilter = '#100';
    this.searchHelpShow = false;
    this.filterCaches();
  }

}
