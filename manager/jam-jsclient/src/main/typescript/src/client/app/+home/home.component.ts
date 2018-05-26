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
import { DashboardWidgetVO, DashboardWidgetInfoVO } from '../shared/model/index';
import { ReportsService, I18nEventBus } from '../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from '../shared/modal/index';
import { LogUtil } from './../shared/log/index';


/**
 * This class represents the lazy loaded HomeComponent.
 */
@Component({
  moduleId: module.id,
  selector: 'yc-home',
  templateUrl: 'home.component.html',
})
export class HomeComponent implements OnInit {

  private static _widgets:any = {};
  private static _userWidgets:DashboardWidgetInfoVO[] = [];

  @ViewChild('widgetModalDialog')
  private widgetModalDialog:ModalComponent;

  @ViewChild('widgetEditModalDialog')
  private widgetEditModalDialog:ModalComponent;

  private loading:boolean = false;

  private validForSelect:boolean = false;
  private availableWidgets:DashboardWidgetInfoVO[] = [];
  private filteredAvailableWidgets:DashboardWidgetInfoVO[] = [];

  private widgetFilter:string = null;
  private selectedWidget:DashboardWidgetInfoVO = null;

  private selectedWidgets:DashboardWidgetInfoVO[] = [];

  constructor(private _dashboardService:ReportsService) {
    LogUtil.debug('HomeComponent constructed');
  }

  public get widgets():any {
    return HomeComponent._widgets;
  }

  public set widgets(value:any) {
    HomeComponent._widgets = value;
  }

  public get userWidgets():DashboardWidgetInfoVO[] {
    return HomeComponent._userWidgets;
  }

  public set userWidgets(value:DashboardWidgetInfoVO[]) {
    HomeComponent._userWidgets = value;
  }


  protected onFilterChange() {

    let _filter = this.widgetFilter ? this.widgetFilter.toLowerCase() : null;

    let _filtered:Array<DashboardWidgetInfoVO> = [];
    if (_filter) {
      _filtered = this.availableWidgets.filter(val =>
          val.widgetId.toLowerCase().indexOf(_filter) !== -1 ||
          val.widgetDescription && val.widgetDescription.toLowerCase().indexOf(_filter) !== -1
      );
    } else {
      _filtered = this.availableWidgets;
    }

    this.filteredAvailableWidgets = _filtered;

  }

  protected onClearFilter() {
    this.widgetFilter = '';
    this.onFilterChange();
  }

  onSelectClick(widget: DashboardWidgetInfoVO) {
    LogUtil.debug('HomeComponent onSelectClick', widget);
    this.selectedWidget = widget;
    this.validForSelect = true;
  }


  ngOnInit() {
    LogUtil.debug('HomeComponent ngOnInit');
    this.onRefreshHandler();
  }

  onAddHandler() {

    this.loading = true;

    let lang = I18nEventBus.getI18nEventBus().current();
    let _sub:any = this._dashboardService.getAvailableWidgets(lang).subscribe((widgets:DashboardWidgetInfoVO[]) => {

      LogUtil.debug('HomeComponent getAvailableWidgets', widgets);

      this.availableWidgets = widgets;
      this.onFilterChange();

      this.widgetModalDialog.show();

      this.loading = false;
      _sub.unsubscribe();

    });

  }


  onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('HomeComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action && this.selectedWidget != null) {

      let update = this.userWidgets.slice(0, this.userWidgets.length);
      update.push(this.selectedWidget);

      LogUtil.debug('HomeComponent onSelectConfirmationResult', this.userWidgets, update);

      this.updateDashboardSettings(update, true);

    }
  }

  updateDashboardSettings(update:DashboardWidgetInfoVO[], refresh:boolean = false) {

    this.loading = true;

    let lang = I18nEventBus.getI18nEventBus().current();
    let _sub:any = this._dashboardService.updateDashboardWidgets(update, lang).subscribe((value:void) => {

      LogUtil.debug('HomeComponent updateDashboardWidgets');

      this.loading = false;
      _sub.unsubscribe();

      if (refresh) {
        this.onRefreshHandler();
      }

    });
  }

  onConfigureSelected() {

    this.selectedWidgets = this.userWidgets.slice(0, this.userWidgets.length);
    this.widgetEditModalDialog.show();

  }

  onConfigureSelectedRemove(widget:DashboardWidgetInfoVO) {

    let idx = this.selectedWidgets.findIndex(val => widget.widgetId == val.widgetId);
    if (idx !== -1) {
      this.selectedWidgets.splice(idx, 1);
      this.selectedWidgets = this.selectedWidgets.slice(0, this.selectedWidgets.length);
    }

  }

  onConfigureSelectedUp(widget:DashboardWidgetInfoVO) {

    let idx = this.selectedWidgets.findIndex(val => widget.widgetId == val.widgetId);
    if (idx !== -1 && idx > 0) {
      let oneUp = this.selectedWidgets[idx - 1];
      this.selectedWidgets[idx - 1] = this.selectedWidgets[idx];
      this.selectedWidgets[idx] = oneUp;
      this.selectedWidgets = this.selectedWidgets.slice(0, this.selectedWidgets.length);
    }

  }

  onConfigureSelectedDown(widget:DashboardWidgetInfoVO) {

    let idx = this.selectedWidgets.findIndex(val => widget.widgetId == val.widgetId);
    if (idx !== -1 && idx < this.selectedWidgets.length - 1) {
      let oneDown = this.selectedWidgets[idx + 1];
      this.selectedWidgets[idx + 1] = this.selectedWidgets[idx];
      this.selectedWidgets[idx] = oneDown;
      this.selectedWidgets = this.selectedWidgets.slice(0, this.selectedWidgets.length);
    }

  }


  onUpdateConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('HomeComponent onUpdateConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      LogUtil.debug('HomeComponent onSelectConfirmationResult', this.userWidgets, this.selectedWidgets);

      this.updateDashboardSettings(this.selectedWidgets, true);

    }
  }


  onRefreshHandler() {

    this.loading = true;

    let lang = I18nEventBus.getI18nEventBus().current();
    let _sub:any = this._dashboardService.getDashboard(lang).subscribe((widgets:DashboardWidgetVO[]) => {

      LogUtil.debug('HomeComponent getDashboard', widgets);

      this.widgets = {};
      this.userWidgets = [];
      widgets.forEach(widget => {
        this.widgets[widget.widgetId] = widget;
        this.userWidgets.push(widget);
      });

      LogUtil.debug('HomeComponent widgets', this.widgets);

      this.loading = false;
      _sub.unsubscribe();

    });

  }
}
