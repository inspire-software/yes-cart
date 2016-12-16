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
import { DataGroupInfoVO, JobStatusVO, Pair } from './../shared/model/index';
import { ImpexService } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-import-manager',
  moduleId: module.id,
  templateUrl: 'import-manager.component.html',
})

export class ImportManagerComponent implements OnInit {

  private static tabs:Array<ImportTabData> = [ ];

  private selectedGroup:DataGroupInfoVO = null;
  private selectedFile:Pair<string,string> = null;

  private selectedTab:number = -1;
  private selectedTabRunning:boolean = false;
  private selectedTabCompleted:boolean = false;

  @ViewChild('selectGroupModalDialog')
  private selectGroupModalDialog:ModalComponent;

  @ViewChild('selectFileModalDialog')
  private selectFileModalDialog:ModalComponent;

  private delayedStatus:Future;
  private delayedStatusMs:number = Config.UI_BULKSERVICE_DELAY;

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

  protected tabSelected(idx:number) {
    this.selectedTab = idx;
    let tab = this.tabs[idx];
    this.selectedTabRunning = tab.running;
    this.selectedTabCompleted = !tab.running && tab.status.completion != null;
  }

  protected onNewTabHandler() {
    LogUtil.debug('ImportManagerComponent onNewTabHandler');
    this.selectedGroup = null;
    this.selectGroupModalDialog.show();
  }

  protected onGroupSelect(group:DataGroupInfoVO) {
    LogUtil.debug('ImportManagerComponent onGroupSelect', group);
    this.selectedGroup = group;
  }

  protected onGroupConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ImportManagerComponent onGroupConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.selectedGroup != null) {
        this.selectedFile = null;
        this.selectFileModalDialog.show();
      }
    }
  }

  protected onFileSelect(file:Pair<string, string>) {
    LogUtil.debug('ImportManagerComponent onFileSelect', file);
    this.selectedFile = file;
  }

  protected onFilesConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ImportManagerComponent onFilesConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.selectedFile != null) {
        this.tabs.push({
          group: this.selectedGroup,
          file: this.selectedFile,
          status : { token: null, state: 'UNDEFINED', completion: null, report: null },
          running : false
        });
        if (this.selectedTab < 0) {
          this.selectedTab = 0;
        }
      }
    }
  }


  protected onTabDeleteSelected() {
    if (this.selectedTab >= 0) {
      let data = this.tabs[this.selectedTab];
      if (!data.running) {
        this.tabs.splice(this.selectedTab, 1);
        this.tabs = this.tabs.slice(0, this.tabs.length);
        if (this.tabs.length == 0) {
          this.selectedTab = -1;
        }
      }
    }
  }

  protected onRunHandler() {
    LogUtil.debug('ImportManagerComponent Run handler');

    if (this.selectedTab >= 0) {
      let data = this.tabs[this.selectedTab];
      if (!data.running) {

        LogUtil.debug('ImportManagerComponent importFromFile', data.group.label, data.file.first);

        var _sub:any = this._importService.importFromFile(data.group.label, data.file.first).subscribe(res => {

          LogUtil.debug('ImportManagerComponent importFromFile', res);

          data.status.token = res;
          data.running = true;

          _sub.unsubscribe();

          this.delayedStatus.delay();

        });

      }
    }
  }

  protected onRefreshHandler() {
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

        var _sub:any = this._importService.getImportStatus(tab.status.token).subscribe(update => {

          LogUtil.debug('ImportManagerComponent getImportStatus', update);
          tab.status = update;
          tab.running = tab.status.completion == null;
          _sub.unsubscribe();

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

