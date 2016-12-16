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
import { Component,  OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ManagerInfoVO } from '../model/index';
import { ManagementService } from '../services/index';
import { ShopVO } from '../model/index';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-sidebar',
  moduleId: module.id,
  templateUrl: 'sidebar.component.html',
})

export class SidebarComponent implements OnInit {

  private currentUser : ManagerInfoVO;
  private currentUserName : string;

  constructor (private _managementService : ManagementService,
               private _router : Router) {
    LogUtil.debug('SidebarComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('SidebarComponent ngOnInit');
    var _sub:any = this._managementService.getMyself().subscribe( myself => {
      LogUtil.debug('SidebarComponent getMyself', myself);
      this.currentUser = myself;
      if (this.currentUser.firstName != null && /.*\S+.*/.test(this.currentUser.firstName)) {
        this.currentUserName = this.currentUser.firstName;
      } else if (this.currentUser.lastName != null && /.*\S+.*/.test(this.currentUser.lastName)) {
        this.currentUserName = this.currentUser.lastName;
      } else {
        this.currentUserName = this.currentUser.email;
      }
      _sub.unsubscribe();
    });

  }

  protected selectNewShop() {

    this._router.navigate(['/shop', 'new_' + Math.random()]);

  }

  protected selectCurrentShop(shop:ShopVO) {

    this._router.navigate(['/shop', shop.shopId]);

  }

  protected selectCurrentShopContent(shop:ShopVO) {

    this._router.navigate(['/content', shop.shopId]);

  }

}
