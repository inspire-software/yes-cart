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
import { ImpexService } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { Futures, Future } from './../shared/event/index';
import { Config } from './../../environments/environment';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'cw-import-manager',
  templateUrl: 'import-manager.component.html',
})

export class ImportManagerComponent implements OnInit {

  private static tabs:Array<ImportTabData> = [ ];

  public selectedGroup:DataGroupInfoVO = null;
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

  @ViewChild('importTabs')
  private importTabs:TabsetComponent;

  /**
   * Construct import panel
   *
   * @param _importService system service
   */
  constructor(private _importService:ImpexService) {
    LogUtil.debug('ImportManagerComponent constructed');
    let that = this;
    this.delayedStatus = Futures.perpetual(function() {
      that.getStatusInfo();
    }, this.delayedStatusMs);

  }

  get tabs():Array<ImportTabData> {
    return ImportManagerComponent.tabs;
  }

  set tabs(tabs:Array<ImportTabData>) {
    ImportManagerComponent.tabs = tabs;
  }

  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('ImportManagerComponent ngOnInit');
  }

  tabSelected(idx:number) {
    this.selectedTab = idx;
    let tab = this.tabs[idx];
    this.selectedTabRunning = tab.running;
    this.selectedTabCompleted = !tab.running && tab.status.completion != null;
  }

  onNewTabHandler() {
    LogUtil.debug('ImportManagerComponent onNewTabHandler');
    this.selectedGroup = null;
    this.selectGroupModalDialog.show();
  }

  onGroupSelect(group:DataGroupInfoVO) {
    LogUtil.debug('ImportManagerComponent onGroupSelect', group);
    this.selectedGroup = group;
  }

  onGroupConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ImportManagerComponent onGroupConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.selectedGroup != null) {
        this.selectedFile = null;
        this.selectFileModalDialog.show();
      }
    }
  }

  onFileSelect(file:Pair<string, string>) {
    LogUtil.debug('ImportManagerComponent onFileSelect', file);
    this.selectedFile = file;
  }

  onFilesConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ImportManagerComponent onFilesConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.selectedFile != null) {
        this.tabs.push({
          group: this.selectedGroup,
          file: this.selectedFile,
          status : { token: null, state: 'UNDEFINED', completion: null, report: null },
          running : false
        });

        let that = this;

        Futures.once(function () {
          if (that.importTabs.tabs.length == that.tabs.length) {
            that.selectedTab = that.tabs.length - 1;
            if (!that.importTabs.tabs[that.selectedTab].active) {
              that.importTabs.tabs[that.selectedTab].active = true;
            }
          } else if (that.tabs.length == 1) {
            that.selectedTab = 0;
          }
        }, 50).delay();

      }
    }
  }


  onTabDeleteTab(tab:ImportTabData) {
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
    LogUtil.debug('ImportManagerComponent Run handler');

    if (this.selectedTab >= 0) {
      let data = this.tabs[this.selectedTab];
      if (!data.running) {

        LogUtil.debug('ImportManagerComponent importFromFile', data.group.label, data.file.first);

        this._importService.importFromFile(data.group.label, data.file.first).subscribe(res => {

          LogUtil.debug('ImportManagerComponent importFromFile', res);

          data.status.token = res != null ? res.token : null;
          data.running = true;

          this.delayedStatus.delay();

        });

      }
    }
  }

  onRefreshHandler() {
    LogUtil.debug('ImportManagerComponent refresh handler');
    this.getStatusInfo();
  }

  /**
   * Read attributes.
   */
  private getStatusInfo() {
    LogUtil.debug('ImportManagerComponent status');

    this.tabs.forEach((tab:ImportTabData, idx:number) => {

      if (tab.status.completion == null) {

        this._importService.getImportStatus(tab.status.token).subscribe(update => {

          LogUtil.debug('ImportManagerComponent getImportStatus', update);
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

    });

  }

}

interface ImportTabData {

  group : DataGroupInfoVO;
  file : Pair<string, string>;
  status : JobStatusVO;
  running : boolean;

}

