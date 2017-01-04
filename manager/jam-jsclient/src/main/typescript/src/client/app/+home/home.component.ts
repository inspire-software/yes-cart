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
import { DashboardWidgetVO } from '../shared/model/index';
import { ReportsService, I18nEventBus } from '../shared/services/index';
import { Config } from '../shared/config/env.config';
import { CookieUtil } from '../shared/cookies/index';
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

  private uiLangReadOnly:boolean = true;
  private uiSettingReadOnly:boolean = true;
  private uiSettingProperty:string = '';

  private loading:boolean = false;

  constructor(private _dashboardService:ReportsService) {
    LogUtil.debug('HomeComponent constructed');
  }

  public get widgets():any {
    return HomeComponent._widgets;
  }

  public set widgets(value:any) {
    HomeComponent._widgets = value;
  }

  ngOnInit() {
    LogUtil.debug('HomeComponent ngOnInit');

    this.onRefreshHandler();
  }

  onRefreshHandler() {

    this.loading = true;
    var _sub:any = this._dashboardService.getDashboard().subscribe((widgets:DashboardWidgetVO[]) => {

      LogUtil.debug('HomeComponent getDashboard', widgets);

      this.widgets = {};
      widgets.forEach(widget => {
        this.widgets[widget.widgetId] = widget;
      });

      LogUtil.debug('HomeComponent widgets', this.widgets);

      this.loading = false;
      _sub.unsubscribe();

    });

  }

  onChangeSettingClick(setting:string) {
    LogUtil.debug('HomeComponent modify', this.uiSettingProperty, setting);
    this.uiSettingReadOnly = this.uiSettingProperty === setting;
    this.uiSettingProperty = this.uiSettingReadOnly ? '' : setting;
    if (!this.uiSettingReadOnly) {
      this.uiLangReadOnly = true; // ensure lang is hidden
    }
  }

  onChangeLanguageClick() {
    LogUtil.debug('HomeComponent modify lang', this.uiLangReadOnly);
    this.uiSettingReadOnly = true;   // ensure setting is hidden
    this.uiSettingProperty = '';
    this.uiLangReadOnly = !this.uiLangReadOnly;
  }

  get uiPagination():number {
    return Config.UI_TABLE_PAGE_SIZE;
  }

  set uiPagination(pageSize:number) {
    Config.UI_TABLE_PAGE_SIZE = pageSize;
  }

  get uiFilteMax():number {
    return Config.UI_FILTER_CAP;
  }

  set uiFilteMax(maxResults:number) {
    Config.UI_FILTER_CAP = maxResults;
  }

  get uiNoFilteMax():number {
    return Config.UI_FILTER_NO_CAP;
  }

  set uiNoFilteMax(maxResults:number) {
    Config.UI_FILTER_NO_CAP = maxResults;
  }

  get uiLang():string {
      return I18nEventBus.getI18nEventBus().current();
  }

  set uiLang(lang:string) {
    if (['uk','ru','en','de'].indexOf(lang) !== -1) {
      I18nEventBus.getI18nEventBus().emit(lang);
      CookieUtil.createCookie('YCJAM_UI_LANG', lang, 360);
    }
  }

  get uiSetting():number {
    let cfg:any = Config;
    return cfg[this.uiSettingProperty];
  }

  set uiSetting(value:number) {
    let cfg:any = Config;
    cfg[this.uiSettingProperty] = value;
    let cookieName = 'YCJAM_' + this.uiSettingProperty;
    CookieUtil.createCookie(cookieName, ''+value, 360);
  }

}
