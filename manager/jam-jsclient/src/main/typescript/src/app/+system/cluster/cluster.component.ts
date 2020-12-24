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
import { ClusterNodeVO, ModuleVO } from './../../shared/model/index';
import { SystemService, ReportsService, UserEventBus } from './../../shared/services/index';
import { ModalComponent } from './../../shared/modal/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-cluster',
  templateUrl: 'cluster.component.html',
})

export class ClusterComponent implements OnInit {

  private cluster:Array<ClusterNodeVO> = [];
  public filteredCluster:Array<ClusterNodeVO> = [];
  public clusterFilter:string;

  public selectedRow:ClusterNodeVO;
  private selectedRowModules:Array<ModuleVO> = [];
  public filteredRowModules:Array<ModuleVO> = [];
  private duplicateModules:Array<ModuleVO> = [];
  public moduleFilter : string;

  @ViewChild('featuresModalDialog')
  private featuresModalDialog:ModalComponent;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private delayedModuleFiltering:Future;
  private delayedModuleFilteringMs:number = Config.UI_INPUT_DELAY;

  //sorting
  public sortColumn:string = 'id';
  public sortDesc:boolean = false;

  //paging
  public maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  public itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  public totalItems:number = 0;
  public currentPage:number = 1; // tslint:disable-line:no-unused-variable
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
    LogUtil.debug('ClusterComponent constructed');

  }

  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('ClusterComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterCluster();
    }, this.delayedFilteringMs);
    this.delayedModuleFiltering = Futures.perpetual(function() {
      that.filterModules();
    }, this.delayedModuleFilteringMs);

  }


  onSelectRow(row:ClusterNodeVO) {
    LogUtil.debug('ClusterComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }


  onRowInfoSelected() {
    if (this.selectedRow != null) {

      LogUtil.debug('ClusterComponent get node info');

      this.loading = true;
      this._systemService.getModuleInfo(this.selectedRow.id).subscribe(modules => {

        LogUtil.debug('ClusterComponent node info', this.selectedRow.id, modules);
        if (modules != null) {
          modules.sort((a:ModuleVO, b:ModuleVO) => {
            return (a.loaded < b.loaded) ? -1 : 1;
          });
        }
        this.selectedRowModules = modules;
        this.filterModules();
        this.checkDuplicates();
        this.loading = false;

        this.featuresModalDialog.show();

      });

    }
  }


  onSaveHandler() {
    LogUtil.debug('ClusterComponent Save handler');

    this._reportsService.downloadReportObject(this.cluster, 'cluster-info.csv').subscribe(res => {
      LogUtil.debug('SensorsMonitoringComponent onDownloadHandler', res);
    });

  }

  onRefreshHandler() {
    LogUtil.debug('ClusterComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getClusterInfo();
    }
  }

  onModuleFilterChange() {

    this.delayedModuleFiltering.delay();

  }

  onFilterChange() {

    this.delayedFiltering.delay();

  }

  onClearFilter() {

    this.clusterFilter = '';
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
        this.sortColumn = 'id';
        this.sortDesc = false;
      } else {  // same column asc, change to desc
        this.sortColumn = event;
        this.sortDesc = true;
      }
    } else { // different column, start asc sort
      this.sortColumn = event;
      this.sortDesc = false;
    }
    this.filterCluster();
  }

  private getClusterInfo() {
    LogUtil.debug('ClusterComponent get cluster');

    this.loading = true;
    this._systemService.getClusterInfo().subscribe(cluster => {

      LogUtil.debug('ClusterComponent cluster', cluster);
      this.cluster = cluster;
      this.selectedRow = null;
      this.filterCluster();
      this.loading = false;

    });

  }

  private checkDuplicates() {

    this.duplicateModules = [];
    if (this.selectedRow != null && this.filteredRowModules != null && this.selectedRowModules.length > 0) {
      let _uniqueModules:string[] = [];
      this.selectedRowModules.forEach(module => {
        let _key = module.name + (module.subName ? (':' + module.subName) : '');
        if (_uniqueModules.includes(_key)) {
          this.duplicateModules.push(module);
        } else {
          _uniqueModules.push(_key);
        }
      });
    }
    LogUtil.debug('ClusterComponent checkDuplicates', this.duplicateModules);

  }

  private filterModules() {
    if (this.selectedRow != null && this.filteredRowModules != null && this.selectedRowModules.length > 0) {
      if (this.moduleFilter) {
        let _filter = this.moduleFilter.toLowerCase();

        this.filteredRowModules = this.selectedRowModules.filter(module =>
          module.functionalArea.toLowerCase().indexOf(_filter) !== -1 ||
          module.name.toLowerCase().indexOf(_filter) !== -1 ||
          (module.subName != null && module.subName.toLowerCase().indexOf(_filter) !== -1)
        );
      } else {
        this.filteredRowModules = this.selectedRowModules;
      }
    } else {
      this.filteredRowModules = [];
    }
    LogUtil.debug('ClusterComponent filterModules text', this.moduleFilter, this.filteredRowModules);
  }


  private filterCluster() {

    if (this.cluster) {
      if (this.clusterFilter) {

        let _filter = this.clusterFilter.toLowerCase();

        this.filteredCluster = this.cluster.filter(cluster =>
          cluster.id.toLowerCase().indexOf(_filter) !== -1 ||
          cluster.nodeId.toLowerCase().indexOf(_filter) !== -1 ||
          cluster.nodeType.toLowerCase().indexOf(_filter) !== -1 ||
          cluster.nodeConfig.toLowerCase().indexOf(_filter) !== -1 ||
          cluster.clusterId.toLowerCase().indexOf(_filter) !== -1 ||
          cluster.channel.toLowerCase().indexOf(_filter) !== -1
        );
        LogUtil.debug('ClusterComponent filterCluster text', this.clusterFilter);

      } else {
        this.filteredCluster = this.cluster.slice(0, this.cluster.length);
        LogUtil.debug('ClusterComponent filterCluster no filter');
      }
    }

    if (this.filteredCluster === null) {
      this.filteredCluster = [];
    }

    let _sortProp = this.sortColumn;
    let _sortOrder = this.sortDesc ? -1 : 1;

    let _sort = function(a:any, b:any):number {
      return (a[_sortProp] > b[_sortProp] ? 1 : -1) * _sortOrder;
    };

    this.filteredCluster.sort(_sort);

    let _total = this.filteredCluster.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
