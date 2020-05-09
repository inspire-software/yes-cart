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
import { Router } from '@angular/router';
import { Component, Input } from '@angular/core';
import { DashboardWidgetVO } from '../../shared/model/index';

@Component({
  selector: 'yc-widget-customers',
  template: `
    <div class="col-lg-3 col-md-4 col-sm-6">
      <div class="panel panel-primary">
        <div class="panel-heading widget-body">
          <div class="row">
            <div class="col-xs-3">
              <i class="fa fa-user fa-5x"></i>
            </div>
            <div class="col-xs-9 text-right">
              <div>{{ 'DASHBOARD_CUSTOMERS_TODAY' | translate }} {{ widget.data.customersToday }}</div>
              <div>{{ 'DASHBOARD_CUSTOMERS_THIS_WEEK' | translate }} {{ widget.data.customersThisWeek }}</div>
              <div>{{ 'DASHBOARD_CUSTOMERS_THIS_MONTH' | translate }} {{ widget.data.customersThisMonth }}</div>
            </div>
          </div>
        </div>
        <a (click)="onCustomerClick('regthismth')" class="js-click">
          <div class="panel-footer">
            <span class="pull-left">{{ 'DASHBOARD_CUSTOMERS_DETAILS' | translate }}</span>
            <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
            <div class="clearfix"></div>
          </div>
        </a>
      </div>
    </div>
  `
})
export class WidgetCustomersComponent {

  @Input() widget: DashboardWidgetVO;

  constructor(private _router : Router) {

  }

  onCustomerClick(filter:string = 'new') {

    this._router.navigate(['/customer/allcustomers'], { queryParams: { filter: filter } });

  }

}
