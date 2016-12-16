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
import { Component, OnInit } from '@angular/core';
import { ClusterNodeVO } from './../../shared/model/index';
import { SystemService } from './../../shared/services/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-query',
  moduleId: module.id,
  templateUrl: 'query.component.html',
})

export class QueryComponent implements OnInit {

  private static tabs:Array<QueryTabData> = [ { query: '', qtype: 'SQL', result: '', resultQuery: '' } ];

  private cluster:Array<ClusterNodeVO> = [];

  private selectedNode:string = null;
  private selectedTabType:string = 'SQL';

  private selectedTab:number = 0;

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

    let qtype = this.selectedTabType ? this.selectedTabType : 'SQL';

    this.tabs.push({ query: '', qtype: qtype, result: '', resultQuery: '' });
  }

  protected onTabDeleteSelected() {
    this.tabs.splice(this.selectedTab, 1);
    this.tabs = this.tabs.slice(0, this.tabs.length);
  }

  protected onRunHandler() {
    LogUtil.debug('QueryComponent Run handler');

    let data = this.tabs[this.selectedTab];
    let node = this.selectedNode;

    var _sub:any = this._systemService.runQuery(node, data.qtype, data.query).subscribe(res => {

      LogUtil.debug('QueryComponent res', res);

      var _res:Array<Array<string>> = res;

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
      _sub.unsubscribe();

    });

  }

  protected onRefreshHandler() {
    LogUtil.debug('QueryComponent refresh handler');
    this.getClusterInfo();
  }

  private getClusterInfo() {
    LogUtil.debug('QueryComponent get cluster');

    var _sub:any = this._systemService.getClusterInfo().subscribe(cluster => {

      LogUtil.debug('QueryComponent cluster', cluster);
      this.cluster = cluster;
      this.selectedNode = this.cluster[0].id;
      _sub.unsubscribe();

    });

  }

}

interface QueryTabData {

  query : string;
  qtype : string;
  result : string;
  resultQuery : string;

}

