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
import { Component,  OnInit, OnDestroy } from '@angular/core';
import { UserVO, DashboardWidgetVO } from '../model/index';
import { I18nEventBus, UserEventBus, CommandEventBus, SystemService, ReportsService, ManagementService } from '../services/index';
import { Futures, Future } from '../event/index';
import { LogUtil } from '../log/index';
import { Config } from '../../../environments/environment';

@Component({
  selector: 'cw-topbar',
  templateUrl: 'topbar.component.html',
})

export class TopbarComponent implements OnInit, OnDestroy {

  public currentUser:UserVO = null;
  public currentUserName : string;
  public currentUserEmail : string;
  public menuType : any;
  public envLabel : string;

  private userSub:any;
  private alertSub:any;

  private evictingCache:boolean = false;
  public hasAlerts:boolean = false;
  private continuePolling:boolean = false;

  private delayedAlerts:Future;
  private delayedAlertsMs:number = Config.UI_ALERTCHECK_DELAY;

  constructor (private _systemService:SystemService,
               private _dashboardService:ReportsService,
               private _managementService:ManagementService) {
    LogUtil.debug('TopbarComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('TopbarComponent ngOnInit');

    let that = this;
    this.delayedAlerts = Futures.perpetual(function() {
      if (that.continuePolling) {
        that.checkAlerts();
        that.continuePolling = false; // disable polling until we get activity notification
      }
      that.delayedAlerts.delay();
    }, this.delayedAlertsMs);

    if (UserEventBus.getUserEventBus().current() != null) {
      this.configureUser(UserEventBus.getUserEventBus().current());
    }

    this.userSub = UserEventBus.getUserEventBus().userUpdated$.subscribe(user => {
      this.configureUser(user);
    });

    this.alertSub = UserEventBus.getUserEventBus().activeUpdated$.subscribe(active => {
      if (active && !that.continuePolling) {
        that.continuePolling = (that.currentUser != null); // enable polling if user interaction detected
        if (that.continuePolling) {
          LogUtil.debug('TopbarComponent user interaction detected, continue polling');
        }
      }
    });

  }

  configureUser(user:any) {
    this.currentUser = user;
    this.currentUserName = this.currentUser != null ? this.currentUser.name : 'anonymous';
    this.currentUserEmail = this.currentUser != null && this.currentUser.manager != null ? this.currentUser.manager.email : 'anonymous';
    this.menuType = this.currentUser != null ? this.currentUser.ui : {
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

    if (this.currentUser != null) {
      this.delayedAlerts.delay();
    } else {
      this.delayedAlerts.cancel();
    }

  }

  ngOnDestroy() {
    LogUtil.debug('TopbarComponent ngOnDestroy');
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
    if (this.alertSub) {
      this.alertSub.unsubscribe();
    }
  }

  onCacheEvictAll() {
    LogUtil.debug('TopbarComponent onCacheEvictAll');
    this.evictingCache = true;
    this._systemService.evictAllCache().subscribe(caches => {
      LogUtil.debug('TopbarComponent evictAllCache', caches);
      this.evictingCache = false;
    });
  }

  onChangePassword() {
    LogUtil.debug('TopbarComponent onChangePassword');
    CommandEventBus.getCommandEventBus().emit('changepassword');
  }

  onLogOutClick() {
    LogUtil.debug('TopbarComponent onLogOutClick');
    CommandEventBus.getCommandEventBus().emit('logout');
  }

  checkAlerts() {

    this._managementService.refreshToken().subscribe(res => {

      LogUtil.debug('TopbarComponent refreshToken', res);

      let lang = I18nEventBus.getI18nEventBus().current();
      this._dashboardService.getDashboardWidget('Alerts', lang).subscribe((widgets:DashboardWidgetVO[]) => {

        LogUtil.debug('TopbarComponent getDashboard', widgets);

        widgets.forEach(widget => {
          this.hasAlerts = widget.data.length > 0;
        });

        LogUtil.debug('TopbarComponent widgets', this.hasAlerts);

      });
    });
  }

}
