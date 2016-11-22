import { Component, OnInit } from '@angular/core';
import { REACTIVE_FORM_DIRECTIVES } from '@angular/forms';
import { ROUTER_DIRECTIVES } from '@angular/router';
import { DashboardWidgetVO } from '../shared/model/index';
import { ReportsService } from '../shared/services/index';
import { Config } from '../shared/config/env.config';
import { CookieUtil } from '../shared/cookies/index';


/**
 * This class represents the lazy loaded HomeComponent.
 */
@Component({
  moduleId: module.id,
  selector: 'yc-home',
  templateUrl: 'home.component.html',
  styleUrls: ['home.component.css'],
  directives: [REACTIVE_FORM_DIRECTIVES, ROUTER_DIRECTIVES]
})
export class HomeComponent implements OnInit {

  private static _widgets:any = {};

  uiSettingReadOnly:boolean = true;
  uiSettingProperty:string = '';

  constructor(private _dashboardService:ReportsService) {
    console.debug('HomeComponent constructed');
  }

  public get widgets():any {
    return HomeComponent._widgets;
  }

  public set widgets(value:any) {
    HomeComponent._widgets = value;
  }

  ngOnInit() {
    console.debug('HomeComponent ngOnInit');

    this.onRefreshHandler();
  }

  onRefreshHandler() {

    var _sub:any = this._dashboardService.getDashboard().subscribe((widgets:DashboardWidgetVO[]) => {

      console.debug('HomeComponent getDashboard', widgets);

      this.widgets = {};
      widgets.forEach(widget => {
        this.widgets[widget.widgetId] = widget;
      });

      console.debug('HomeComponent widgets', this.widgets);

      _sub.unsubscribe();

    });

  }

  onChangeSettingClick(setting:string) {
    console.debug('HomeComponent modify', this.uiSettingProperty, setting);
    this.uiSettingReadOnly = this.uiSettingProperty === setting;
    this.uiSettingProperty = this.uiSettingReadOnly ? '' : setting;
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
