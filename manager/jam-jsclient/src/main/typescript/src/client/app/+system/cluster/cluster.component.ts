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
import { Component, OnInit, ViewChild } from '@angular/core';
import { ClusterNodeVO, ModuleVO } from './../../shared/model/index';
import { SystemService, Util } from './../../shared/services/index';
import { ModalComponent } from './../../shared/modal/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-cluster',
  moduleId: module.id,
  templateUrl: 'cluster.component.html',
})

export class ClusterComponent implements OnInit {

  private cluster:Array<ClusterNodeVO> = [];
  private filteredCluster:Array<ClusterNodeVO> = [];
  private clusterFilter:string;

  private selectedRow:ClusterNodeVO;
  private selectedRowModules:Array<ModuleVO> = [];
  private filteredRowModules:Array<ModuleVO> = [];
  private duplicateModules:Array<ModuleVO> = [];
  private moduleFilter : string;

  @ViewChild('featuresModalDialog')
  private featuresModalDialog:ModalComponent;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private delayedModuleFiltering:Future;
  private delayedModuleFilteringMs:number = Config.UI_INPUT_DELAY;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  private pageStart:number = 0;
  private pageEnd:number = this.itemsPerPage;

  private loading:boolean = false;

  /**
   * Construct shop attribute panel
   *
   * @param _systemService system service
   */
  constructor(private _systemService:SystemService) {
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


  protected onSelectRow(row:ClusterNodeVO) {
    LogUtil.debug('ClusterComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }


  protected onRowInfoSelected() {
    if (this.selectedRow != null) {

      LogUtil.debug('ClusterComponent get node info');

      this.loading = true;
      let _sub:any = this._systemService.getModuleInfo(this.selectedRow.id).subscribe(modules => {

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
        _sub.unsubscribe();

        this.featuresModalDialog.show();

      });

    }
  }


  protected onSaveHandler() {
    LogUtil.debug('ClusterComponent Save handler');

    let myWindow = window.open('', 'ExportClusterInfo', 'width=800,height=600');

    let _csv:string = Util.toCsv(this.cluster, true);
    myWindow.document.write('<textarea style="width:100%; height:100%">' + _csv + '</textarea>');

  }

  protected onRefreshHandler() {
    LogUtil.debug('ClusterComponent refresh handler');
    this.getClusterInfo();
  }

  protected onModuleFilterChange() {

    this.delayedModuleFiltering.delay();

  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  protected onClearFilter() {

    this.clusterFilter = '';
    this.onFilterChange();

  }

  protected resetLastPageEnd() {
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onPageChanged(event:any) {
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  private getClusterInfo() {
    LogUtil.debug('ClusterComponent get cluster');

    this.loading = true;
    let _sub:any = this._systemService.getClusterInfo().subscribe(cluster => {

      LogUtil.debug('ClusterComponent cluster', cluster);
      this.cluster = cluster;
      this.selectedRow = null;
      this.filterCluster();
      this.loading = false;
      _sub.unsubscribe();

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
      this.filteredCluster = this.cluster;
      LogUtil.debug('ClusterComponent filterCluster no filter');
    }

    if (this.filteredCluster === null) {
      this.filteredCluster = [];
    }

    let _total = this.filteredCluster.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
