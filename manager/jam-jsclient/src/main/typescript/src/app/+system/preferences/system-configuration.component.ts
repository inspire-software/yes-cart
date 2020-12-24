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
import { Component, OnInit, ViewChild } from '@angular/core';
import { ConfigurationVO } from './../../shared/model/index';
import { SystemService, ReportsService, UserEventBus } from './../../shared/services/index';
import { ModalComponent } from './../../shared/modal/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-system-configuration',
  templateUrl: 'system-configuration.component.html',
})

export class SystemConfigurationComponent implements OnInit {

  private configurations:Array<ConfigurationVO> = [];
  public filteredConfigurations:Array<ConfigurationVO> = [];
  public configurationFilter:string;

  public selectedRow:ConfigurationVO;

  @ViewChild('featuresModalDialog')
  private featuresModalDialog:ModalComponent;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  //sorting
  public sortColumn:string = 'nodeId';
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
    LogUtil.debug('SystemConfigurationComponent constructed');

  }

  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('SystemConfigurationComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterConfigurations();
    }, this.delayedFilteringMs);

  }

  onReloadHandler() {
    this.reloadConfigurations();
  }

  onSelectRow(row:ConfigurationVO) {
    LogUtil.debug('SystemConfigurationComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }


  onRowInfoSelected() {
    if (this.selectedRow != null) {

      LogUtil.debug('SystemConfigurationComponent get node info');

      this.featuresModalDialog.show();

    }
  }


  onSaveHandler() {
    LogUtil.debug('SystemConfigurationComponent Save handler');

    this._reportsService.downloadReportObject(this.configurations, 'system-config-info.csv').subscribe(res => {
      LogUtil.debug('SystemConfigurationComponent onDownloadHandler', res);
    });

  }

  onRefreshHandler() {
    LogUtil.debug('SystemConfigurationComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getConfigurationInfo();
    }
  }

  onFilterChange() {

    this.delayedFiltering.delay();

  }

  onClearFilter() {

    this.configurationFilter = '';
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
        this.sortColumn = 'nodeId';
        this.sortDesc = false;
      } else {  // same column asc, change to desc
        this.sortColumn = event;
        this.sortDesc = true;
      }
    } else { // different column, start asc sort
      this.sortColumn = event;
      this.sortDesc = false;
    }
    this.filterConfigurations();
  }

  getProperties(row:ConfigurationVO):string {
    if (row.properties != null) {
      let out:string = '';
      row.properties.forEach(prop => {
        out += '<b>' + prop.first + '</b>: ' + prop.second + '\n';
      });
      return out;
    }
    return '';
  }

  getTargets(row:ConfigurationVO):string[] {
    if (row.targets != null) {
      return row.targets;
    }
    return [];
  }

  private reloadConfigurations() {
    LogUtil.debug('SystemConfigurationComponent reload configurations');

    this.loading = true;
    this._systemService.reloadConfigurations().subscribe(cluster => {

      LogUtil.debug('SystemConfigurationComponent cluster', cluster);
      this.selectedRow = null;
      this.getConfigurationInfo();
      this.loading = false;

    });

  }


  private getConfigurationInfo() {
    LogUtil.debug('SystemConfigurationComponent get configurations');

    this.loading = true;
    this._systemService.getConfigurationInfo().subscribe(configurations => {

      LogUtil.debug('SystemConfigurationComponent attributes', configurations);
      this.configurations = configurations;
      this.selectedRow = null;
      this.filterConfigurations();
      this.loading = false;

    });

  }

  private filterConfigurations() {

    if (this.configurations) {
      if (this.configurationFilter) {

        let _filter = this.configurationFilter.toLowerCase();

        this.filteredConfigurations = this.configurations.filter(configuration =>
          configuration.functionalArea.toLowerCase().indexOf(_filter) !== -1 ||
          configuration.name.toLowerCase().indexOf(_filter) !== -1 ||
          configuration.cfgInterface.toLowerCase().indexOf(_filter) !== -1 ||
          configuration.nodeId.toLowerCase().indexOf(_filter) !== -1 ||
          (configuration.targets != null && configuration.targets.findIndex(el => el.toLowerCase().indexOf(_filter) !== -1) !== -1) ||
          (configuration.properties != null && configuration.properties.findIndex(el => el.first.toLowerCase().indexOf(_filter) !== -1 || el.second.toLowerCase().indexOf(_filter) !== -1) !== -1)
        );
        LogUtil.debug('SystemConfigurationComponent filterConfigurations text', this.configurationFilter, this.filteredConfigurations);

      } else {
        this.filteredConfigurations = this.configurations.slice(0, this.configurations.length);
        LogUtil.debug('SystemConfigurationComponent filterConfigurations no filter', this.filteredConfigurations);
      }
    }

    if (this.filteredConfigurations === null) {
      this.filteredConfigurations = [];
    }

    let _sortProp = this.sortColumn;
    let _sortOrder = this.sortDesc ? -1 : 1;

    let _sort = function(a:any, b:any):number {
      return (a[_sortProp] > b[_sortProp] ? 1 : -1) * _sortOrder;
    };

    this.filteredConfigurations.sort(_sort);

    let _total = this.filteredConfigurations.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
