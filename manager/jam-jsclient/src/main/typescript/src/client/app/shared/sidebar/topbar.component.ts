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
import { Component,  OnInit, OnDestroy } from '@angular/core';
import { UserVO, DashboardWidgetVO } from '../model/index';
import { I18nEventBus, UserEventBus, SystemService, ReportsService } from '../services/index';
import { Futures, Future } from '../event/index';
import { LogUtil } from '../log/index';
import { Config } from '../index';

@Component({
  selector: 'yc-topbar',
  moduleId: module.id,
  templateUrl: 'topbar.component.html',
})

export class TopbarComponent implements OnInit, OnDestroy {

  private currentUserName : string;
  private currentUserEmail : string;
  private menuType : string;
  private envLabel : string;

  private userSub:any;

  private evictingCache:boolean = false;
  private hasAlerts:boolean = false;
  private continuePolling:boolean = true;

  private delayedAlerts:Future;
  private delayedAlertsMs:number = Config.UI_ALERTCHECK_DELAY;

  constructor (private _systemService:SystemService,
               private _dashboardService:ReportsService) {
    LogUtil.debug('TopbarComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('TopbarComponent ngOnInit');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.configureUser(UserEventBus.getUserEventBus().current());
    }
    this.userSub = UserEventBus.getUserEventBus().userUpdated$.subscribe(user => {
      this.configureUser(user);
    });
    let that = this;
    this.delayedAlerts = Futures.perpetual(function() {
      if (that.continuePolling) {
        that.checkAlerts();
        that.continuePolling = false; // disable polling
      }
    }, this.delayedAlertsMs);
    this.delayedAlerts.delay(3000);
    let _pollingRefresh = function() {
      if (!that.continuePolling) {
        that.continuePolling = true; // enable polling if user interaction detected
        LogUtil.debug('TopbarComponent user interaction detected, continue polling');
      }
    };
    document.addEventListener('mousemove', _pollingRefresh);
  }

  protected configureUser(user:any) {
    let currentUser:UserVO = user;
    this.currentUserName = currentUser != null ? currentUser.name : 'anonymous';
    this.currentUserEmail = currentUser != null && currentUser.manager != null ? currentUser.manager.email : 'anonymous';
    this.menuType = currentUser != null ? currentUser.ui : {
      'CCC': false,
      'PIM': false,
      'CMS': false,
      'MRK': false,
      'INV': false,
      'REP': false,
      'SHP': false,
      'SHO': false,
      'ORG': false,
      'SYS': false
    };
    this.envLabel = Config.UI_LABEL;
  }

  ngOnDestroy() {
    LogUtil.debug('TopbarComponent ngOnDestroy');
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
  }

  onCacheEvictAll() {
    LogUtil.debug('TopbarComponent onCacheEvictAll');
    this.evictingCache = true;
    let _sub:any = this._systemService.evictAllCache().subscribe(caches => {
      LogUtil.debug('TopbarComponent evictAllCache', caches);
      this.evictingCache = false;
      _sub.unsubscribe();
    });
  }

  checkAlerts() {

    let lang = I18nEventBus.getI18nEventBus().current();
    let _sub:any = this._dashboardService.getDashboardWidget('Alerts', lang).subscribe((widgets:DashboardWidgetVO[]) => {

      LogUtil.debug('TopbarComponent getDashboard', widgets);

      widgets.forEach(widget => {
        this.hasAlerts = widget.data.length > 0;
      });

      LogUtil.debug('TopbarComponent widgets', this.hasAlerts);

      _sub.unsubscribe();

      this.delayedAlerts.delay();

    });
  }

}
