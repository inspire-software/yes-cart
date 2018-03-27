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
import { ConfigurationVO } from './../../shared/model/index';
import { SystemService, Util } from './../../shared/services/index';
import { ModalComponent } from './../../shared/modal/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-system-configuration',
  moduleId: module.id,
  templateUrl: 'system-configuration.component.html',
})

export class SystemConfigurationComponent implements OnInit {

  private configurations:Array<ConfigurationVO> = [];
  private filteredConfigurations:Array<ConfigurationVO> = [];
  private configurationFilter:string;

  private selectedRow:ConfigurationVO;

  @ViewChild('featuresModalDialog')
  private featuresModalDialog:ModalComponent;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

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

  protected onReloadHandler() {
    this.reloadConfigurations();
  }

  protected onSelectRow(row:ConfigurationVO) {
    LogUtil.debug('SystemConfigurationComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }


  protected onRowInfoSelected() {
    if (this.selectedRow != null) {

      LogUtil.debug('SystemConfigurationComponent get node info');

      this.featuresModalDialog.show();

    }
  }


  protected onSaveHandler() {
    LogUtil.debug('SystemConfigurationComponent Save handler');

    let myWindow = window.open('', 'ExportCacheInfo', 'width=800,height=600');

    let _csv:string = Util.toCsv(this.configurations, true);
    myWindow.document.write('<textarea style="width:100%; height:100%">' + _csv + '</textarea>');

  }

  protected onRefreshHandler() {
    LogUtil.debug('SystemConfigurationComponent refresh handler');
    this.getConfigurationInfo();
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  protected onClearFilter() {

    this.configurationFilter = '';
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

  protected getProperties(row:ConfigurationVO):string {
    if (row.properties != null) {
      let out:string = '';
      row.properties.forEach(prop => {
         out += prop.first + ': ' + prop.second + '\n';
      });
      return out;
    }
    return '';
  }

  protected getTargets(row:ConfigurationVO):string[] {
    if (row.targets != null) {
      return row.targets;
    }
    return [];
  }

  private reloadConfigurations() {
    LogUtil.debug('SystemConfigurationComponent reload configurations');

    this.loading = true;
    let _sub:any = this._systemService.reloadConfigurations().subscribe(cluster => {

      LogUtil.debug('SystemConfigurationComponent cluster', cluster);
      this.selectedRow = null;
      this.getConfigurationInfo();
      this.loading = false;
      _sub.unsubscribe();

    });

  }


  private getConfigurationInfo() {
    LogUtil.debug('SystemConfigurationComponent get configurations');

    this.loading = true;
    let _sub:any = this._systemService.getConfigurationInfo().subscribe(configurations => {

      LogUtil.debug('SystemConfigurationComponent attributes', configurations);
      this.configurations = configurations;
      this.selectedRow = null;
      this.filterConfigurations();
      this.loading = false;
      _sub.unsubscribe();

    });

  }

  private filterConfigurations() {
    if (this.configurationFilter) {

      let _filter = this.configurationFilter.toLowerCase();

      this.filteredConfigurations = this.configurations.filter(configuration =>
        configuration.functionalArea.toLowerCase().indexOf(_filter) !== -1 ||
        configuration.name.toLowerCase().indexOf(_filter) !== -1 ||
        configuration.cfgInterface.toLowerCase().indexOf(_filter) !== -1 ||
        configuration.nodeId.toLowerCase().indexOf(_filter) !== -1 ||
        (configuration.targets != null && configuration.targets.findIndex(el => el.toLowerCase().indexOf(_filter) !== -1) !== -1)  ||
        (configuration.properties != null && configuration.properties.findIndex(el => el.first.toLowerCase().indexOf(_filter) !== -1 || el.second.toLowerCase().indexOf(_filter) !== -1) !== -1)
      );
      LogUtil.debug('SystemConfigurationComponent filterConfigurations text', this.configurationFilter, this.filteredConfigurations);

    } else {
      this.filteredConfigurations = this.configurations;
      LogUtil.debug('SystemConfigurationComponent filterConfigurations no filter', this.filteredConfigurations);
    }

    if (this.filteredConfigurations === null) {
      this.filteredConfigurations = [];
    }

    let _total = this.filteredConfigurations.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

}
