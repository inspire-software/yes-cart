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
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {DataGroupInfoVO, JobStatusVO, Pair} from './../shared/model/index';
import {ImpexService, Util, I18nEventBus} from './../shared/services/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {FileSelectComponent, DataGroupSelectComponent} from './../shared/impex/index';
import {Futures, Future} from './../shared/event/index';
import {Config} from './../shared/config/env.config';

@Component({
  selector: 'yc-import-manager',
  moduleId: module.id,
  templateUrl: 'import-manager.component.html',
  directives: [TAB_DIRECTIVES, REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES, FileSelectComponent, DataGroupSelectComponent, ModalComponent]
})

export class ImportManagerComponent implements OnInit {

  selectedGroup:DataGroupInfoVO = null;
  selectedFile:Pair<string,string> = null;

  static tabs:Array<ImportTabData> = [ ];
  selectedTab:number = -1;
  selectedTabRunning:boolean = false;
  selectedTabCompleted:boolean = false;

  @ViewChild('selectGroupModalDialog')
  selectGroupModalDialog:ModalComponent;

  @ViewChild('selectFileModalDialog')
  selectFileModalDialog:ModalComponent;

  delayedStatus:Future;
  delayedStatusMs:number = Config.UI_BULKSERVICE_DELAY;

  /**
   * Construct import panel
   *
   * @param _importService system service
   */
  constructor(private _importService:ImpexService) {
    console.debug('ImportManagerComponent constructed');
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
    console.debug('ImportManagerComponent ngOnInit');
  }

  protected tabSelected(idx:number) {
    this.selectedTab = idx;
    let tab = this.tabs[idx];
    this.selectedTabRunning = tab.running;
    this.selectedTabCompleted = !tab.running && tab.status.completion != null;
  }

  protected onNewTabHandler() {
    console.debug('ImportManagerComponent onNewTabHandler');
    this.selectedGroup = null;
    this.selectGroupModalDialog.show();
  }

  protected onGroupSelect(group:DataGroupInfoVO) {
    console.debug('ImportManagerComponent onGroupSelect', group);
    this.selectedGroup = group;
  }

  protected onGroupConfirmationResult(modalresult: ModalResult) {
    console.debug('ImportManagerComponent onGroupConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.selectedGroup != null) {
        this.selectedFile = null;
        this.selectFileModalDialog.show();
      }
    }
  }

  protected onFileSelect(file:Pair<string, string>) {
    console.debug('ImportManagerComponent onFileSelect', file);
    this.selectedFile = file;
  }

  protected onFilesConfirmationResult(modalresult: ModalResult) {
    console.debug('ImportManagerComponent onFilesConfirmationResult modal result is ', modalresult);
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
    console.debug('ImportManagerComponent Run handler');

    if (this.selectedTab >= 0) {
      let data = this.tabs[this.selectedTab];
      if (!data.running) {

        console.debug('ImportManagerComponent importFromFile', data.group.label, data.file.first);

        var _sub:any = this._importService.importFromFile(data.group.label, data.file.first).subscribe(res => {

          console.debug('ImportManagerComponent importFromFile', res);

          data.status.token = res;
          data.running = true;

          _sub.unsubscribe();

          this.delayedStatus.delay();

        });

      }
    }
  }

  protected onRefreshHandler() {
    console.debug('ImportManagerComponent refresh handler');
    this.getStatusInfo();
  }

  /**
   * Read attributes.
   */
  private getStatusInfo() {
    console.debug('ImportManagerComponent status');

    this.tabs.forEach((tab:ImportTabData, idx:number) => {

      if (tab.status.completion == null) {

        var _sub:any = this._importService.getImportStatus(tab.status.token).subscribe(update => {

          console.debug('ImportManagerComponent getImportStatus', update);
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

