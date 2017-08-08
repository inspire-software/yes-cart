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
import { Router } from '@angular/router';
import { UserVO } from '../model/index';
import { UserEventBus } from '../services/index';
import { ShopVO } from '../model/index';
import { LogUtil } from '../log/index';
import { Config } from '../index';

@Component({
  selector: 'yc-sidebar',
  moduleId: module.id,
  templateUrl: 'sidebar.component.html',
})

export class SidebarComponent implements OnInit, OnDestroy {

  private currentUserName : string;
  private menuType : string;
  private docLink : string;
  private copyNote : string;
  private envLabel : string;

  private userSub:any;

  constructor (private _router : Router) {
    LogUtil.debug('SidebarComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('SidebarComponent ngOnInit');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.configureUser(UserEventBus.getUserEventBus().current());
    }
    this.userSub = UserEventBus.getUserEventBus().userUpdated$.subscribe(user => {
      this.configureUser(user);
    });
  }

  protected configureUser(user:any) {
    let currentUser:UserVO = user;
    this.currentUserName = currentUser != null ? currentUser.name : 'anonymous';
    this.menuType = currentUser != null ? currentUser.ui : 'FULL';
    this.docLink = Config.UI_DOC_LINK;
    this.copyNote = Config.UI_COPY_NOTE;
    this.envLabel = Config.UI_LABEL;
  }

  protected selectNewShop() {

    this._router.navigate(['/shop', 'new_' + Math.random()]);

  }

  protected selectCurrentShop(shop:ShopVO) {

    if (shop.masterCode != null) {
      this._router.navigate(['/subshop', shop.shopId]);
    } else {
      this._router.navigate(['/shop', shop.shopId]);
    }

  }

  protected selectCurrentShopContent(shop:ShopVO) {

    if (shop.masterCode != null) {
      this._router.navigate(['/content', shop.masterId]);
    } else {
      this._router.navigate(['/content', shop.shopId]);
    }

  }

  ngOnDestroy() {
    LogUtil.debug('SidebarComponent ngOnDestroy');
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
  }

}
