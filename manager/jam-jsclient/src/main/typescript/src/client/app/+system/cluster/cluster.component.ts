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
import {Component, OnInit, OnDestroy, OnChanges, Input, ViewChild} from '@angular/core';
import {NgIf, NgFor, CORE_DIRECTIVES } from '@angular/common';
import {REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {PaginationComponent} from './../../shared/pagination/index';
import {ClusterNodeVO} from './../../shared/model/index';
import {SystemService, ShopEventBus, Util} from './../../shared/services/index';
import {Futures, Future} from './../../shared/event/index';
import {Config} from './../../shared/config/env.config';

@Component({
  selector: 'yc-cluster',
  moduleId: module.id,
  templateUrl: 'cluster.component.html',
  directives: [PaginationComponent, REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES]
})

export class ClusterComponent implements OnInit {

  cluster:Array<ClusterNodeVO> = [];
  filteredCluster:Array<ClusterNodeVO> = [];
  clusterFilter:string;

  selectedRow:ClusterNodeVO;

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
    console.debug('ClusterComponent constructed');

  }

  /** {@inheritDoc} */
  public ngOnInit() {
    console.debug('ClusterComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterCluster();
    }, this.delayedFilteringMs);

  }


  protected onSelectRow(row:ClusterNodeVO) {
    console.debug('ClusterComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onSaveHandler() {
    console.debug('ClusterComponent Save handler');

    let myWindow = window.open("", "ExportClusterInfo", "width=800,height=600");

    var _csv:string = Util.toCsv(this.cluster, true);
    myWindow.document.write('<textarea style="width:100%; height:100%">' + _csv + '</textarea>');

  }

  protected onRefreshHandler() {
    console.debug('ClusterComponent refresh handler');
    this.getClusterInfo();
  }

  /**
   * Read attributes.
   */
  private getClusterInfo() {
    console.debug('ClusterComponent get cluster');

    var _sub:any = this._systemService.getClusterInfo().subscribe(cluster => {

      console.debug('ClusterComponent cluster', cluster);
      this.cluster = cluster;
      this.selectedRow = null;
      this.filterCluster();
      _sub.unsubscribe();

    });

  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

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
      console.debug('ClusterComponent filterCluster text', this.clusterFilter);

    } else {
      this.filteredCluster = this.cluster;
      console.debug('ClusterComponent filterCluster no filter');
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

}
