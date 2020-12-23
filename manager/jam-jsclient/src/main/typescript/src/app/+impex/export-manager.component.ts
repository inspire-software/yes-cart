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
import { TabsetComponent } from 'ngx-bootstrap/tabs';
import { DataGroupInfoVO, JobStatusVO, Pair } from './../shared/model/index';
import { ImpexService, ReportsService } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { Futures, Future } from './../shared/event/index';
import { Config } from './../../environments/environment';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'cw-export-manager',
  templateUrl: 'export-manager.component.html',
})

export class ExportManagerComponent implements OnInit {

  private static tabs:Array<ExportTabData> = [ ];

  public selectedGroup:DataGroupInfoVO = null;
  public fileFilter:string = null;
  public selectedFile:Pair<string,string> = null;

  public selectedTab:number = -1;
  public selectedTabRunning:boolean = false;
  public selectedTabCompleted:boolean = false;

  @ViewChild('selectGroupModalDialog')
  private selectGroupModalDialog:ModalComponent;

  @ViewChild('selectFileModalDialog')
  private selectFileModalDialog:ModalComponent;

  private delayedStatus:Future;
  private delayedStatusMs:number = Config.UI_BULKSERVICE_DELAY;

  @ViewChild('exportTabs')
  private exportTabs:TabsetComponent;

  /**
   * Construct export panel
   *
   * @param _exportService system service
   */
  constructor(private _exportService:ImpexService,
              private _reportsService:ReportsService) {
    LogUtil.debug('ExportManagerComponent constructed');
    let that = this;
    this.delayedStatus = Futures.perpetual(function() {
      that.getStatusInfo();
    }, this.delayedStatusMs);

  }

  get tabs():Array<ExportTabData> {
    return ExportManagerComponent.tabs;
  }

  set tabs(tabs:Array<ExportTabData>) {
    ExportManagerComponent.tabs = tabs;
  }

  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('ExportManagerComponent ngOnInit');
  }

  tabSelected(idx:number) {
    this.selectedTab = idx;
    let tab = this.tabs[idx];
    this.selectedTabRunning = tab.running;
    this.selectedTabCompleted = !tab.running && tab.status.completion != null;
  }

  onNewTabHandler() {
    LogUtil.debug('ExportManagerComponent onNewTabHandler');
    this.selectedGroup = null;
    this.selectGroupModalDialog.show();
  }

  onGroupSelect(group:DataGroupInfoVO) {
    LogUtil.debug('ExportManagerComponent onGroupSelect', group);
    this.selectedGroup = group;
  }

  onGroupConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ExportManagerComponent onGroupConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.selectedGroup != null) {
        let safeName = this.selectedGroup.name.replace(/[/\\?%*:|"<>\s]/g, '-');
        let safeExt = '.out';
        if (this.selectedGroup.name.toLowerCase().includes('csv')) {
          safeExt = '.csv';
        } else if (this.selectedGroup.name.toLowerCase().includes('xml')) {
          safeExt = '.xml';
        } else if (this.selectedGroup.name.toLowerCase().includes('zip')) {
          safeExt = '.zip';
        }
        this.tabs.push({
          group: this.selectedGroup,
          file: safeName + safeExt,
          status : { token: null, state: 'UNDEFINED', completion: null, report: null },
          running : false
        });

        let that = this;

        Futures.once(function () {
          if (that.exportTabs.tabs.length == that.tabs.length) {
            that.selectedTab = that.tabs.length - 1;
            if (!that.exportTabs.tabs[that.selectedTab].active) {
              that.exportTabs.tabs[that.selectedTab].active = true;
            }
          } else if (that.tabs.length == 1) {
            that.selectedTab = 0;
          }
        }, 50).delay();
      }
    }
  }

  onFileSelect(file:Pair<string, string>) {
    LogUtil.debug('ExportManagerComponent onFileSelect', file);
    this.selectedFile = file;
  }

  onFileDownload() {
    LogUtil.debug('ExportManagerComponent onFileSelect');
    if (this.selectedTab >= 0) {
      let data = this.tabs[this.selectedTab];
      if (data.status.completion == 'OK') {
        this.fileFilter = data.file;
        this.selectFileModalDialog.show();
      }
    } else {
      this.fileFilter = null;
      this.selectFileModalDialog.show();
    }
  }

  onFilesConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ExportManagerComponent onFilesConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.selectedFile != null) {

        this._reportsService.downloadReport(this.selectedFile.first).subscribe(res => {
          LogUtil.debug('ReportsComponent downloaded report', this.selectedFile, res);
        });

      }
    }
    this.selectedFile = null;
  }

  onTabDeleteTab(tab:ExportTabData) {
    if (tab != null && !tab.running) {
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


  onRunHandler() {
    LogUtil.debug('ExportManagerComponent Run handler');

    if (this.selectedTab >= 0) {
      let data = this.tabs[this.selectedTab];
      if (!data.running) {

        LogUtil.debug('ExportManagerComponent exportFromFile', data.group.label, data.file);

        this._exportService.exportToFile(data.group.label, data.file).subscribe(res => {

          LogUtil.debug('ExportManagerComponent exportToFile', res);

          data.status.token = res != null ? res.token : null;
          data.running = true;

          this.delayedStatus.delay();

        });

      }
    }
  }

  onRefreshHandler() {
    LogUtil.debug('ExportManagerComponent refresh handler');
    this.getStatusInfo();
  }

  /**
   * Read attributes.
   */
  private getStatusInfo() {
    LogUtil.debug('ExportManagerComponent status');

    this.tabs.forEach((tab:ExportTabData, idx:number) => {

      if (tab.status.token != null) {
        if (tab.status.completion == null) {

          this._exportService.getExportStatus(tab.status.token).subscribe(update => {

            LogUtil.debug('ExportManagerComponent getExportStatus', update);
            tab.status = update;
            tab.running = tab.status.completion == null;

            if (tab.running) {
              this.delayedStatus.delay();
            }

            if (this.selectedTab == idx) {
              this.selectedTabRunning = tab.running;
              this.selectedTabCompleted = !tab.running;
            }

          });

        } else if (this.selectedTab == idx) {
          this.selectedTabRunning = tab.running;
          this.selectedTabCompleted = !tab.running;
        }
      }
    });

  }

}

interface ExportTabData {

  group : DataGroupInfoVO;
  file : string;
  status : JobStatusVO;
  running : boolean;

}

