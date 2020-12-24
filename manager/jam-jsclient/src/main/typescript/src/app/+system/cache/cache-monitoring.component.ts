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
import { Component, OnInit } from '@angular/core';
import { CacheInfoVO } from './../../shared/model/index';
import { SystemService, ReportsService, UserEventBus } from './../../shared/services/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-cache-monitoring',
  templateUrl: 'cache-monitoring.component.html',
})

export class CacheMonitoringComponent implements OnInit {

  public searchHelpShow:boolean = false;

  private caches:Array<CacheInfoVO> = [];
  public filteredCaches:Array<CacheInfoVO> = [];
  public cacheFilter:string;

  public selectedRow:CacheInfoVO;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;


  //sorting
  public sortColumn:string = 'cacheName';
  public sortDesc:boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  public pageStart:number = 0;
  public pageEnd:number = this.itemsPerPage;

  public loading:boolean = false;

  /**
   * Construct shop attribute panel
   *
   * @param _systemService system service
   */
  constructor(private _systemService:SystemService,
              private _reportsService:ReportsService) {
    LogUtil.debug('CacheMonitoringComponent constructed');

  }

  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('CacheMonitoringComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterCaches();
    }, this.delayedFilteringMs);

  }


  onRowDeleteSelected() {
    if (this.selectedRow != null) {

      LogUtil.debug('CacheMonitoringComponent delete cache', this.selectedRow);

      this.loading = true;
      this._systemService.evictSingleCache(this.selectedRow.cacheName).subscribe(caches => {

        LogUtil.debug('CacheMonitoringComponent evictSingleCache', caches);
        this.caches = caches;
        this.selectedRow = null;
        this.filterCaches();
        this.loading = false;

      });

    } else {

      LogUtil.debug('CacheMonitoringComponent delete cache all');

      this.loading = true;
      this._systemService.evictAllCache().subscribe(caches => {

        LogUtil.debug('CacheMonitoringComponent evictAllCache', caches);
        this.caches = caches;
        this.selectedRow = null;
        this.filterCaches();
        this.loading = false;

      });

    }
  }

  onRowEditSelected() {
    if (this.selectedRow != null) {

      let cache = this.selectedRow.cacheName;
      let enabled = this.selectedRow.disabled;

      LogUtil.debug('CacheMonitoringComponent flag set: ' + enabled, this.selectedRow);

      this.loading = true;
      this._systemService.saveCacheFlag(cache, enabled).subscribe(caches => {

        LogUtil.debug('CacheMonitoringComponent saveCacheFlag', caches);
        this.caches = caches;
        this.selectedRow = null;
        this.filterCaches();
        this.loading = false;

      });

    }
  }

  onSelectRow(row:CacheInfoVO) {
    LogUtil.debug('CacheMonitoringComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  onSaveHandler() {
    LogUtil.debug('CacheMonitoringComponent Save handler');

    this._reportsService.downloadReportObject(this.caches, 'cache-info.csv').subscribe(res => {
      LogUtil.debug('CacheMonitoringComponent onDownloadHandler', res);
    });

  }

  onRefreshHandler() {
    LogUtil.debug('CacheMonitoringComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getCacheInfo();
    }
  }

  onFilterChange() {

    this.delayedFiltering.delay();

  }

  onClearFilter() {

    this.cacheFilter = '';
    this.onFilterChange();

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

  onSortClick(event:any) {
    if (event == this.sortColumn) {
      if (this.sortDesc) {  // same column already desc, remove sort
        this.sortColumn = 'cacheName';
        this.sortDesc = false;
      } else {  // same column asc, change to desc
        this.sortColumn = event;
        this.sortDesc = true;
      }
    } else { // different column, start asc sort
      this.sortColumn = event;
      this.sortDesc = false;
    }
    this.filterCaches();
  }

  getHitsAndMissed(row:CacheInfoVO):string {
    if (!row.disabled) {
      if (row.hits <= 0) {
        return '0/' + row.misses;
      }
      return row.hits + '/' + row.misses + ' (' + (row.misses > 0 ? Math.floor(row.misses * 100 / row.hits) + '%' : '0%') + ')';
    }
    return '-';
  }

  getMemSize(row:CacheInfoVO):string {
    return row.inMemorySize + (row.disabled ? '' : this.getHumanReadableSize(row.calculateInMemorySize));
  }

  getDiskSize(row:CacheInfoVO):string {
    if (row.diskStoreSize > 0 || row.overflowToDisk) {
      return row.diskStoreSize + (row.disabled ? '' : this.getHumanReadableSize(row.calculateOnDiskSize));
    }
    return '';
  }

  getTTL(row:CacheInfoVO):string {
    let _out:string = '';
    if (row.timeToLiveSeconds > 0) {
      _out += this.getHumanReadableSeconds(row.timeToLiveSeconds) + ' ttl ';
    }
    if (row.timeToIdleSeconds > 0) {
      _out += this.getHumanReadableSeconds(row.timeToIdleSeconds) + ' tti ';
    }
    return _out;
  }

  getHumanReadableSeconds(seconds:number):string {
    if (seconds >= 0) {

      let _sec:number = 1000;
      let _min:number = 60 * _sec;
      let _hour:number = 60 * _min;

      let _ms:number = seconds * 1000;
      let _out:string = '';
      if (_ms >= _hour) {
        _out += Math.floor(_ms / _hour) + 'h ';
      }
      if (_ms % _hour >= _min) {
        _out += Math.floor(_ms % _hour / _min) + 'm ';
      }

      if (_ms % _min >= _sec) {
        _out += Math.floor(_ms % _min / _sec) + 's ';
      }

      if (_ms % _sec > 0) {
        _out += (Math.floor(_ms % _sec * 100) / 100) + 'ms';
      }

      return _out;

    }
    return '-';
  }

  onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  onTopSelected() {
    this.cacheFilter = '^10';
    this.searchHelpShow = false;
    this.filterCaches();
  }

  onSizeSelected() {
    this.cacheFilter = '#100';
    this.searchHelpShow = false;
    this.filterCaches();
  }

  onHotSelected() {
    this.cacheFilter = '!10';
    this.searchHelpShow = false;
    this.filterCaches();
  }

  onHeavySelected() {
    this.cacheFilter = '$10';
    this.searchHelpShow = false;
    this.filterCaches();
  }


  private getCacheInfo() {
    LogUtil.debug('CacheMonitoringComponent get caches');

    this.loading = true;
    this._systemService.getCacheInfo().subscribe(caches => {

      LogUtil.debug('CacheMonitoringComponent attributes', caches);
      this.caches = caches;
      this.selectedRow = null;
      this.filterCaches();
      this.loading = false;

    });

  }

  private filterCaches() {

    let _sorted = false;

    if (this.caches) {
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
          LogUtil.debug('CacheMonitoringComponent filterCaches size', this.cacheFilter);
        } else if (_filter.indexOf('^') == 0) {

          let _top = parseInt(_filter.substr(1));
          if (isNaN(_top)) {
            _top = 5;
          }

          this.filteredCaches = this.caches.sort((n1, n2) => n2.cacheSize - n1.cacheSize).slice(0, _top);
          _sorted = true;

        } else if (_filter.indexOf('!') == 0) {

          let _hot = parseInt(_filter.substr(1));
          if (isNaN(_hot)) {
            _hot = 5;
          }

          this.filteredCaches = this.caches.sort((n1, n2) => (n2.cacheSize / n2.inMemorySizeMax) - (n1.cacheSize / n1.inMemorySizeMax)).slice(0, _hot);
          _sorted = true;

        } else if (_filter.indexOf('$') == 0) {

          let _heavy = parseInt(_filter.substr(1));
          if (isNaN(_heavy)) {
            _heavy = 5;
          }

          this.filteredCaches = this.caches.sort((n1, n2) => (n2.calculateInMemorySize - n1.calculateInMemorySize)).slice(0, _heavy);
          _sorted = true;

        } else {
          this.filteredCaches = this.caches.filter(cache =>
            cache.cacheName.toLowerCase().indexOf(_filter) !== -1 ||
            cache.nodeId.toLowerCase().indexOf(_filter) !== -1
          );
          LogUtil.debug('CacheMonitoringComponent filterCaches text', this.cacheFilter);
        }
      } else {
        this.filteredCaches = this.caches.slice(0, this.caches.length);
        LogUtil.debug('CacheMonitoringComponent filterCaches no filter');
      }
    }

    if (this.filteredCaches === null) {
      this.filteredCaches = [];
    }

    if (!_sorted) {
      let _sortProp = this.sortColumn;
      let _sortOrder = this.sortDesc ? -1 : 1;

      let _sort = function (a: any, b: any): number {
        return (a[_sortProp] > b[_sortProp] ? 1 : -1) * _sortOrder;
      };

      this.filteredCaches.sort(_sort);
    }

    let _total = this.filteredCaches.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }


  private getHumanReadableSize(bytes:number):string {
    if (bytes >= 0) {

      let _kb:number = 1024;
      let _mb:number = 1024 * _kb;
      let _gb:number = 1024 * _mb;

      let _bytes:number = bytes;
      let _out:string = '' + _bytes;
      if (_bytes >= _gb) {
        _out = '' + Math.floor(_bytes / _gb) + '.' + Math.floor((_bytes % _gb) / _mb / 100) + 'GB';
      } else if (_bytes >= _mb) {
        _out = '' + Math.floor(_bytes / _mb) + '.' + Math.floor((_bytes % _mb) / _kb / 100) + 'MB';
      } else if (_bytes >= _kb) {
        _out = '' + Math.floor(_bytes / _kb) + '.' + Math.floor((_bytes % _kb) / 100) + 'Kb';
      }

      return ' (' + _out + ')';

    }
    return '';
  }

}
