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
import { ReportDescriptorVO, ReportRequestVO, ReportRequestParameterVO, Pair } from './../shared/model/index';
import { ShopVO, FulfilmentCentreInfoVO } from './../shared/model/index';
import { FormValidationEvent } from './../shared/event/index';
import { ReportsService, ImpexService, I18nEventBus, UserEventBus } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { FulfilmentCentreSelectComponent } from "./../shared/fulfilment/index";
import { Futures, Future } from './../shared/event/index';
import { Config } from './../../environments/environment';
import { LogUtil } from './../shared/log/index';
import { UiUtil } from './../shared/ui/uiutil';


@Component({
  selector: 'cw-reports',
  templateUrl: 'reports.component.html',
})

export class ReportsComponent implements OnInit {

  private static tabs:Array<QueryTabData> = [];

  public reports:Array<ReportDescriptorVO> = [];
  public filteredReports:Array<ReportDescriptorVO> = [];
  public reportFilter:string = null;

  public runnableReportTab:boolean = false;
  public selectedReport:ReportDescriptorVO = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public selectedTab:number = 0;

  public fileFilter:string = null;
  public selectedFile:Pair<string,string> = null;

  @ViewChild('selectFileModalDialog')
  private selectFileModalDialog:ModalComponent;

  @ViewChild('selectShopModalDialog')
  private selectShopModalDialog:ModalComponent;

  @ViewChild('selectReportModalDialog')
  private selectReportModalDialog:ModalComponent;

  @ViewChild('selectCentreModalDialog')
  private selectCentreModalDialog:FulfilmentCentreSelectComponent;

  private selectedParam:ReportRequestParameterVO = null;
  public selectedShop:ShopVO = null;

  @ViewChild('reportTabs')
  private reportTabs:TabsetComponent;

  /**
   * Construct reports panel
   *
   * @param _reportsService reports service
   */
  constructor(private _reportsService: ReportsService,
              private _fileService : ImpexService) {
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
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.reloadReportList();
    }, this.delayedFilteringMs);

  }

  tabSelected(idx:number) {
    this.selectedTab = idx;
    this.validateCurrentTabForm();
  }

  onNewTabHandler() {

    if (this.selectedReport != null) {

      let descriptor:ReportDescriptorVO = this.selectedReport;

      if (descriptor != null) {

        let lang = I18nEventBus.getI18nEventBus().current();
        let request:ReportRequestVO = {
          reportId: descriptor.reportId,
          displayNames : descriptor.displayNames,
          parameters: [],
          reportType : descriptor.reportType,
          reportFileExtension : descriptor.reportFileExtension,
          reportFileMimeType : descriptor.reportFileMimeType,
          lang: lang
        };

        descriptor.parameters.forEach(param => {
          let rp:ReportRequestParameterVO = {
            parameterId: param.parameterId,
            displayNames: param.displayNames,
            options: [],
            value: null,
            displayValue: null,
            businesstype: param.businesstype,
            mandatory : param.mandatory,
            editorType: param.editorType,
            editorProperty: param.editorProperty,
            displayProperty: param.displayProperty
          };
          request.parameters.push(rp);
        });

        LogUtil.debug('ReportsComponent onNewTabHandler', descriptor, request);

        this._reportsService.updateReportRequestValues(request).subscribe((res:ReportRequestVO) => {

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

        });


      }

    }

  }

  onDateValueChange(event:any, requestParam:ReportRequestParameterVO) {

    LogUtil.debug('ReportsComponent date change', event);

    if (event.valid) {

      requestParam.value = event.source;

      this.validateCurrentTabForm();

    }

  }

  onDataChange(event:any) {

    LogUtil.debug('ReportsComponent data change', event);

    let data:QueryTabData = this.tabs[this.selectedTab];
    data.completed = false;

    this.validateCurrentTabForm();

  }

  validateCurrentTabForm() {

    let data:QueryTabData = this.tabs[this.selectedTab];

    LogUtil.debug('ReportsComponent validating tab', data);

    let valid = data != null && !data.running;
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

  onTabDeleteTab(tab:QueryTabData) {
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

  onRunHandler() {
    LogUtil.debug('ReportsComponent Run handler');

    let data = this.tabs[this.selectedTab];
    data.running = true;

    this._reportsService.generateReport(data.request).subscribe(res => {

      LogUtil.debug('ReportsComponent res', res);

      data.running = false;
      data.filename = res;
      data.success = res != null && res != '';
      data.completed = data.success;
      this.validateCurrentTabForm();

    });

  }

  onDownloadClick() {

    let data = this.tabs[this.selectedTab];
    LogUtil.debug('ReportsComponent onDownloadClick handler', data);

    if (data.completed) {
      this.fileFilter = data.filename;

      this.downloadSingleFile(this.fileFilter);

    }

  }

  onNewReportClick() {
    if (this.reports.length == 0) {
      this.onRefreshHandler();
    }
    this.selectReportModalDialog.show();
  }

  onFileSelect(file:Pair<string, string>) {
    LogUtil.debug('ReportsComponent onFileSelect', file);
    this.selectedFile = file;
  }


  onFileDownload() {
    LogUtil.debug('ReportsComponent onFileSelect');
    this.selectFileModalDialog.show();
  }

  onFilesConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ReportsComponent onFilesConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.selectedFile != null) {

        this._reportsService.downloadReport(this.selectedFile.first).subscribe(res => {
          LogUtil.debug('ReportsComponent downloaded report', this.selectedFile, res);
        });

      }
    }
    this.selectedFile = null;
  }


  onSelectClick(report: ReportDescriptorVO) {
    LogUtil.debug('ReportsComponent onSelectClick', report);
    this.selectedReport = report;
  }

  onRefreshHandler() {
    LogUtil.debug('ReportsComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getReportInfo();
    }
  }

  onFilterChange() {

    this.delayedFiltering.delay();

  }

  onClearFilter() {
    this.reportFilter = '';
    this.delayedFiltering.delay();
  }

  getReportName(report:ReportDescriptorVO):string {

    let lang = I18nEventBus.getI18nEventBus().current();
    let i18n = report.displayNames;
    let def = report.reportId;

    return UiUtil.toI18nString(i18n, def, lang);

  }

  getReportParamName(param:ReportRequestParameterVO):string {

    let lang = I18nEventBus.getI18nEventBus().current();
    let i18n = param.displayNames;
    let def = param.parameterId;

    return UiUtil.toI18nString(i18n, def, lang);

  }

  onEditParameterValueClick(param:ReportRequestParameterVO) {

    LogUtil.debug('ReportsComponent onEditParameterValueClick', param);

    switch (param.editorType) {
      case 'FulfilmentCentreSelect':
        this.selectedParam = param;
        this.selectCentreModalDialog.showDialog();
        break;
      case 'ShopSelect':
        this.selectedParam = param;
        this.selectShopModalDialog.show();
        break;
    }

  }

  onShopSelected(event:ShopVO) {
    LogUtil.debug('ReportsComponent onShopSelected', event);
    this.selectedShop = event;
  }

  onSelectShopResult(modalresult: ModalResult) {
    LogUtil.debug('ReportsComponent onSelectShopResult modal result is ', modalresult, this.selectedShop);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.selectedShop != null) {
        this.selectedParam.value = this.selectedShop[this.selectedParam.editorProperty];
        this.selectedParam.displayValue = this.selectedShop[this.selectedParam.displayProperty];
      } else {
        this.selectedParam.value = null;
        this.selectedParam.displayValue = null;
      }
      LogUtil.debug('ReportsComponent onSelectShopResult selectedParam is ', this.selectedParam);
      this.onDataChange(null);
    }
  }

  onFulfilmentCentreSelected(event:FormValidationEvent<FulfilmentCentreInfoVO>) {
    LogUtil.debug('ReportsComponent onFulfilmentCentreSelected', event);
    if (event.valid) {
      this.selectedParam.value = event.source[this.selectedParam.editorProperty];
      this.selectedParam.displayValue = event.source[this.selectedParam.displayProperty];
    } else {
      this.selectedParam.value = null;
      this.selectedParam.displayValue = null;
    }
    LogUtil.debug('ReportsComponent onSelectShopResult selectedParam is ', this.selectedParam);
    this.onDataChange(null);
  }

  onSelectReportResult(modalresult: ModalResult) {
    LogUtil.debug('ReportsComponent onSelectReportResult modal result is ', modalresult, this.selectedReport);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.selectedReport != null) {
        this.onNewTabHandler();
      }
    }
  }


  /**
   * Read attributes.
   */
  private getReportInfo() {
    LogUtil.debug('ReportsComponent get reports');

    let lang = I18nEventBus.getI18nEventBus().current();

    this._reportsService.getReportDescriptors(lang).subscribe(reports => {

      LogUtil.debug('ReportsComponent reports', reports);
      this.reports = reports;
      this.selectedReport = this.reports[0];
      this.reloadReportList();

    });

  }

  private reloadReportList() {

    if (this.reports != null) {
      if (this.reportFilter) {
        let _filter = this.reportFilter.toLowerCase();
        this.filteredReports = this.reports.filter(report =>
          this.getReportName(report).toLowerCase().indexOf(_filter) !== -1
        );
        LogUtil.debug('ReportsComponent reloadReportList filter: ' + _filter, this.filteredReports);
      } else {
        this.filteredReports = this.reports;
        LogUtil.debug('ReportsComponent reloadReportList no filter', this.filteredReports);
      }
    }

  }

  private downloadSingleFile(fileName:string) {
    this._fileService.getFiles('export').subscribe(list => {

      LogUtil.debug('ReportsComponent res reading list', list);

      let _file = list.find(file => file.first.indexOf(fileName) != -1);
      if (_file != null) {

        LogUtil.debug('ReportsComponent res downloading', _file);

        this._reportsService.downloadReport(_file.first).subscribe(downloaded => {

          LogUtil.debug('ReportsComponent res downloaded', _file, downloaded);

        });
      }
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

