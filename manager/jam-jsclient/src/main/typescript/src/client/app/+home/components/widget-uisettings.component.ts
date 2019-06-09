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
import { Component, Input } from '@angular/core';
import { DashboardWidgetVO } from '../../shared/model/index';
import { I18nEventBus } from '../../shared/services/index';
import { Config } from '../../shared/config/env.config';
import { CookieUtil } from '../../shared/cookies/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-widget-uisettings',
  template: `
    <div class="col-lg-3 col-md-4 col-sm-6">
      <div class="panel panel-primary">
        <div class="panel-heading widget-body">
          <div class="row">
            <div class="col-xs-3">
              <i class="fa fa-gears fa-5x"></i>
            </div>
            <div class="col-xs-9 text-right">
              <div><a class="js-click" style="color: #fff" (click)="onChangeSettingClick('UI_TABLE_PAGE_SIZE')">{{ 'PANEL_UI_SETTINGS_PAGE_SIZE' | translate: { value: uiPagination }  }}</a></div>
              <div><a class="js-click" style="color: #fff" (click)="onChangeSettingClick('UI_FILTER_CAP')">{{ 'PANEL_UI_SETTINGS_FILTER_CAP' | translate: { value: uiFilteMax }  }}</a></div>
              <div><a class="js-click" style="color: #fff" (click)="onChangeSettingClick('UI_FILTER_NO_CAP')">{{ 'PANEL_UI_SETTINGS_FILTER_NO_CAP' | translate: { value: uiNoFilteMax }  }}</a></div>
              <div><a class="js-click" style="color: #fff" (click)="onChangeLanguageClick()">{{ 'PANEL_UI_SETTINGS_LOCALE' | translate: { value: uiLang }  }}</a></div>
            </div>
          </div>
        </div>
        <a>
          <div class="panel-footer">
            <span class="pull-left">{{ 'PANEL_UI_SETTINGS' | translate }}</span>
            <form class="pull-right" [hidden]="uiSettingReadOnly"><input type="text" class="form-control" style="width: 80px;" name="uiSetting" [(ngModel)]="uiSetting" /></form>
            <form class="pull-right" [hidden]="uiLangReadOnly"><select type="text" class="form-control" style="width: 80px;" name="uiSetting" [(ngModel)]="uiLang">
              <option value="de">de</option>
              <option value="en">en</option>
              <option value="ru">ru</option>
              <option value="uk">uk</option>
            </select></form>
            <div class="clearfix"></div>
          </div>
        </a>
      </div>
    </div>
  `
})
export class WidgetUiSettingsComponent {

  @Input() widget: DashboardWidgetVO;

  private uiLangReadOnly:boolean = true;
  private uiSettingReadOnly:boolean = true;
  private uiSettingProperty:string = '';

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
      CookieUtil.createCookie('ADM_UI_LANG', lang, 360);
    }
  }

  get uiSetting():number {
    let cfg:any = Config;
    return cfg[this.uiSettingProperty];
  }

  set uiSetting(value:number) {
    let cfg:any = Config;
    cfg[this.uiSettingProperty] = value;
    let cookieName = 'ADM_' + this.uiSettingProperty;
    CookieUtil.createCookie(cookieName, ''+value, 360);
  }


}
