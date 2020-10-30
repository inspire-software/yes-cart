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
import { Router } from '@angular/router';
import { Component, Input } from '@angular/core';
import { DashboardWidgetVO } from '../../shared/model/index';

@Component({
  selector: 'cw-widget-orders',
  template: `
    <div class="col-lg-3 col-md-4 col-sm-6">
      <div class="panel panel-yellow">
        <div class="panel-heading widget-body">
          <div class="row">
            <div class="col-xs-3 js-click">
              <i class="fa fa-shopping-cart fa-5x"></i>
            </div>
            <div class="col-xs-9 text-right">
              <div>{{ 'DASHBOARD_ORDERS_TODAY' | translate }} {{ widget.data.ordersToday }}</div>
              <div>{{ 'DASHBOARD_ORDERS_THIS_WEEK' | translate }} {{ widget.data.ordersThisWeek }}</div>
              <div>{{ 'DASHBOARD_ORDERS_THIS_MONTH' | translate }} {{ widget.data.ordersThisMonth }}</div>
            </div>
          </div>
        </div>
        <a (click)="onOrdersClick('ordnew')" class="js-click">
          <div class="panel-footer">
            <span class="pull-left">{{ 'DASHBOARD_ORDERS_DETAILS' | translate }}</span>
            <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
            <div class="clearfix"></div>
          </div>
        </a>
      </div>
    </div>

    <div class="col-lg-3 col-md-4 col-sm-6">
      <div class="panel {{ widget.data.ordersWaiting > 0 ? 'panel-red' : 'panel-yellow'}}">
        <div class="panel-heading widget-body">
          <div class="row">
            <div class="col-xs-3">
              <i class="fa fa-shopping-cart fa-5x"></i>
            </div>
            <div class="col-xs-9 text-right">
              <div class="huge">{{ widget.data.ordersWaiting }}</div>
              <div>{{ 'DASHBOARD_ORDERS_WAITING' | translate }}</div>
            </div>
          </div>
        </div>
        <a (click)="onOrdersClick('ordwait')" class="js-click">
          <div class="panel-footer">
            <span class="pull-left">{{ 'DASHBOARD_ORDERS_DETAILS' | translate }}</span>
            <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
            <div class="clearfix"></div>
          </div>
        </a>
      </div>
    </div>

    <div class="col-lg-3 col-md-4 col-sm-6">
      <div class="panel {{ widget.data.deliveriesWaitingDate > 0 || widget.data.deliveriesWaitingInventory > 0 ? 'panel-red' : 'panel-yellow'}}">
        <div class="panel-heading widget-body">
          <div class="row">
            <div class="col-xs-3">
              <i class="fa fa-cubes fa-5x"></i>
            </div>
            <div class="col-xs-9 text-right">
              <div>{{ 'ds.wait.allocation' | translate }} {{ widget.data.deliveriesWaitingAllocation }}</div>
              <div>{{ 'ds.wait.date' | translate }} {{ widget.data.deliveriesWaitingDate }}</div>
              <div>{{ 'ds.wait.inventory' | translate }} {{ widget.data.deliveriesWaitingInventory }}</div>
            </div>
          </div>
        </div>
        <a (click)="onOrdersClick('ordinp')" class="js-click">
          <div class="panel-footer">
            <span class="pull-left">{{ 'DASHBOARD_ORDERS_DETAILS' | translate }}</span>
            <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
            <div class="clearfix"></div>
          </div>
        </a>
      </div>
    </div>
  `
})
export class WidgetOrdersComponent {

  @Input() widget: DashboardWidgetVO;

  constructor(private _router : Router) {

  }

  onOrdersClick(filter:string = 'new') {

    this._router.navigate(['/customerorder/allorders'], { queryParams: { filter: filter } });

  }

}
