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
import { TabsetComponent } from 'ngx-bootstrap';
import { ClusterNodeVO, Pair } from './../../shared/model/index';
import { SystemService, UserEventBus } from './../../shared/services/index';
import { Futures } from './../../shared/event/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-query',
  moduleId: module.id,
  templateUrl: 'query.component.html',
})

export class QueryComponent implements OnInit {

  private static tabs:Array<QueryTabData> = [ { query: '', qtype: 'sql-core', result: '', resultQuery: '' } ];

  private cluster:Array<ClusterNodeVO> = [];
  private supportedQueries:Array<Pair<string, Array<string>>> = [];

  private selectedNode:string = null;
  private selectedTabType:string = 'sql-core';
  private selectedNodeQueries:Array<string> = [];

  private selectedTab:number = 0;

  private loading:boolean = false;

  @ViewChild('queryTabs')
  private queryTabs:TabsetComponent;

  /**
   * Construct shop attribute panel
   *
   * @param _systemService system service
   */
  constructor(private _systemService:SystemService) {
    LogUtil.debug('QueryComponent constructed');

  }

  get tabs():Array<QueryTabData> {
    return QueryComponent.tabs;
  }

  set tabs(tabs:Array<QueryTabData>) {
    QueryComponent.tabs = tabs;
  }

  ngOnInit() {
    LogUtil.debug('QueryComponent ngOnInit');
    this.onRefreshHandler();
  }

  protected tabSelected(idx:number) {
    this.selectedTab = idx;
  }

  protected onNewTabHandler() {

    let qtype = this.selectedTabType ? this.selectedTabType : 'sql-core';
    this.tabs.push({
      query: '',
      qtype: qtype,
      result: '',
      resultQuery: ''
    });

    let that = this;

    Futures.once(function () {
      if (that.queryTabs.tabs.length == that.tabs.length) {
        that.selectedTab = that.tabs.length - 1;
        if (!that.queryTabs.tabs[that.selectedTab].active) {
          that.queryTabs.tabs[that.selectedTab].active = true;
        }
      } else if (that.tabs.length == 1) {
        that.selectedTab = 0;
      }
    }, 50).delay();

  }

  protected onTabDeleteTab(tab:QueryTabData) {
    if (tab != null) {
      let idx = this.tabs.indexOf(tab);
      if (idx != -1) {
        let wasActive = this.selectedTab == idx;
        this.tabs.splice(idx, 1);
        this.tabs = this.tabs.slice(0, this.tabs.length);
        if (wasActive) {
          this.selectedTab = -1;
        }
      }
    }
  }

  protected onRunHandler() {
    LogUtil.debug('QueryComponent Run handler');

    let data = this.tabs[this.selectedTab];
    let node = this.selectedNode;

    this.loading = true;
    let _sub:any = this._systemService.runQuery(node, data.qtype, data.query).subscribe(res => {

      LogUtil.debug('QueryComponent res', res);

      let _res:Array<Array<string>> = res;

      let _csv = '';
      _res.forEach(line => {
        line.forEach(col => {
          _csv += col + ',';
        });
        _csv += '\n';
      });

      let data = this.tabs[this.selectedTab];
      data.result = _csv;
      data.resultQuery = data.query;
      this.loading = false;
      _sub.unsubscribe();

    });

  }

  protected onRefreshHandler() {
    LogUtil.debug('QueryComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getClusterInfo();
    }
  }

  private getClusterInfo() {
    LogUtil.debug('QueryComponent get cluster');

    this.loading = true;
    let _sub:any = this._systemService.getClusterInfo().subscribe(cluster => {

      LogUtil.debug('QueryComponent cluster', cluster);
      this.cluster = cluster;
      this.selectedNode = this.cluster[0].id;
      _sub.unsubscribe();

      let _sub2:any = this._systemService.supportedQueries().subscribe(supportedQueries => {

        LogUtil.debug('QueryComponent supportedQueries', supportedQueries);

        this.supportedQueries = supportedQueries;
        this.onSelectedNodeChange(this.selectedNode);

        this.loading = false;
        _sub2.unsubscribe();

      });

    });

  }

  private onSelectedNodeChange(ev:any) {

    LogUtil.debug('QueryComponent onSelectedNodeChange', this.selectedNode);

    let idx = this.supportedQueries.findIndex(pair => {
      return pair.first == this.selectedNode;
    });

    if (idx != -1) {
      this.selectedNodeQueries = this.supportedQueries[idx].second;
    } else {
      this.selectedNodeQueries = [];
    }

  }


}

interface QueryTabData {

  query : string;
  qtype : string;
  result : string;
  resultQuery : string;

}

