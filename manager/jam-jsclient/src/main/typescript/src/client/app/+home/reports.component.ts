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
import { ReportDescriptorVO, ReportRequestVO, ReportRequestParameterVO, Pair } from './../shared/model/index';
import { ReportsService, I18nEventBus, UserEventBus } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { Futures } from './../shared/event/index';
import { LogUtil } from './../shared/log/index';
import { Config } from './../shared/config/env.config';


@Component({
  selector: 'yc-reports',
  moduleId: module.id,
  templateUrl: 'reports.component.html',
})

export class ReportsComponent implements OnInit {

  private static tabs:Array<QueryTabData> = [];

  private reports:Array<ReportDescriptorVO> = [];

  private runnableReportTab:boolean = false;
  private selectedReport:string = null;

  private selectedTab:number = 0;

  private fileFilter:string = null;
  private selectedFile:Pair<string,string> = null;

  @ViewChild('selectFileModalDialog')
  private selectFileModalDialog:ModalComponent;

  @ViewChild('reportTabs')
  private reportTabs:TabsetComponent;

  /**
   * Construct reports panel
   *
   * @param _reportsService reports service
   */
  constructor(private _reportsService:ReportsService) {
    LogUtil.debug('ReportsComponent constructed');

  }

  get tabs():Array<QueryTabData> {
    return ReportsComponent.tabs;
  }

  set tabs(tabs:Array<QueryTabData>) {
    ReportsComponent.tabs = tabs;
  }

  public ngOnInit() {
    LogUtil.debug('ReportsComponent ngOnInit');
    this.onRefreshHandler();
  }

  protected tabSelected(idx:number) {
    this.selectedTab = idx;
    this.validateCurrentTabForm();
  }

  protected onNewTabHandler() {

    if (this.selectedReport != null) {

      let descriptor:ReportDescriptorVO = this.reports.find(report => {
        return report.reportId == this.selectedReport;
      });

      if (descriptor != null) {

        let lang = I18nEventBus.getI18nEventBus().current();
        let request:ReportRequestVO = { reportId: descriptor.reportId, lang: lang, parameters: [] };

        descriptor.parameters.forEach(param => {
          let rp:ReportRequestParameterVO = {
            parameterId: param.parameterId,
            options: [],
            value: null,
            businesstype: param.businesstype,
            mandatory : param.mandatory
          };
          request.parameters.push(rp);
        });

        let _sub:any = this._reportsService.updateReportRequestValues(request).subscribe((res:ReportRequestVO) => {

          LogUtil.debug('ReportsComponent request', res);

          let _tab:QueryTabData = { descriptor: descriptor, request: res, running: false, completed: false, filename: null, success: true };

          this.tabs.push(_tab);

          let that = this;

          Futures.once(function () {
            if (that.reportTabs.tabs.length == that.tabs.length) {
              that.selectedTab = that.tabs.length - 1;
              if (!that.reportTabs.tabs[that.selectedTab].active) {
                that.reportTabs.tabs[that.selectedTab].active = true;
              }
            } else if (that.tabs.length == 1) {
              that.selectedTab = 0;
            }
          }, 50).delay();


          _sub.unsubscribe();

        });


      }

    }

  }

  protected onDateValueChange(event:any, requestParam:ReportRequestParameterVO) {

    LogUtil.debug('ReportsComponent date change', event);

    if (event.valid) {

      requestParam.value = event.source;

      this.validateCurrentTabForm();

    }

  }

  protected onDataChange(event:any) {

    LogUtil.debug('ReportsComponent data change', event);

    this.validateCurrentTabForm();

  }

  protected validateCurrentTabForm() {

    let data:QueryTabData = this.tabs[this.selectedTab];

    LogUtil.debug('ReportsComponent validating tab', data);

    let valid = data != null && !data.running && !data.completed;
    if (valid) {
      data.request.parameters.forEach((param:ReportRequestParameterVO) => {
        if (param.mandatory && (param.value == null || param.value == '')) {
          valid = false;
        }
      });
    }

    this.runnableReportTab = valid;

    LogUtil.debug('ReportsComponent tab is valid', this.runnableReportTab);

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
    LogUtil.debug('ReportsComponent Run handler');

    let data = this.tabs[this.selectedTab];
    data.running = true;

    let _sub:any = this._reportsService.generateReport(data.request).subscribe(res => {

      LogUtil.debug('ReportsComponent res', res);

      data.running = false;
      data.filename = res;
      data.success = res != null && res != '';
      data.completed = data.success;
      if (data.success) {
        window.postMessage(res, '*');
      }
      _sub.unsubscribe();
      this.validateCurrentTabForm();

    });

  }

  protected onDownloadClick() {

    let data = this.tabs[this.selectedTab];
    LogUtil.debug('ReportsComponent onDownloadClick handler', data);

    if (data.completed) {
      this.fileFilter = data.filename;
      this.selectFileModalDialog.show();
    }

  }

  protected onFileSelect(file:Pair<string, string>) {
    LogUtil.debug('ReportsComponent onFileSelect', file);
    this.selectedFile = file;
  }


  protected onFilesConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ReportsComponent onFilesConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.selectedFile != null) {

        let nocache = '&nocache=' + Math.random();
        window.open(Config.CONTEXT_PATH + '/service/filemanager/download?fileName=' + encodeURI(this.selectedFile.first) + nocache, '_blank', 'width=300,height=100');

      }
    }
    this.selectedFile = null;
  }


  protected onRefreshHandler() {
    LogUtil.debug('ReportsComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getReportInfo();
    }
  }

  /**
   * Read attributes.
   */
  private getReportInfo() {
    LogUtil.debug('ReportsComponent get reports');

    let _sub:any = this._reportsService.getReportDescriptors().subscribe(reports => {

      LogUtil.debug('ReportsComponent reports', reports);
      this.reports = reports;
      this.selectedReport = this.reports[0].reportId;
      _sub.unsubscribe();

    });

  }

}

interface QueryTabData {

  descriptor : ReportDescriptorVO;
  request : ReportRequestVO;
  running : boolean;
  completed : boolean;
  filename : string;
  success : boolean;

}

